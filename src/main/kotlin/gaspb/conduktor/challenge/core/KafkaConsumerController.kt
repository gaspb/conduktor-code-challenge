package gaspb.conduktor.challenge.core


import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.callback
import gaspb.conduktor.challenge.model.KafkaConsumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import tornadofx.Controller
import java.time.Duration


class KafkaConsumerController(private val consumer: Consumer<String, String>, val conf: KafkaConsumer) : Controller() {


    data class Record(val key: String, val value: String, val timestamp: Long)


    fun subscriptionFlow(topic: String): Flow<Record> {

        return flow {
            val partitions = consumer.partitionsFor(topic).map { a -> TopicPartition(a.topic(), a.partition()) }
            consumer.assign(partitions)
            consumer.seekToBeginning(partitions)
            while (currentCoroutineContext().isActive) {
                val records = consumer.poll(Duration.ofMillis(conf.pollTimeout))
                records.records(topic).forEach {
                    emit(Record(it.key(), it.value(), it.timestamp()))
                }
            }
        }.flowOn(Dispatchers.IO)
            .onCompletion {
                consumer.unsubscribe()
                consumer.close()
            }
    }


    // also safe (and more FP), but harder to handle contexts and executors
    fun subscriptionStream(topic: String, acquire: suspend () -> Consumer<String, String>): Stream<Record> {
        return Stream.bracket({ acquire() }, {
            it.unsubscribe()
            it.close()
        })
            .flatMap { cons ->
                Stream.callback<Record> {
                    val partitions = cons.partitionsFor(topic).map { a -> TopicPartition(a.topic(), a.partition()) }
                    cons.assign(partitions)
                    cons.seekToBeginning(partitions)
                    log.info("subscriptionStream dbg isActive " + currentCoroutineContext().isActive)
                    while (currentCoroutineContext().isActive) {

                        val records = consumer.poll(Duration.ofMillis(conf.pollTimeout))
                        records.records(topic).forEach {
                            emit(Record(it.key(), it.value(), it.timestamp()))
                        }
                    }

                }
            }
    }
}
