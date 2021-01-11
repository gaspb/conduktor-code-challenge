package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrap
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import javafx.scene.layout.Priority
import tornadofx.*



class BootstrapView : View("My View") {

    val model : KafkaBootstrapModel by inject()
    override val root =  form {
        fieldset {
            field("Kafka brokers") {
                textfield(model.url).required()
            }
            field("additional properties") {
                textarea(model.additionalProps) {
                    prefRowCount = 5
                    vgrow = Priority.ALWAYS
                }
            }

        }
        button("Save") {
            action {
                model.commit {
                    fire(AdminConfigUpdated(model.item))
                }
            }
            enableWhen(model.valid)
        }

    }

}
