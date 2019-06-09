package com.example.finalgroup3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.finalgroup3.R
import com.example.finalgroup3.model.Users
import com.example.finalgroup3.model.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_user.view.*

@Suppress("SpellCheckingInspection")
@SuppressLint("SetTextI18n")
class UserAdapter(private var items: ArrayList<Users>, private val content: Context?, private val isChat:Boolean): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var mListenner: ClickListenner
    private lateinit var thelastmess: String

    //get Layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {

        return UserViewHolder(LayoutInflater.from(content).inflate(R.layout.item_user,parent,false))
    }

    // get size List
    override fun getItemCount(): Int {
        return items.size
    }
    // implement interface ClickListenner
    fun setClickListener(mListenner: ClickListenner){

        this.mListenner = mListenner
    }

    // Bind data
    override fun onBindViewHolder(viewHolder: UserViewHolder, position: Int) {

        viewHolder.username.text = items[position].username
        if(items[position].ImageURL == "default"){
            viewHolder.profile.setImageResource(R.drawable.user)
        }else{
            content?.let {
                Glide.with(it)
                    .load(items[position].ImageURL)
                    .centerCrop()
                    .into(viewHolder.profile)
            }
        }
        // get status or the last message
        if(isChat){
           TheLastMessage(items[position].id,viewHolder.mess)
        }else{
            viewHolder.mess.text = items[position].status
        }

        //online/offline
        if(items[position].OnOffline=="online"){
            viewHolder.stt_on.visibility=View.VISIBLE
            viewHolder.stt_off.visibility=View.GONE
        }else{
            viewHolder.stt_off.visibility=View.VISIBLE
            viewHolder.stt_on.visibility=View.GONE
        }

        // set onclick item
        viewHolder.itemView.setOnClickListener{
            mListenner.onClick(position)
        }
    }

    //show the last message
    private fun TheLastMessage(userId: String, mess: TextView) {
        thelastmess = "default"
        val current = FirebaseAuth.getInstance().currentUser
        val mId = current!!.uid
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        reference.addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(datasnap: DataSnapshot) {
                for(data: DataSnapshot in datasnap.children){
                    val chat: message = data.getValue(message::class.java)!!
                    if(chat.sender == mId && chat.receiver == userId ||
                        chat.receiver == mId && chat.sender == userId){
                        thelastmess = chat.message
                    }
                    if(chat.receiver == mId && chat.sender == userId){
                        if(chat.isseen){
                            val typeface : Int = Typeface.NORMAL
                            mess.typeface = Typeface.defaultFromStyle(typeface)
                        } else{
                            val typeface : Int = Typeface.BOLD
                            mess.typeface = Typeface.defaultFromStyle(typeface)
                        }
                    }else{
                        val typeface : Int = Typeface.NORMAL
                        mess.typeface = Typeface.defaultFromStyle(typeface)
                    }
                }

                if (thelastmess == "default") {
                    mess.text = "No Message..."
                } else {
                    mess.text = thelastmess
                }
                thelastmess = "default"
            }
        })
    }

    // define viewHolder
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile: CircleImageView = itemView.user
        var username: TextView = itemView.user_name
        var mess: TextView = itemView.mess
        var stt_on: CircleImageView = itemView.stt_on
        var stt_off: CircleImageView = itemView.stt_off
    }


}

