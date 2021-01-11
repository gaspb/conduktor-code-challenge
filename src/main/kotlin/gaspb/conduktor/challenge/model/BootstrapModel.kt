package gaspb.conduktor.challenge.model

import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class KafkaBootstrap {
    var url by property<String>()
    fun urlProperty() = getProperty(KafkaBootstrap::url)

    var additionalProps by property<String>()
    fun additionalPropsProperty() = getProperty(KafkaBootstrap::additionalProps)

}

class KafkaBootstrapModel : ItemViewModel<KafkaBootstrap>(KafkaBootstrap()) {
    val url: StringProperty = bind { item?.urlProperty() }
    val additionalProps: StringProperty = bind { item?.additionalPropsProperty() }

}
