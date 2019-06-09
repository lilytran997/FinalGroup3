package com.example.finalgroup3.Fragment


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalgroup3.Adapter.ClickListenner
import com.example.finalgroup3.Adapter.UserAdapter
import com.example.finalgroup3.MessageActivity
import com.example.finalgroup3.ProfileActivity

import com.example.finalgroup3.R
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_users.*


@Suppress("PrivatePropertyName")
class UsersFragment : Fragment() {

    var users: ArrayList<Users> = ArrayList()
    lateinit var userAdapter: UserAdapter
    private lateinit var UserDatabase: DatabaseReference
    //private lateinit var mHandler: Handler
    private lateinit var mRunnable:Runnable
    private var handler: Handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addUser()
        userAdapter = UserAdapter(users,activity,false)

    }

    // get Data user
    private fun addUser() {
        val mUser = FirebaseAuth.getInstance().currentUser
        val userId = mUser!!.uid
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users")
        UserDatabase.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                users.clear()
                for(data: DataSnapshot in dataSnapshot.children){
                    val user : Users? = data.getValue(Users::class.java)
                    if(user!!.id != userId){
                        users.add(user)
                        userAdapter.notifyDataSetChanged()

                    }
                }
            }
        })
    }
    // Get Layout Fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    // Set LayoutManager and set onclick item
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listUser.layoutManager = LinearLayoutManager(context)
        listUser.adapter = userAdapter

        //set onclick item
        userAdapter.setClickListener(object :ClickListenner{
            override fun onClick(position: Int) {

                val builder = context?.let { AlertDialog.Builder(it) }
                builder!!.setTitle("Confirmation")
                    .setMessage("Please choose an option")
                    .setPositiveButton("Send Message") { _, _ ->
                        val intentP = Intent(activity, MessageActivity::class.java)
                        intentP.putExtra("userId",users[position].id)
                        startActivity(intentP)

                    }
                    .setNegativeButton("Profile") { _, _ ->
                        val intent = Intent(activity,ProfileActivity::class.java)
                        intent.putExtra("userId",users[position].id)
                        startActivity(intent)
                    }
                val myDialog = builder.create()
                myDialog.show()
            }
        })
        // pull refresh
        refreshUser.setOnRefreshListener {
            mRunnable = Runnable {
                userAdapter.clear()
                RefreshUsers()
                refreshUser.isRefreshing=false
            }
            //set delay
            handler.postDelayed(mRunnable, 300)
            //mHandler.postDelayed(mRunnable,5000)
        }

        // set color refreshicon
        refreshUser.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light)
    }
    // update List
    var UserRefresh: ArrayList<Users> = ArrayList()
    private fun RefreshUsers() {
        val mUser = FirebaseAuth.getInstance().currentUser
        val userId = mUser!!.uid
        UserDatabase = FirebaseDatabase.getInstance().getReference("Users")
        UserDatabase.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(databaseError: DatabaseError) {

            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                UserRefresh.clear()
                for(data: DataSnapshot in dataSnapshot.children){
                    val user : Users? = data.getValue(Users::class.java)
                    if(user!!.id != userId){
                        UserRefresh.add(user)
                    }
                }
                userAdapter.updateList(UserRefresh)
            }
        })
    }


}

