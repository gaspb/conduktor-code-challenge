package gaspb.conduktor.challenge.core


import arrow.core.*
import arrow.core.computations.either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicative.map
import arrow.core.extensions.list.traverse.traverse
import arrow.fx.IO
import arrow.fx.extensions.fx
import gaspb.conduktor.challenge.model.KafkaBootstrap
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.KafkaAdminClient
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import tornadofx.Controller
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set


class KafkaServiceController : Controller() {

    sealed class ConfigError : Throwable() {
        data class MissingConfig(val field: String) : ConfigError()
        data class ParseConfig(val field: String) : ConfigError()
    }


    private val anyWebUrlRegex =
        "^[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?|^((http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(:[0-9]+)?\$".toRegex()

    private fun validateBrokerUrl(url: String) = Validated.fromEither(
        if (url.matches(anyWebUrlRegex)) Either.right(url)
        else Either.left(ConfigError.ParseConfig("broker"))
    )

    private fun getBrokers(state: KafkaBootstrap): Either<ConfigError, NonEmptyList<String>> {

        return Nel.fromList(state.url.split(",")).fold({
            Either.left(ConfigError.MissingConfig("broker"))
        }) { nonEmptyList ->
            nonEmptyList.traverse(Either.applicative<ConfigError>()) { validateBrokerUrl(it).toEither() }
            Either.right(nonEmptyList)
        }
    }

    // didn't take time to do the regex
    private fun validateAdditionalProperty(prop: String): Validated<ConfigError.ParseConfig, Pair<String, String>> {
        val splited = prop.split("=")
        return Validated.fromEither(
            if (splited.size == 2) Either.right(splited[0] to splited[1])
            else Either.left(ConfigError.ParseConfig("additionalProperty"))
        )
    }

    private fun getAdditionalProperties(state: KafkaBootstrap): Either<ConfigError, Map<String, String>> {
        if (state.additionalProps != null) {
            return state.additionalProps
                .reader()
                .readLines()
                .traverse(Either.applicative<ConfigError>()) { validateAdditionalProperty(it).toEither() }
                .map { it.fix().toMap() }
        } else {
            return Either.right(emptyMap<String, String>())
        }

    }

    private fun buildPropertiesObject(config: Map<String, String>): Properties {
        val properties = Properties()
        config.forEach { (key, value) -> properties[key] = value }
        return properties
    }


    // TODO add more properties
    private suspend fun buildConsumerProperties(
        state: KafkaBootstrap,
        conf: gaspb.conduktor.challenge.model.KafkaConsumer
    ): Either<ConfigError, Properties> = either {
        val brokers = getBrokers(state).bind()
        val additionalProperties = getAdditionalProperties(state).bind()
        val configMap = additionalProperties + mapOf<String, String>(
            "bootstrap.servers" toT brokers.all.joinToString(separator = ","),
            "key.deserializer" toT StringDeserializer::class.java.canonicalName,
            "value.deserializer" toT StringDeserializer::class.java.canonicalName,
        )
        if (conf.enableAutoCommit) {
            val extra = configMap + mapOf<String, String>(
                "enable.auto.commit" toT conf.enableAutoCommit.toString(),
                "group.id" toT conf.groupId
            )
            buildPropertiesObject(extra)
        } else {
            buildPropertiesObject(configMap)
        }

    }

    private suspend fun buildAdminProperties(state: KafkaBootstrap): Either<ConfigError, Properties> = either {
        val brokers = getBrokers(state).bind()
        val additionalProperties = getAdditionalProperties(state).bind()
        val configMap = additionalProperties + mapOf<String, String>(
            "bootstrap.servers" toT brokers.all.joinToString(separator = ","),
            "key.serializer" toT StringSerializer::class.java.canonicalName,
            "value.serializer" toT StringSerializer::class.java.canonicalName
        )
        buildPropertiesObject(configMap)
    }

    fun createAdminClient(state: KafkaBootstrap): IO<Either<ConfigError, AdminClient>> = IO.fx {
        val propsEither = IO.effect { buildAdminProperties(state) }.bind()
        val client = IO.effect { propsEither.map { KafkaAdminClient.create(it) } }.bind()
        client
    }

    fun createKafkaConsumer(
        state: KafkaBootstrap,
        conf: gaspb.conduktor.challenge.model.KafkaConsumer
    ): IO<Either<ConfigError, KafkaConsumer<String, String>>> =
        IO.fx {
            val propsEither = IO.effect { buildConsumerProperties(state, conf) }.bind()
            val client = IO.effect { propsEither.map { KafkaConsumer<String, String>(it) } }.bind()
            client
        }

    fun createKafkaProducer(state: KafkaBootstrap): IO<Either<ConfigError, KafkaProducer<String, String>>> =
        IO.fx {
            val propsEither = IO.effect { buildAdminProperties(state) }.bind()
            val client = IO.effect { propsEither.map { KafkaProducer<String, String>(it) } }.bind()
            client
        }


    // can't call without keeping a state.. (if not resource)
    fun closeConsumer(consumer: Consumer<String, String>): IO<Unit> = IO {
        log.info("CLOSING CONSUMER")
        consumer.unsubscribe()
        consumer.close()
    }

    fun closeAdmin(admin: AdminClient): IO<Unit> = IO {
        log.info("CLOSING ADMIN")
        admin.close()
    }


    // would have been good, but I could not manage to keep the resource open while navigating
    // fun kafkaAdminResource(config: KafkaBootstrap) = Resource({ createAdminClient(config) }, ::closeAdmin, IO.bracket()).fix()
    // fun kafkaConsumerResource(config: KafkaBootstrap) = Resource({ createKafkaConsumer(config) }, ::closeConsumer, IO.bracket()).fix()


}

