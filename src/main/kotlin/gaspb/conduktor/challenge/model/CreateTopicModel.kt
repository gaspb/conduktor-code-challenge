package gaspb.conduktor.challenge.model

import javafx.beans.property.IntegerProperty
import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class CreateTopic {
    var name by property<String>()
    fun nameProperty() = getProperty(CreateTopic::name)

    var numPartition by property<Int>()
    fun numPartitionProperty() = getProperty(CreateTopic::numPartition)


}

class CreateTopicModel : ItemViewModel<CreateTopic>(CreateTopic()) {
    val name: StringProperty = bind { item?.nameProperty() }
    val numPartition: IntegerProperty = bind { item?.numPartitionProperty() }
}
