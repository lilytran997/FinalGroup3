package com.example.finalgroup3

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val data = intent.extras
        if(data!=null){
            val userId = data.getString("userId")
        }
    }
}
