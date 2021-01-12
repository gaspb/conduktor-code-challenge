package gaspb.conduktor.challenge.view

import arrow.integrations.kotlinx.suspendCancellable
import gaspb.conduktor.challenge.core.KafkaAdminController
import gaspb.conduktor.challenge.core.KafkaServiceController
import gaspb.conduktor.challenge.model.KafkaBootstrapModel
import gaspb.conduktor.challenge.view.events.AdminConfigUpdated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.launch
import tornadofx.Workspace

sealed class KafkaInitialError : Throwable() {
    object NotInitialized : KafkaInitialError()
    data class ConnectError(val data: String) : KafkaInitialError()
}


fun exec() {

}

class AppWorkspace : Workspace() {
    private val service: KafkaServiceController by inject()

    private val coroutineScope = MainScope()

    init {
        dock<BootstrapView>()
        disableDelete()
        disableSave()



        subscribe<AdminConfigUpdated> {
            val model = it.config
            val job = coroutineScope.launch(Dispatchers.JavaFx) {
                service.createAdminClient(model).map { eth ->
                    eth.mapLeft {
                        // TODO notification
                        dock<BootstrapView>()
                    }
                        .map { admin ->

                            val newModel = KafkaBootstrapModel()
                            newModel.item = model
                            dockInNewScope<TopicsView>(newModel, KafkaAdminController(admin), service)


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
