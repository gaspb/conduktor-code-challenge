package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.model.CreateTopicModel
import gaspb.conduktor.challenge.view.events.TopicCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.*


class TopicEditor : View("Topic editor") {

    private val controller: KafkaAdminController by inject()

    val model = CreateTopicModel()
    private val coroutineScope = MainScope()

    override val root = form {
        fieldset {
            field("name") {
                textfield(model.name)
            }
            field("number of partitions") {
                textfield(model.numPartition).stripNonInteger()
            }

        }
        button("Create !") {
            action {
                model.commit {
                    coroutineScope.launch(Dispatchers.IO) {
                        controller.createTopic(model.item.name, model.item.numPartition)
                        delay(500) //because I can't get a future from above (returned future make extra requests I don't need)
                        fire(TopicCreated())
                    }

                    close()

                }
            }
        }
    }
}
