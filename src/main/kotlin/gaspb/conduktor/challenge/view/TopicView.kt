package gaspb.conduktor.challenge.view

import arrow.core.Either
import arrow.core.extensions.either.monad.monad
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.extensions.io.monadIO.monadIO
import arrow.fx.extensions.resource.monadIO.liftIO

import gaspb.conduktor.challenge.core.KafkaConsumerController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.model.KafkaConsumerModel
import gaspb.conduktor.challenge.model.TopicModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.javafx.JavaFx
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


