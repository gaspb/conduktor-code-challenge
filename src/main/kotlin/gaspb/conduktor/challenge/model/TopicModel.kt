package gaspb.conduktor.challenge.model

import javafx.beans.property.StringProperty
import tornadofx.ItemViewModel
import tornadofx.getProperty
import tornadofx.property

class Topic {
    var name: String by property<String>()
    fun nameProperty() = getProperty(Topic::name)


}

class TopicModel : ItemViewModel<Topic>(Topic()) {
    val name: StringProperty = bind { item?.nameProperty() }

}
