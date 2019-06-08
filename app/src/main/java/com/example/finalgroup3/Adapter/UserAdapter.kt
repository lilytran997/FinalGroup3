package com.example.finalgroup3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.finalgroup3.R
import com.example.finalgroup3.model.Users
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_user.view.*

@Suppress("SpellCheckingInspection")
@SuppressLint("SetTextI18n")
class UserAdapter(private var items: ArrayList<Users>, private val content: Context?, private val isChat:Boolean): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private lateinit var mListenner: ClickListenner
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {


        return UserViewHolder(LayoutInflater.from(content).inflate(R.layout.item_user,parent,false))
    }

    override fun getItemCount(): Int {
        return items.size
    }
    fun setClickListener(mListenner: ClickListenner){

        this.mListenner = mListenner
    }


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
        if(isChat){
           viewHolder.mess.visibility = View.GONE
        }else{
            viewHolder.mess.text = items[position].status
        }
        viewHolder.itemView.setOnClickListener{
            mListenner.onClick(position)
        }
    }


    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profile: CircleImageView = itemView.user
        var username: TextView = itemView.user_name
        var mess: TextView = itemView.mess
        var stt_on: CircleImageView = itemView.stt_on
        var stt_off: CircleImageView = itemView.stt_off


    }


}

