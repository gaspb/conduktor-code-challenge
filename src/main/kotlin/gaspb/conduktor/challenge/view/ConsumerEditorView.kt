package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaConsumerModel
import tornadofx.*


class ConsumerEditor : Fragment("Consumer editor") {

    val model = KafkaConsumerModel()

    init {
        model.pollTimeout.value = 300
    }


    override val root = form {
        fieldset {
            field("poll timeout") {
                textfield(model.pollTimeout).stripNonNumeric()
            }
            field("enable auto commit") {
                checkbox("", model.enableAutoCommit)
            }
            field("groupId") {
                textfield(model.groupId)
                hiddenWhen(!model.enableAutoCommit)
            }

        }
        button("Consume !") {
            action {
                model.commit {

                    val consumerView = ConsumerView(model)
                    val parentView = find(TopicLeftViewContainer::class)
                    parentView.add(consumerView)
                    close()

                }
            }
        }
    }
}
