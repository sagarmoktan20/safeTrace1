package com.example.safetrace

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.safetrace.databinding.ItemInviteBinding

class Invite_Adapter(val listOfContacts: List<ContactModel>): RecyclerView.Adapter<Invite_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val inflater = LayoutInflater.from(parent.context)
        val item = ItemInviteBinding.inflate(inflater, parent, false)
        return ViewHolder(item)
    }

    override fun getItemCount(): Int {
        return listOfContacts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listOfContacts[position]
        holder.name.text = item.name
        holder.invite.text = item.number.toString()

    }
    class ViewHolder(val item: ItemInviteBinding): RecyclerView.ViewHolder(item.root) {
    val name = item.name
        val invite = item.invite
    }
}