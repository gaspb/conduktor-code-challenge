package gaspb.conduktor.challenge.model


import javafx.beans.property.LongProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaProducer {
    var interval by property<Long>()
    fun intervalProperty() = getProperty(KafkaProducer::interval)

}

class KafkaProducerModel : ItemViewModel<KafkaProducer>(KafkaProducer()) {
    val interval: LongProperty = bind { item?.intervalProperty() } as LongProperty
}
