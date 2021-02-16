package gaspb.conduktor.challenge.model


import javafx.beans.property.IntegerProperty
import javafx.beans.property.LongProperty
import javafx.beans.property.ObjectProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property


enum class ProducerMethod {
    USE_INTERVAL,
    USE_THROUGHPUT
}
class KafkaProducer {

    var method: ProducerMethod by property<ProducerMethod>(ProducerMethod.USE_THROUGHPUT)
    fun methodProperty() = getProperty(KafkaProducer::method)

    var interval: Long by property<Long>(300)
    fun intervalProperty() = getProperty(KafkaProducer::interval)

    var throughput: Long by property<Long>(10)
    fun throughputProperty() = getProperty(KafkaProducer::throughput)

    var messageSize: Int by property<Int>(1000)
    fun messageSizeProperty() = getProperty(KafkaProducer::messageSize)
}

class KafkaProducerModel : ItemViewModel<KafkaProducer>(KafkaProducer()) {
    val interval: LongProperty = bind { item?.intervalProperty() }
    val throughput: LongProperty = bind { item?.throughputProperty() }
    val method: ObjectProperty<ProducerMethod> = bind { item?.methodProperty() }
    val messageSize: IntegerProperty = bind { item?.messageSizeProperty() }

}
