package gaspb.conduktor.challenge.core

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.common.KafkaFuture
import tornadofx.Controller
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KafkaAdminController(private val admin: AdminClient) : Controller() {
    private suspend fun <A> kafkaFutureToCoroutine(future: KafkaFuture<A>) =
        suspendCoroutine<A> { continuation ->
            future.whenComplete { a, err ->
                if (a == null) {
                    continuation.resumeWithException(err)
                } else {
                    continuation.resume(a)
                }
            }
        }

    suspend fun listTopics() = kafkaFutureToCoroutine(admin.listTopics().names())

    suspend fun createTopic(name: String, numPartition: Int) {
        admin.createTopics(listOf<NewTopic>(NewTopic(name, Optional.of(numPartition), Optional.empty())))
    }
}
