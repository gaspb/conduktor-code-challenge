package gaspb.conduktor.challenge.view.events

import gaspb.conduktor.challenge.model.Topic
import tornadofx.FXEvent




class TopicSelected(val topic: Topic) : FXEvent()

