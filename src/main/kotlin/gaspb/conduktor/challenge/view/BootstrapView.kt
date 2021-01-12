package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import javafx.scene.layout.Priority
import tornadofx.*


class BootstrapView : View("Cluster") {

    val model: KafkaBootstrapModel by inject()

    init {
        disableDelete()
        disableSave()
        disableClose()
        disableRefresh()
        disableCreate()
    }


    override val root = form {

        fieldset {
            vbox {
                field("Kafka brokers") {
                    textfield(model.url).required()
                }
                field("additional props") {
                    textarea(model.additionalProps) {
                        prefRowCount = 5
                        vgrow = Priority.ALWAYS
                    }
                }

            }
        }

        button("Connect") {
            action {
                model.commit {
                    fire(AdminConfigUpdated(model.item))
                }
            }
            enableWhen(model.valid)
        }

    }

}
