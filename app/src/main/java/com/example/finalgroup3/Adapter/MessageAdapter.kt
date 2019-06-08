package com.example.finalgroup3.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.example.finalgroup3.model.message

class MessageAdapter(var items: ArrayList<message>, private val context:Context):RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: MessageViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    class MessageViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}