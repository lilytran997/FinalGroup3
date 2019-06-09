package com.example.finalgroup3

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.finalgroup3.Adapter.MessageAdapter
import com.example.finalgroup3.model.Users
import com.example.finalgroup3.model.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageActivity : AppCompatActivity() {
    private lateinit var UserReference: DatabaseReference
    private lateinit var ChatReference: DatabaseReference
    private lateinit var  ListChatReference: DatabaseReference
    private lateinit var ListReference: DatabaseReference
    lateinit var messageAdapter: MessageAdapter
    var listMessage:ArrayList<message> = ArrayList()
    private lateinit var currentId: String
    private lateinit var seenListenner: ValueEventListener

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
        currentId = firebaseUser!!.uid
        ChatReference = FirebaseDatabase.getInstance().getReference("Chats")

        //get data intent
        val data = intent.extras
        if(data!=null){
            val userId = data.getString("userId")
            //show info
            UserReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            UserReference.addValueEventListener(object: ValueEventListener{
                override fun onCancelled(dataerror: DatabaseError) {

                }
                override fun onDataChange(datasnap: DataSnapshot) {
                    val fUser : Users = datasnap.getValue(Users::class.java)!!
                    fName.text = fUser.username
                    //avatar
                    if(fUser.ImageURL=="default"){
                        Image.setImageResource(R.drawable.user)
                    }
                    else{
                        Glide.with(applicationContext)
                            .load(fUser.ImageURL)
                            .centerCrop()
                            .into(Image)
                    }
                    //set onclick avatar
                    Image.setOnClickListener {
                        val intentP = Intent(this@MessageActivity,ProfileActivity::class.java)
                        intentP.putExtra("userId",fUser.id)
                        startActivity(intentP)
                    }

                   // online/offline
                    if(fUser.OnOffline=="online"){
                        sttOn.visibility = View.VISIBLE
                        sttOff.visibility=View.GONE
                    }else{
                        sttOff.visibility = View.VISIBLE
                        sttOn.visibility=View.GONE
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
            seenMessage(userId)
        }
    }

    //seen Message
    private fun seenMessage(userId: String?) {
        ChatReference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListenner = ChatReference.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data:DataSnapshot in snapshot.children){
                    val chats: message = data.getValue(message::class.java)!!
                    if(chats.receiver==currentId && chats.sender==userId){
                        val hashmap: HashMap<String,Any> = HashMap()
                        hashmap["isseen"]=true
                        data.ref.updateChildren(hashmap)
                    }
                }
            }

        })
    }
    // Update node Chat and ListChat
    private fun sendMess(currentId: String, userId: String, mess: String) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["sender"] = currentId
        hashMap["receiver"] = userId
        hashMap["message"] = mess
        hashMap["isseen"] = false
        ChatReference.push().setValue(hashMap)

        ListChatReference = FirebaseDatabase.getInstance().getReference("ListChat").child(currentId).child(userId)
        ListChatReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(data: DataSnapshot) {
                if(!data.exists()){
                    ListChatReference.child("id").setValue(userId)
                }
            }
        })
        ListReference = FirebaseDatabase.getInstance().getReference("ListChat").child(userId).child(currentId)
        ListReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(dataSnap: DataSnapshot) {
                if(!dataSnap.exists()){
                    ListReference.child("id").setValue(currentId)
                }
            }
        })
    }
    override fun onResume(){
        super.onResume()
        status("online")
    }
    override fun onPause(){
        super.onPause()
        status("offline")
        ChatReference.removeEventListener(seenListenner)
    }

    // Online/Offline
    private fun status(onOff: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(currentId)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["OnOffline"]=onOff
        reference.updateChildren(hashMap)
    }
}
