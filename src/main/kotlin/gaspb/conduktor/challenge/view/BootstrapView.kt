package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrap
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import javafx.scene.layout.Priority
import tornadofx.*

class BootstrapView : View("My View") {
    val controller : KafkaServiceController by inject()

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
                    log.info("FIRING")
                    fire(AdminConfigUpdated(model.item))
                }
            }
            enableWhen(model.valid)
        }

        button("default") {
            action {
                val default =KafkaBootstrap()
                default.additionalProps = "security.protocol=SSL\n" +
                        "ssl.truststore.location=/home/uto/Downloads/client.truststore-29262205196991976.jks\n" +
                        "ssl.truststore.password=ec1efbaca4934ffea0da695726c667bc\n" +
                        "ssl.keystore.type=PKCS12\n" +
                        "ssl.keystore.location=/home/uto/Downloads/client.keystore-11222543342845213716.p12\n" +
                        "ssl.keystore.password=b1542884b1d344908b867e8cddb61689\n" +
                        "ssl.key.password=b1542884b1d344908b867e8cddb61689\n" +
                        "ssl.endpoint.identification.algorithm="
                default.url= "kafka-374a5f07-firemailbox-b04b.aivencloud.com:24918"
                model.rebind { item = default }
            }
        }
    }

}
