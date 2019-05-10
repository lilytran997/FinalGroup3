package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("kiem tra Activity", "Login")

        auth = FirebaseAuth.getInstance()
        btn_log.setOnClickListener(login)
    }
    private val login = View.OnClickListener {
        val text_email: String = email.text.toString()
        val text_pass: String = pass.text.toString()

        if ((TextUtils.isEmpty(text_email)) || (TextUtils.isEmpty(text_pass))) {
            Toast.makeText(this@LoginActivity, "All fileds are required", Toast.LENGTH_SHORT).show();
        } else {
            auth.signInWithEmailAndPassword(text_email, text_pass)
                .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //val user = auth.currentUser
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}
