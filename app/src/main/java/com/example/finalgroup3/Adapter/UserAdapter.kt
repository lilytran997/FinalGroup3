package com.example.finalgroup3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.finalgroup3.R
import com.example.finalgroup3.model.Users
import kotlinx.android.synthetic.main.item_user.view.*

@SuppressLint("SetTextI18n")
class UserAdapter(var items: ArrayList<Users>, private val content: Context?,private val isChat:Boolean): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    lateinit var mListenner: ClickListenner
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
        if(items[position].ImageURL.equals("default")){
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

        var profile = itemView.user
        var username = itemView.user_name
        var mess = itemView.mess
        var stt_on = itemView.stt_on
        var stt_off = itemView.stt_off


    }


}

