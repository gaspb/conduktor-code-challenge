package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.KafkaProducerModel
import tornadofx.*


class ProducerEditor : Fragment("Consumer editor") {

    val model = KafkaProducerModel()

    init {
        model.interval.value = 300
    }

    override val root = form {
        fieldset {
            field("Interval") {
                textfield(model.interval).stripNonNumeric()
            }
        }
        button("Produce !") {
            action {
                model.commit {
                    
                    val producerView = ProducerView(model)
                    val parentView = find(TopicRightViewContainer::class)
                    parentView.add(producerView)
                    close()

                }
            }
        }
    }
}
