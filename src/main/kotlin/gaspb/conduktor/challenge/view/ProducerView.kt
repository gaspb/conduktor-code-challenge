package gaspb.conduktor.challenge.view

import arrow.core.Either
import arrow.integrations.kotlinx.suspendCancellable
import gaspb.conduktor.challenge.core.KafkaProducerController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.KafkaProducerModel
import gaspb.conduktor.challenge.model.TopicModel
import gaspb.conduktor.challenge.view.events.TopicViewUndocked
import gaspb.conduktor.challenge.view.style.Style
import javafx.beans.property.SimpleIntegerProperty
import javafx.scene.control.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*


class ProducerView(private val kafkaProducerModel: KafkaProducerModel) : Fragment("Consuming") {

    private val topicModel: TopicModel by inject()
    private val bootstrapModel: KafkaBootstrapModel by inject()


    private val coroutineScope = MainScope()

    private val controller: KafkaServiceController by inject()

    private val count = SimpleIntegerProperty()

    private val job = coroutineScope.launch(Dispatchers.JavaFx) {
        when (val producer = controller.createKafkaProducer(bootstrapModel.item).suspendCancellable()) {
            is Either.Left -> alert(Alert.AlertType.ERROR,"Failed to create kafka producer")
            is Either.Right -> {
                val producerService = KafkaProducerController(producer.b, kafkaProducerModel.item)
                producerService.producerFlow(topicModel.item.name, null)
                    .collect {
                        count.set(it)
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
        hbox {
            text("Sent : ")
            text {
                bind(count)
            }
        }
    }
}