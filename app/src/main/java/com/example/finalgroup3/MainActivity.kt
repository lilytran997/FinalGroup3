package com.example.finalgroup3


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.example.finalgroup3.Fragment.ChatsFragment
import com.example.finalgroup3.Fragment.UsersFragment
import com.example.finalgroup3.Notifications.Token
import com.example.finalgroup3.model.Users
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("LocalVariableName")
class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    private lateinit var tab_Layout: TabLayout
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user_id :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""
        auth = FirebaseAuth.getInstance()
        sttOn.visibility = View.VISIBLE
        sttOff.visibility=View.GONE

        // Account Info
        val User = auth.currentUser
        user_id= User!!.uid
        database = FirebaseDatabase.getInstance().getReference("Users").child(user_id)
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(snapshot: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
               val users : Users = snapshot.getValue(Users::class.java)!!
                if(users.ImageURL == "default"){
                    mImage.setImageResource(R.drawable.user)
                }else{
                    Glide.with(applicationContext)
                        .load(users.ImageURL)
                        .centerCrop()
                        .into(mImage)
                }
                name.text = users.username
                mProfile.setOnClickListener {
                    val intentP = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intentP)
                }
                my_profile.visibility = View.VISIBLE
            }
        })
        viewPager = findViewById(R.id.main_views)
        tab_Layout = findViewById(R.id.tablayout)
        val myViewStaticPageAdapter = ViewStaticPageAdapter(supportFragmentManager)
        myViewStaticPageAdapter.addFragment(ChatsFragment(),"Chats")
        myViewStaticPageAdapter.addFragment(UsersFragment(),"Users")
        viewPager.adapter = myViewStaticPageAdapter
        tab_Layout.setupWithViewPager(viewPager,true)
        tab_Layout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab) {
                //   viewPager.notifyAll();
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                // setAdapter();
            }
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
                val fm:FragmentManager = supportFragmentManager
                val ft: FragmentTransaction = fm.beginTransaction()
                val count: Int = fm.backStackEntryCount
                if(count>=1){
                    supportFragmentManager.popBackStack()
                }
                ft.commit()
            }
        })

        //get Token
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val newtoken = task.result?.token

                val token = newtoken?.let { Token(it) }
                val database = FirebaseDatabase.getInstance().getReference("Tokens")
                database.child(user_id).setValue(token)
                    .addOnCompleteListener {
                       // Toast.makeText(this@MainActivity, newtoken, Toast.LENGTH_SHORT).show()
                         }
            })

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item?.itemId){
            R.id.logout ->{
                FirebaseAuth.getInstance().signOut()
                sendtoStart()
                finish()
                return true
            }
            R.id.search->{
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }
    class ViewStaticPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
        private val fragments: MutableList<Fragment> = ArrayList()
        private val titles: MutableList<String> = ArrayList()
        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
        fun addFragment(fragment: Fragment, title:String){
            fragments.add(fragment)
            titles.add(title)
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // No user is signed in
            sendtoStart()
            Log.d("kiem tra Activity", "main")
        }
    }
    private fun sendtoStart(){

        val intentStart = Intent(this, StartActivity::class.java)
        startActivity(intentStart)
        finish()

    }
    // status...
    override fun onResume(){
        super.onResume()
        status("online")
        sttOn.visibility = View.VISIBLE
        sttOff.visibility=View.GONE
    }
    override fun onPause(){
        super.onPause()
        status("offline")
        sttOff.visibility = View.VISIBLE
        sttOn.visibility=View.GONE
    }
    private fun status(onOff: String) {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["OnOffline"]=onOff
        reference.updateChildren(hashMap)
    }
}
