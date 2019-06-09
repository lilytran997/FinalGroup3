package com.example.finalgroup3.model

data class message(val sender: String, val receiver: String, val message: String, val isseen: Boolean) {

    constructor(): this("","","",false)
}