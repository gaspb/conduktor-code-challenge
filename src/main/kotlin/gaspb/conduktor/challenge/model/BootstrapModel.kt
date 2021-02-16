package gaspb.conduktor.challenge.model

import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaBootstrap {
    var url: String by property<String>("localhost:9092")
    fun urlProperty() = getProperty(KafkaBootstrap::url)

    var additionalProps: String by property<String>()
    fun additionalPropsProperty() = getProperty(KafkaBootstrap::additionalProps)

}

class KafkaBootstrapModel : ItemViewModel<KafkaBootstrap>(KafkaBootstrap()) {
    val url: StringProperty = bind { item?.urlProperty() }
    val additionalProps: StringProperty = bind { item?.additionalPropsProperty() }

}
