package com.example.finalgroup3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("kiem tra Activity", "Login")

    }
}
