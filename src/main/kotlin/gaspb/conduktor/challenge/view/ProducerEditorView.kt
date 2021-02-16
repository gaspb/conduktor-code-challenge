package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.KafkaProducerModel
import gaspb.conduktor.challenge.model.ProducerMethod
import tornadofx.*


class ProducerEditor : Fragment("Consumer editor") {

    val model = KafkaProducerModel()

    init {
        model.interval.value = 300
    }

    override val root = form {
        fieldset {

            field() {
                combobox(model.method, ProducerMethod.values().toList()) {
                    cellFormat {
                        text = when(it) {
                            ProducerMethod.USE_INTERVAL -> "Use interval"
                            ProducerMethod.USE_THROUGHPUT -> "Use throughput"
                        }
                    }
                }
            }

            field("Interval") {
                removeWhen(model.method.isNotEqualTo(ProducerMethod.USE_INTERVAL))
                textfield(model.interval).stripNonNumeric()
            }

            field("Throughput (KB/s)") {
                removeWhen(model.method.isNotEqualTo(ProducerMethod.USE_THROUGHPUT))
                textfield(model.throughput).stripNonNumeric()
            }

            field("Message size (bytes)") {
                textfield(model.messageSize).stripNonNumeric()
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
