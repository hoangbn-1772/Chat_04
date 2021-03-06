package com.sun.chat_04.data.model

data class Message(
    val id: String = "",
    var contents: String = "",
    var from: String = "",
    var type: String = "",
    var bytes: ByteArray? = null,
    var seen: Int = 0,
    var avatar: String = ""
)
