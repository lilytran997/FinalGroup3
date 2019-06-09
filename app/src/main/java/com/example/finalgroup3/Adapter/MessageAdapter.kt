package com.example.finalgroup3.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.finalgroup3.R
import com.example.finalgroup3.model.message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.item_mess_left.view.*
@SuppressLint("SetTextI18n")
class MessageAdapter(private var items: ArrayList<message>, private val context:Context):RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    private val MSG_LEFT : Int = 0
    private val MSG_RIGHT : Int = 1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return if(viewType==MSG_RIGHT){
            MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mess_right,parent,false))
        }else{
            MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mess_left,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(messViewHolder: MessageViewHolder, position: Int) {
        messViewHolder.showMess.text = items[position].message

        if(position==items.size-1){
            if(items[position].isseen){
                messViewHolder.seen.text = "Seen"
            }else{
                messViewHolder.seen.text = "Delivered"
            }
        }else{
            messViewHolder.seen.visibility = View.GONE
        }

    }

    override fun getItemViewType(position: Int): Int {

        val mUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val cId = mUser!!.uid
        return if(items[position].sender == cId){
            MSG_RIGHT
        }else{
            MSG_LEFT
        }
    }
    class MessageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
        var showMess = itemView.showMess!!
        var seen = itemView.seen!!

    }
}