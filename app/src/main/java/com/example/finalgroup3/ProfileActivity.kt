package com.example.finalgroup3

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
@SuppressLint("SetTextI18n")
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "PrivatePropertyName", "SpellCheckingInspection")
class ProfileActivity : AppCompatActivity() {
    private lateinit var UserDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var mUserId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = "Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        mUserId = firebaseUser!!.uid
        sttOn.visibility = View.VISIBLE
        sttOff.visibility = View.GONE
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users")
        //get data intent
        val data = intent.extras
        if(data!=null) {
            val userId = data.getString("userId")
            UserDatabase.child(userId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(dataSnap: DataSnapshot) {
                    val user: Users = dataSnap.getValue(Users::class.java)!!
                    if (user.ImageURL == "default") {
                        profile.setImageResource(R.drawable.user)
                    } else {
                        Glide.with(applicationContext)
                            .load(user.ImageURL)
                            .centerCrop()
                            .into(profile)
                    }
                    if (user.OnOffline == "online") {
                        sttOn.visibility = View.VISIBLE
                        sttOff.visibility = View.GONE
                    } else {
                        sttOff.visibility = View.VISIBLE
                        sttOn.visibility = View.GONE
                    }
                    mName.text = user.username
                    mStatus.text = user.status
                }
            })
            if (userId != mUserId) {
                btn_send.text = "Send Message"
                btn_send.setOnClickListener {
                    val intentMess = Intent(this@ProfileActivity, MessageActivity::class.java)
                    intentMess.putExtra("userId", userId)
                    startActivity(intentMess)
                }
            }
        }else{
            UserDatabase.child(mUserId).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(data: DataSnapshot) {
                    val user: Users = data.getValue(Users::class.java)!!
                    if (user.ImageURL == "default") {
                        profile.setImageResource(R.drawable.user)
                    } else {
                        Glide.with(applicationContext)
                            .load(user.ImageURL)
                            .centerCrop()
                            .into(profile)
                    }
                    mName.text = user.username
                    mStatus.text = user.status
                }
            })
            btn_send.text = "Edit Information"
            btn_send.setOnClickListener {
                val intentEd = Intent(this@ProfileActivity,EditInfoActivity::class.java)
                startActivity(intentEd)
            }
        }
    }
    override fun onResume(){
        super.onResume()
        status("online")
    }
    override fun onPause(){
        super.onPause()
        status("offline")
    }

    // Online/Offline
    private fun status(onOff: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mUserId)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["OnOffline"]=onOff
        reference.updateChildren(hashMap)
    }
}
