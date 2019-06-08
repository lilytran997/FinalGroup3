package com.example.finalgroup3

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    lateinit var UserDatabase: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var  userId: String
    lateinit var mUserId: String
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        mUserId = firebaseUser!!.uid

        userId = intent.getStringExtra("userId")
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId)
        UserDatabase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnap: DataSnapshot) {
                val user: Users = dataSnap.getValue(Users::class.java)!!
                if (user.ImageURL.equals("default")) {
                    profile.setImageResource(R.drawable.user)
                } else {
                    Glide.with(this@ProfileActivity)
                        .load(user.ImageURL)
                        .centerCrop()
                        .into(profile)
                }
                mName.text = user.username
                mStatus.text = user.status
            }
        })
        if (userId == mUserId) {
            btn_send.text = "Edit Information"
            btn_send.setOnClickListener {
                val intentEd = Intent(this@ProfileActivity,EditInfoActivity::class.java)
                startActivity(intentEd)
            }
        } else {
            btn_send.text = "Send Message"
            btn_send.setOnClickListener {
                val intentMess = Intent(this@ProfileActivity, MessageActivity::class.java)
                intentMess.putExtra("userId",userId)
                startActivity(intentMess)
            }
        }
    }
}
