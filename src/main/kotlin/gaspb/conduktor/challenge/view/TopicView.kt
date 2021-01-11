package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.KafkaConsumerModel
import gaspb.conduktor.challenge.model.TopicModel
import tornadofx.*


class TopicScope : Scope() {
    val topicModel: TopicModel = TopicModel()
    val bootstrapModel: KafkaBootstrapModel = KafkaBootstrapModel()
    val kafkaConsumerModel : KafkaConsumerModel  = KafkaConsumerModel()
}
class TopicView : View("Topic") {

    override val scope = super.scope as TopicScope
    val model: TopicModel = scope.topicModel

    init {
        log.info("Im in topicView")
    }

    override val root = borderpane {
        top {
            text(model.name)

        }
        center {
            button("Consume") {
                action {
                    val newScope = TopicScope()
                    newScope.topicModel.item = model.item
                    newScope.bootstrapModel.item = scope.bootstrapModel.item
                    openInternalWindow<ConsumerEditor>()
                }
            }
        }
    }

}


