package com.example.finalgroup3.model

data class Users(val id: String, val username: String,val ImageURL: String, val status: String){
    constructor() : this("","","","")
}