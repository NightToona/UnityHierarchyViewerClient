package com.github.nighttoona.unityhierarchyviewerclient.network

enum class MessageType(val code: Int){

    NONE(0),
    XML(1),
    HEARTBEAT_PING(2),
    HEARTBEAT_PONG(3),
    CLOSE(4)

}