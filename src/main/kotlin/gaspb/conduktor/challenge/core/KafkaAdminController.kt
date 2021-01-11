package gaspb.conduktor.challenge.core

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.common.KafkaFuture
import tornadofx.Controller
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class KafkaAdminController(private val admin: AdminClient) : Controller() {
    private suspend fun <A>kafkaFutureToCoroutine(future: KafkaFuture<A>) =
        suspendCoroutine<A> { continuation ->
            future.whenComplete { a, err ->
                if(a == null) {
                    continuation.resumeWithException(err)
                } else {
                    continuation.resume(a)
                }
            }
        }

    suspend fun listTopics() =
      //  admin.map {
            //client ->
    kafkaFutureToCoroutine(admin.listTopics().names())
        //}


}
