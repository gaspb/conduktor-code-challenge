package gaspb.conduktor.challenge.view
import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import gaspb.conduktor.challenge.view.events.RoutingEvent
import gaspb.conduktor.challenge.view.events.RoutingEventEnum
import gaspb.conduktor.challenge.view.events.TopicsViewEvent
import javafx.geometry.Orientation
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*


sealed class KafkaInitialError : Throwable() {
    object NotInitialized : KafkaInitialError()
    data class ConnectError(val data: String) : KafkaInitialError()
}


class AppWorkspace : Workspace() {
    private val bootstrapView: BootstrapView by inject()
    private val topicsView: TopicsView by inject()
    private val controller : KafkaServiceController by inject()



// TODO fix menubar (delete, save etc)
    init {
        dock<BootstrapView>()
        disableDelete()
        disableSave()

        subscribe<RoutingEvent> {
            when(it.routingEventType) {
                RoutingEventEnum.HOME -> workspace.dock(bootstrapView, true)
                RoutingEventEnum.TOPICS -> workspace.dock(topicsView, true)
                //RoutingEventEnum.CONFIG -> workspace.dock(configView, true)
            }
        }
        log.info("subscribing AdminConfigUpdated")
        subscribe<AdminConfigUpdated> {
            log.info("recieve AdminConfigUpdated")
            val model = it.config


            val job = GlobalScope.launch(Dispatchers.JavaFx) {
                controller.createAdminClient(model).suspended()
                    .mapLeft {
                        // TODO notification
                        dock<BootstrapView>()
                    }
                    .map { admin ->
                        val kafkaController = KafkaAdminController(admin)
                        val newScope = TopicsScope()
                        setInScope(kafkaController, newScope)
                        newScope.bootstrapModel.item = model

                        dock<TopicsView>(newScope)
                        //add<Menu>()
                    }
            }
            workspace.whenDeleted {
                GlobalScope.launch(Dispatchers.JavaFx) {
                    job.cancelAndJoin()
                }
            }
        }
    }
}

/*
class Menu : View() {
    override val root =  borderpane {
        top {
            listmenu {
                orientation = Orientation.HORIZONTAL

                item("Config") {
                    whenSelected {
                        fire(TopicsViewEvent())
                        log.info("Config")
                    }
                }
                item("Topics") {
                    whenSelected {
                        log.info("Topics")
                        fire(TopicsViewEvent())
                    }
                }

            }
        }
    }
}
*/
