package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.KafkaProducerModel
import tornadofx.*


class ProducerEditor : View("Consumer editor") {

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
                    setInScope(model)
                    val producerView = find<ProducerView>()
                    val parentView = find(TopicRightViewContainer::class)
                    parentView.add(producerView)
                    close()

                }
            }
        }
    }
}
