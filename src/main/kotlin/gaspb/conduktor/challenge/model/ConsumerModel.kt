package gaspb.conduktor.challenge.model


import javafx.beans.property.BooleanProperty
import javafx.beans.property.LongProperty
import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaConsumer {
    var pollTimeout: Long by property<Long>()
    fun pollTimeoutProperty() = getProperty(KafkaConsumer::pollTimeout)

    var enableAutoCommit: Boolean by property<Boolean>()
    fun enableAutoCommitProperty() = getProperty(KafkaConsumer::enableAutoCommit)

    var groupId: String by property<String>()
    fun groupIdProperty() = getProperty(KafkaConsumer::groupId)

}

class KafkaConsumerModel : ItemViewModel<KafkaConsumer>(KafkaConsumer()) {
    val pollTimeout: LongProperty = bind { item?.pollTimeoutProperty() } 
    val enableAutoCommit: BooleanProperty = bind { item?.enableAutoCommitProperty() } 
    val groupId: StringProperty = bind { item?.groupIdProperty() } 
}
