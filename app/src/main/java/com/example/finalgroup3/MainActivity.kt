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
import com.bumptech.glide.Glide
import com.example.finalgroup3.Fragment.ChatsFragment
import com.example.finalgroup3.Fragment.UsersFragment
import com.example.finalgroup3.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var tab_Layout: TabLayout
    private lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.title = ""

        // Account Info
        val User = FirebaseAuth.getInstance().currentUser
        val user_id = User!!.uid
        database = FirebaseDatabase.getInstance().getReference("Users").child(user_id)
        database.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(snapshot: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {

               val users : Users = snapshot.getValue(Users::class.java)!!
                if(users.ImageURL.equals("default")){
                    mImage.setImageResource(R.drawable.user)
                }else{
                    Glide.with(this@MainActivity)
                        .load(users.ImageURL)
                        .centerCrop()
                        .into(mImage)
                }
                name.text = users.username
                mImage.setOnClickListener {

                    val intentP = Intent(this@MainActivity, ProfileActivity::class.java)
                    intentP.putExtra("userId",user_id)
                    startActivity(intentP)
                }
            }

        })

        viewPager = findViewById(R.id.main_views)
        tab_Layout = findViewById(R.id.tablayout)
        val myViewStaticPageAdapter: ViewStaticPageAdapter = ViewStaticPageAdapter(supportFragmentManager)
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

        }
        return false
    }
    class ViewStaticPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm){
        val fragments: MutableList<Fragment> = ArrayList<Fragment>()
        val titles: MutableList<String> = ArrayList<String>()
        override fun getItem(position: Int): Fragment {
            return fragments.get(position)
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles.get(position)
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
}
