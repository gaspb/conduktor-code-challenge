package gaspb.conduktor.challenge.core


import gaspb.conduktor.challenge.model.KafkaConsumer
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.TopicPartition
import tornadofx.Controller
import java.time.Duration


class KafkaConsumerController(private val consumer: Consumer<String, String>, val conf: KafkaConsumer) : Controller() {


    data class Record(val value: String, val timestamp: Long)
    suspend fun subscriptionFlow(topic: String): Flow<Record> {

            val partitions = consumer.partitionsFor(topic).map { a -> TopicPartition(a.topic(), a.partition()) }
            consumer.assign(partitions)


        return flow<Record> {

            log.info("isactive" + currentCoroutineContext().isActive)
           // TODO not active
            while(currentCoroutineContext().isActive) {

                val records = consumer.poll(Duration.ofMillis(conf.pollTimeout))
                records.records(topic).forEach {
                    emit(Record(it.value(), it.timestamp()))
                }

            }


            }
        }


}
