package gaspb.conduktor.challenge.view
import arrow.fx.extensions.io.unsafeRun.unsafeRun
import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import arrow.fx.coroutines.*
import arrow.integrations.kotlinx.suspendCancellable

sealed class KafkaInitialError : Throwable() {
    object NotInitialized : KafkaInitialError()
    data class ConnectError(val data: String) : KafkaInitialError()
}


fun exec() {

}

class AppWorkspace : Workspace() {
    private val bootstrapView: BootstrapView by inject()
    private val topicsView: TopicsView by inject()
    private val controller : KafkaServiceController by inject()

    private val coroutineScope = MainScope()

    init {
        dock<BootstrapView>()
        disableDelete()
        disableSave()

        subscribe<AdminConfigUpdated> {
            val model = it.config
            val job = coroutineScope.launch(Dispatchers.JavaFx) {
                controller.createAdminClient(model).map { eth ->
                    eth.mapLeft {
                    // TODO notification
                    dock<BootstrapView>()
                    }
                    .map { admin ->
                        val kafkaController = KafkaAdminController(admin)
                        val newScope = TopicsScope()
                        setInScope(kafkaController, newScope)
                        newScope.bootstrapModel.item = model

                        dock<TopicsView>(newScope)
                    }
                }.suspendCancellable()

            }
            workspace.whenDeleted {
                coroutineScope.launch(Dispatchers.JavaFx) {
                    job.cancelAndJoin()
                }
            }
        }
    }
}
