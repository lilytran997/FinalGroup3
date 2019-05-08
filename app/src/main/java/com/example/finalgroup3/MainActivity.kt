package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // No user is signed in
            sendtoStart()
            Log.d("kiem tra Activity", "main")
        }
    }
    private fun sendtoStart(){

        val intentStart = Intent(this, StartActivity::class.java)
        startActivity(intentStart)
        finish()

    }
}
