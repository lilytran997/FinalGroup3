package com.example.finalgroup3.Fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalgroup3.Adapter.ClickListenner
import com.example.finalgroup3.Adapter.UserAdapter
import com.example.finalgroup3.MessageActivity

import com.example.finalgroup3.R
import com.example.finalgroup3.model.ListChat
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_chats.*

class ChatsFragment : Fragment() {
    var chat: ArrayList<Users> = ArrayList()
    lateinit var chatAdapter: UserAdapter
    var userList:ArrayList<ListChat> = ArrayList()
    private lateinit var UserReference: DatabaseReference
    private lateinit var ChatReference: DatabaseReference
    private lateinit var  ListChatReference: DatabaseReference
    private lateinit var currentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //firebase
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        currentId = firebaseUser!!.uid
        UserReference = FirebaseDatabase.getInstance().getReference("Users")
        ChatReference = FirebaseDatabase.getInstance().getReference("Chats")

        addListChat()
        chatAdapter = UserAdapter(chat,activity,true)

    }

    // add List Chat
    private fun addListChat() {
        ListChatReference = FirebaseDatabase.getInstance().getReference("ListChat").child(currentId)
        ListChatReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(snap: DataSnapshot) {
                userList.clear()
                for(data: DataSnapshot in snap.children){
                    val listChat: ListChat? = data.getValue(ListChat::class.java)
                    listChat?.let { userList.add(it) }
                }
                getListChat()
            }
        })
    }

    // get Data
    private fun getListChat() {

        UserReference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnap: DataSnapshot) {
                chat.clear()
                for(data: DataSnapshot in dataSnap.children){
                    val userChat: Users = data.getValue(Users::class.java)!!
                    for(user:ListChat in userList){
                        if(user.id == userChat.id){
                            chat.add(userChat)
                            chatAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

    }

    // get Layout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }
    // set LayoutManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listChat.layoutManager = LinearLayoutManager(context)
        listChat.adapter = chatAdapter

        chatAdapter.setClickListener(object : ClickListenner {
            override fun onClick(position: Int) {
                val intentP = Intent(activity, MessageActivity::class.java)
                intentP.putExtra("userId",chat[position].id)
                startActivity(intentP)
            }
        })
    }


}
