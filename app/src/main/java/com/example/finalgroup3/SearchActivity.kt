package com.example.finalgroup3

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.View
import com.example.finalgroup3.Adapter.ClickListenner
import com.example.finalgroup3.Adapter.UserAdapter
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_search.*
@SuppressLint("SetTextI18n")
@Suppress("SENSELESS_COMPARISON")
class SearchActivity : AppCompatActivity() {
    var searchUser: ArrayList<Users> = ArrayList()
    lateinit var searchAdapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(false)
        //searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
               val searchQuery = query.toString()
                resultSearch(searchQuery)
                return true
            }
            override fun onQueryTextChange(query: String?): Boolean {
                val searchQuery = query.toString()
                addUserResult(searchQuery)
                return true
            }
        })
        searchResult.layoutManager = LinearLayoutManager(this@SearchActivity)
        searchAdapter = UserAdapter(searchUser,this@SearchActivity,false)
        searchResult.adapter = searchAdapter
        searchAdapter.setClickListener(object:ClickListenner{
            override fun onClick(position: Int) {
                val builder =  AlertDialog.Builder(this@SearchActivity)
                builder.setTitle("Confirmation")
                    .setMessage("Please choose an option")
                    .setPositiveButton("Send Message") { _, _ ->
                        val intentP = Intent(this@SearchActivity, MessageActivity::class.java)
                        intentP.putExtra("userId",searchUser[position].id)
                        startActivity(intentP)

                    }
                    .setNegativeButton("Profile") { _, _ ->
                        val intent = Intent(this@SearchActivity,ProfileActivity::class.java)
                        intent.putExtra("userId",searchUser[position].id)
                        startActivity(intent)
                    }
                val myDialog = builder.create()
                myDialog.show()
            }
        })
    }

    private fun resultSearch(searchQuery: String) {
        val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val query: Query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .equalTo(searchQuery)
        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){
                    searchUser.clear()
                    for(data: DataSnapshot in dataSnapshot.children){
                        val user : Users = data.getValue(Users::class.java)!!
                        if(user.id!= fuser!!.uid){
                            searchUser.add(user)
                            searchAdapter.notifyDataSetChanged()
                        }
                    }
                }else{
                    searchResult.visibility = View.GONE
                    nullResult.visibility = View.VISIBLE
                    nullResult.text = "There isn't any Aloha account connected to this name..."
                }


            }
        })
    }
    private fun addUserResult(searchQuery: String) {
        val fuser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val query: Query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(searchQuery)
            .endAt(searchQuery+"\uf8ff")
        query.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                searchUser.clear()
                for(data: DataSnapshot in dataSnapshot.children){
                    val user : Users = data.getValue(Users::class.java)!!
                    if(user.id!= fuser!!.uid){
                        searchUser.add(user)
                        searchAdapter.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
