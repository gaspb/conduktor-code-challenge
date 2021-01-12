package gaspb.conduktor.challenge.view

import arrow.core.Either
import arrow.integrations.kotlinx.suspendCancellable
import gaspb.conduktor.challenge.core.KafkaConsumerController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.KafkaConsumerModel
import gaspb.conduktor.challenge.model.TopicModel
import gaspb.conduktor.challenge.view.events.TopicViewUndocked
import gaspb.conduktor.challenge.view.style.Style
import javafx.scene.control.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*


class ConsumerView : Fragment("Consuming") {

    private val topicModel: TopicModel by inject()
    private val bootstrapModel: KafkaBootstrapModel by inject()
    private val kafkaConsumerModel: KafkaConsumerModel by inject()


    private val coroutineScope = MainScope()

    private val controller: KafkaServiceController by inject()

    private val entries = mutableListOf<KafkaConsumerController.Record>().asObservable()

    private val job = coroutineScope.launch(Dispatchers.JavaFx) {
        when (val consumer = controller.createKafkaConsumer(bootstrapModel.item, kafkaConsumerModel.item).suspendCancellable()) {
            is Either.Left -> alert(Alert.AlertType.ERROR,"Failed to create kafka consumer")
            is Either.Right -> {
                val consumerService = KafkaConsumerController(consumer.b, kafkaConsumerModel.item)
                consumerService.subscriptionFlow(topicModel.item.name)
                    .collect {
                        entries.add(it)
                    }
            }

        }
    }

    init {
        subscribe<TopicViewUndocked> {
            job.cancel()
        }
    }

    override val root = vbox {

        style {
            prefHeight = 200.px
            prefWidth = 100.percent
        }
        addClass(Style.blackBorder)
        button("Stop") {
            action {
                job.cancel()
            }
        }
        tableview(entries) {
            readonlyColumn("key", KafkaConsumerController.Record::key)
            readonlyColumn("value", KafkaConsumerController.Record::value)
            readonlyColumn("ts", KafkaConsumerController.Record::timestamp)
        }
    }
}