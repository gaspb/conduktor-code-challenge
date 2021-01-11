package gaspb.conduktor.challenge.view.events

import tornadofx.FXEvent

enum class RoutingEventEnum {
    TOPICS,
    CONFIG,
    HOME
}

abstract class RoutingEvent(val routingEventType: RoutingEventEnum) : FXEvent()

class TopicsViewEvent : RoutingEvent(RoutingEventEnum.TOPICS)
class ConfigViewEvent : RoutingEvent(RoutingEventEnum.CONFIG)
class HomeViewEvent : RoutingEvent(RoutingEventEnum.HOME)