package gaspb.conduktor.challenge.model


import javafx.beans.property.LongProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaConsumer {
    var pollTimeout by property<Long>()
    fun pollTimeoutProperty() = getProperty(KafkaConsumer::pollTimeout)
}

class KafkaConsumerModel : ItemViewModel<KafkaConsumer>(KafkaConsumer()) {
    val pollTimeout: LongProperty = bind { item?.pollTimeoutProperty() } as LongProperty

}
