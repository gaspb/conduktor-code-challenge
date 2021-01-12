package gaspb.conduktor.challenge.model


import javafx.beans.property.BooleanProperty
import javafx.beans.property.LongProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaConsumer {
    var pollTimeout by property<Long>()
    fun pollTimeoutProperty() = getProperty(KafkaConsumer::pollTimeout)

    var enableAutoCommit by property<Boolean>()
    fun enableAutoCommitProperty() = getProperty(KafkaConsumer::enableAutoCommit)

}

class KafkaConsumerModel : ItemViewModel<KafkaConsumer>(KafkaConsumer()) {
    val pollTimeout: LongProperty = bind { item?.pollTimeoutProperty() } as LongProperty
    val enableAutoCommit: BooleanProperty = bind { item?.enableAutoCommitProperty() } as BooleanProperty
}
