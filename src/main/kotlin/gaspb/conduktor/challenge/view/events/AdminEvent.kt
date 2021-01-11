package gaspb.conduktor.challenge.view.events

import gaspb.conduktor.challenge.model.KafkaBootstrap
import tornadofx.FXEvent


class AdminConfigUpdated(val config: KafkaBootstrap) : FXEvent()
