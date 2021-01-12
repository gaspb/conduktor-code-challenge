package gaspb.conduktor.challenge.core


import arrow.fx.coroutines.*
import gaspb.conduktor.challenge.model.KafkaProducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import tornadofx.Controller
import java.security.SecureRandom
import java.util.concurrent.TimeUnit


class KafkaProducerController(private val producer: Producer<String, String>, val conf: KafkaProducer) : Controller() {

    suspend fun getPartitions(topic: String): List<TopicPartition> {
        return producer.partitionsFor(topic).map { a -> TopicPartition(a.topic(), a.partition()) }
    }

    private fun randomString(lgth: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val random = SecureRandom()
        val bytes = ByteArray(lgth)
        random.nextBytes(bytes)
        return (bytes.indices)
            .map { i ->
                charPool[random.nextInt(charPool.size)]
            }.joinToString("")
    }

    fun producerFlow(topic: String, partition: Int?): Flow<Int> {
        return flow {
            var i = 0
            while (currentCoroutineContext().isActive) {
                val key = randomString(6)
                val value = randomString(10)
                if (partition === null) {
                    producer.send(ProducerRecord(topic, key, value))
                } else {
                    producer.send(ProducerRecord(topic, partition, key, value))
                }

                emit(i)
                i += 1
                sleep(Duration(conf.interval, TimeUnit.MILLISECONDS))


            }
        }.flowOn(Dispatchers.IO)
            .onCompletion {
                producer.close()
            }
    }

}
