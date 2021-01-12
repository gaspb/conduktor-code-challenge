package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.Topic
import kotlinx.coroutines.*
import tornadofx.*


class TopicsScope : Scope() {
    val bootstrapModel: KafkaBootstrapModel = KafkaBootstrapModel()
}
class TopicsView : View("Topics") {

    override val scope= super.scope as TopicsScope
    val coroutineScope = MainScope()
    private val controller : KafkaAdminController by inject()
    private val topics = mutableListOf<String>().asObservable()


    private val job = coroutineScope.launch( Dispatchers.IO ) {
       val ts = controller.listTopics()
        topics.addAll(ts)
    }

    override fun onDelete() {
        super.onDelete()
        coroutineScope.launch( Dispatchers.IO ) {
            job.cancelAndJoin()
        }
    }



    override val root = borderpane {
        center {
            listview(topics) {
                cellCache {
                    s ->  button(s) {
                                action {

                                    log.info("selected topic")
                                    val newScope = TopicScope()
                                    val model = Topic()
                                    model.name = s
                                    newScope.topicModel.item = model
                                    newScope.bootstrapModel.item = scope.bootstrapModel.item
                                    val view = find(TopicView::class, newScope)
                                    replaceWith(view)
                                }
                         }
                }
            }
        }
    }

}
