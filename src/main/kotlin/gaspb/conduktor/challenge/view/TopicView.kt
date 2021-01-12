package gaspb.conduktor.challenge.view

import gaspb.conduktor.challenge.model.TopicModel
import gaspb.conduktor.challenge.view.events.TopicViewUndocked
import gaspb.conduktor.challenge.view.style.Style
import tornadofx.*



class TopicView : View("Topic") {

    val model: TopicModel by inject()

    init {
        disableRefresh()
        disableRefresh()
        disableClose()
        disableSave()
        disableCreate()
        disableDelete()
    }

    override fun onUndock() {
        super.onUndock()
        fire(TopicViewUndocked())
    }

    override val root = borderpane {
        top {
            text(model.name)
        }
        left<TopicLeftViewContainer>()
        right<TopicRightViewContainer>()
    }

}

class TopicLeftViewContainer : View("Consumers") {
    override val root = vbox {
        style {
            prefWidth = 400.px
        }
        addClass(Style.blackBorder)
        button("Add a consumer") {
            action {
                find<ConsumerEditor>().openWindow()
            }
        }
    }
}

class TopicRightViewContainer : View("Producers") {
    override val root = vbox {
        style {
            prefWidth = 400.px
        }
        addClass(Style.blackBorder)
        button("Add a producer") {
            action {
                find<ProducerEditor>().openWindow()
            }
        }
    }
}