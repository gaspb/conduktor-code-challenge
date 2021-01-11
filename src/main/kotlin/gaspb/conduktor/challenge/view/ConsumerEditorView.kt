package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaConsumerModel
import tornadofx.*


class ConsumerEditor : View("Consumer editor") {
    private val controller : KafkaServiceController = KafkaServiceController()

    override val scope = super.scope as TopicScope
    val model  = KafkaConsumerModel()
    init {
        model.pollTimeout.setValue(300)
    }
    override val root = form {
        fieldset {
            field("poll timeout") {
                textfield(model.pollTimeout).stripNonNumeric()
            }
        }
        button("Consume !") {
            action {

                model.commit {

                    val newScope = TopicScope()
                    newScope.topicModel.item = scope.topicModel.item
                    newScope.bootstrapModel.item = scope.bootstrapModel.item
                    newScope.kafkaConsumerModel.item = model.item
                    setInScope(controller, newScope)
                    val consumerView = find(ConsumerView::class, newScope)
                    replaceWith(consumerView)


                }


            }
        }
    }
}
