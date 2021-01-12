package gaspb.conduktor.challenge.view

import arrow.core.Either
import arrow.core.computations.either
import arrow.core.extensions.either.foldable.foldM
import arrow.fx.IO
import arrow.fx.coroutines.Duration
import arrow.fx.coroutines.sleep
import arrow.fx.coroutines.stream.drain
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.monad
import arrow.fx.extensions.io.monadIO.monadIO
import arrow.integrations.kotlinx.suspendCancellable
import gaspb.conduktor.challenge.core.KafkaConsumerController
import gaspb.conduktor.challenge.core.KafkaServiceController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.javafx.JavaFx
import tornadofx.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext


class ConsumerView: View("Consuming") {

    override val scope = super.scope as TopicScope
    private val coroutineScope = MainScope()

    private val controller : KafkaServiceController by inject()

    private val entries = mutableListOf<KafkaConsumerController.Record>().asObservable()



    private val job = coroutineScope.launch(Dispatchers.JavaFx) {

        val ctx = currentCoroutineContext()
        log.info("job ctx isActive "+ currentCoroutineContext().isActive)



       val consumer = controller.createKafkaConsumer(scope.bootstrapModel.item).suspendCancellable()

       when(consumer) {
            is Either.Left -> null// TODO
            is Either.Right -> {
                val consumerService = KafkaConsumerController(consumer.b, scope.kafkaConsumerModel.item)
                consumerService.subscriptionFlow(scope.topicModel.item.name)
                    .collect {
                        entries.add(it)
                    }

            }

        }
/*
       val eth = either {
            val consumer = controller.createKafkaConsumer(scope.bootstrapModel.item).suspendCancellable().bind()
           // val consumerService = KafkaConsumerController(consumer, scope.kafkaConsumerModel.item)
            //consumerService.subscriptionFlow(scope.topicModel.item.name)
           consumer
        }
        */



/*
        IO.fx {
           val ethI = controller.createKafkaConsumer(scope.bootstrapModel.item).bind()
           //TODO need to lift the IO<Either> to Either<IO> to keep the left and only fold on toplevel
           val consumer = IO.effectEither { ethI }.bind()
           eth.foldM(IO.monad(), 1) {
               _,consumer ->
                IO { 1 }
           }

           IO {dbg() } .bind()
        }
            .suspendCancellable()
            .collect {
                entries.add(it)
            }

*/
        /*
        val consumerEither = controller.createKafkaConsumer(scope.bootstrapModel.item).suspendCancellable()


        val consumer = (consumerEither as Either.Right).b

        val consumerService = KafkaConsumerController(consumer, scope.kafkaConsumerModel.item)
       consumerService.subscriptionFlow(scope.topicModel.item.name)
            //.effectTap { entries.add(it) }
            //.spawn(currentCoroutineContext())
            .drain()

       // log.info("wrapper isActive "+ currentCoroutineContext().isActive)

         */
    }

    override fun onDelete() {
        super.onDelete()
        job.cancel()

    }

    override val root = vbox {
        button("Stop") {
            action {
                job.cancel()
            }
        }
        listview(entries){

        }
    }
}