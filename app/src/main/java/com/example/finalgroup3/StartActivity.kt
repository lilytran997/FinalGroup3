package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        btn_login.setOnClickListener(intentLogin)
        btn_regster.setOnClickListener(intentRegister)
    }
    private val intentLogin = View.OnClickListener {
        val intent_log = Intent(this,LoginActivity::class.java )
        startActivity(intent_log)
    }
    private val intentRegister = View.OnClickListener {
        val intent_reg = Intent(this,RegisterActivity::class.java )
        startActivity(intent_reg)
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val cUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if(cUser!=null){
            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
        }
    }
}
