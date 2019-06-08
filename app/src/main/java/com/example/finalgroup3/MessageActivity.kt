package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.finalgroup3.Adapter.MessageAdapter
import com.example.finalgroup3.model.Users
import com.example.finalgroup3.model.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageActivity : AppCompatActivity() {
    private lateinit var UserReference: DatabaseReference
    private lateinit var ChatReference: DatabaseReference
    private lateinit var  ListChatReference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    var listMessage:ArrayList<message> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        //set ActionBar
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //set LayoutManager
        messageAdapter = MessageAdapter(listMessage,this@MessageActivity)
        ListMess.layoutManager = LinearLayoutManager(this@MessageActivity)
        ListMess.adapter = messageAdapter

        //get current User
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentId = firebaseUser!!.uid
        ChatReference = FirebaseDatabase.getInstance().getReference("Chats")

        //get data intent
        val data = intent.extras
        if(data!=null){
            val userId = data.getString("userId")

            //set onclick avatar
            Image.setOnClickListener {
                val intentP = Intent(this@MessageActivity,ProfileActivity::class.java)
                intentP.putExtra("userId",userId)
                startActivity(intentP)
            }

            //show info
            UserReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            UserReference.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(dataerror: DatabaseError) {

                }
                override fun onDataChange(datasnap: DataSnapshot) {
                    val fUser : Users = datasnap.getValue(Users::class.java)!!
                    fName.text = fUser.username
                    if(fUser.ImageURL=="default"){
                        Image.setImageResource(R.drawable.user)
                    }
                    else{
                        Glide.with(this@MessageActivity)
                            .load(fUser.ImageURL)
                            .centerCrop()
                            .into(Image)
                    }

                    // show message...
                    ChatReference.addValueEventListener(object:ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            listMessage.clear()
                            for(dataSnap:DataSnapshot in dataSnapshot.children)
                            {
                                val Mess: message = dataSnap.getValue(message::class.java)!!
                                if(Mess.sender == currentId && Mess.receiver == userId ||
                                        Mess.sender == userId && Mess.receiver == currentId){
                                    listMessage.add(Mess)
                                    messageAdapter.notifyDataSetChanged()
                                }
                            }
                        }

                    })
                }
            })
            // Button send message
            sendText.setOnClickListener {
                val mess = eText.text.toString()
                if(mess!=""){
                    sendMess(currentId,userId,mess)
                }
                else{
                    Toast.makeText(this@MessageActivity, "You can't send empty message...", Toast.LENGTH_LONG).show()
                }
                eText.setText("")
            }
        }
    }
    // Update node Chat and ListChat
    private fun sendMess(currentId: String, userId: String, mess: String) {
        val hashMap: HashMap<String,String> = HashMap()
        hashMap["sender"] = currentId
        hashMap["receiver"] = userId
        hashMap["message"] = mess
        ChatReference.push().setValue(hashMap)

        ListChatReference = FirebaseDatabase.getInstance().getReference("ListChat")
            .child(currentId).child(userId)
        ListChatReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(dataSnap: DataSnapshot) {
                if(!dataSnap.exists()){
                    ListChatReference.child("id").setValue(userId)
                }
            }
        })
        ListChatReference = FirebaseDatabase.getInstance().getReference("ListChat")
            .child(userId).child(currentId)
        ListChatReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(dataSnap: DataSnapshot) {
                if(!dataSnap.exists()){
                    ListChatReference.child("id").setValue(currentId)
                }
            }
        })
    }
}
