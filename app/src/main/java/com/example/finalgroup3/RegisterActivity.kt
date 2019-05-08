package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        //toolbar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("kiem tra Activity", "register")

        //firebase auth
        auth = FirebaseAuth.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference.child("Users")

        btn_reg.setOnClickListener(register)
    }

    private val register = View.OnClickListener {
        val txt_user = username.text.toString()
        val txt_email = textemail.text.toString()
        val txt_pass = textpass.text.toString()

        if ((TextUtils.isEmpty(txt_user)) || (TextUtils.isEmpty(txt_email)) || (TextUtils.isEmpty(txt_pass))) {
            Toast.makeText(this, "All fileds are required", Toast.LENGTH_LONG).show()
        } else if (txt_pass.length < 6) {
            Toast.makeText(this, "Password must be a least 6 characters", Toast.LENGTH_SHORT).show()
        } else {
            createAccount(txt_user, txt_email, txt_pass)
        }


    }

    private fun createAccount(username: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userId = user!!.uid
                    //push  database
                    val hashMap:HashMap<String, String> = HashMap<String, String>()
                    hashMap.put("id", userId)
                    hashMap.put("username", username)
                    hashMap.put("ImageURL", "default")
                    databaseReference.child(userId).setValue(hashMap)
                        .addOnCompleteListener(this) {
                            if (task.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }

    }


}

