package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.TopicModel
import gaspb.conduktor.challenge.view.events.TopicCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.*


class TopicsScope : Scope() {
    val bootstrapModel: KafkaBootstrapModel = KafkaBootstrapModel()
}

class TopicsView : View("Topics") {

    private val bootstrapModel: KafkaBootstrapModel by inject()
    private val controller: KafkaAdminController by inject()
    private val service: KafkaServiceController by inject()

    private val coroutineScope = MainScope()

    private val topics = mutableListOf<String>().asObservable()


    private fun doFetchTopics() = coroutineScope.launch(Dispatchers.JavaFx) {
        topics.clear()
        val ts = controller.listTopics()
        topics.addAll(ts)
    }

    private val job = doFetchTopics()


    override fun onCreate() {
        find<TopicEditor>().openWindow()
    }

    init {
        disableRefresh()
        disableRefresh()
        disableClose()
        disableSave()
        disableDelete()
        subscribe<TopicCreated> {
            doFetchTopics()
        }
    }

    override val root = borderpane {

        center {
            listview(topics) {
                cellCache { s ->
                    button(s) {
                        action {
                            val topicModel = TopicModel()
                            topicModel.item.name = s
                            workspace.dockInNewScope<TopicView>(topicModel, bootstrapModel, service)

                        }
                    }
                }
            }
        }
    }


    override fun onUndock() {
        super.onUndock()
        coroutineScope.launch(Dispatchers.IO) {
            job.cancelAndJoin()
        }
    }

}
