package gaspb.conduktor.challenge.view

import arrow.fx.IO
import arrow.fx.extensions.fx
import gaspb.conduktor.challenge.core.KafkaConsumerController
import gaspb.conduktor.challenge.core.KafkaServiceController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*


class ConsumerView: View("Consuming") {

    override val scope = super.scope as TopicScope

    private val controller : KafkaServiceController by inject()

    private val entries = mutableListOf<KafkaConsumerController.Record>().asObservable()




    private val job = GlobalScope.launch(Dispatchers.JavaFx) {
        log.info("job ctx isActive "+ currentCoroutineContext().isActive)
        IO.fx {
            val eth = controller.createKafkaConsumer(scope.bootstrapModel.item).bind()
            eth.fold({ err ->
                IO.effect {
                    err //TODO
                }
            }, { succ ->
                IO {
                    val consumerService = KafkaConsumerController(succ, scope.kafkaConsumerModel.item)
                    val flow = consumerService.subscriptionFlow(scope.topicModel.item.name)
                    flow.collect {
                        entries.add(it)
                    }
                    flow
                }
            }).bind()
        }.suspended()
        log.info("wrapper isActive "+ currentCoroutineContext().isActive)
    }

    override fun onDelete() {
        super.onDelete()
        job.cancel()

    }

    override val root = vbox {
        button("Stop") {
            action {
                job.cancel()
            }
        }
        listview(entries){

        }
    }
}