package com.example.safetrace

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//3 here we created the adapter we inherit it with recyclerView.adapter,
// goto home fragment

//it has a primary constructor which takes a list of memberModel
//it takes the viewHolder as a parameter
class MemberAdapter(private val listMember: List<memberModel>) :
    RecyclerView.Adapter<MemberAdapter.ViewHolder>() {
    //here we implemented abstracts methods of adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val item = inflater.inflate(R.layout.item_member, parent, false)
        return ViewHolder(item)
//    it passes the inflated view to the viewHolder
    }

    override fun onBindViewHolder(holder: MemberAdapter.ViewHolder, position: Int) {
        //it binds the data to the views of the viewholder
        //it takes the position of the each item in the list and
        // binds it to the views of the viewholder that we created in onCreateViewHolder
        val item = listMember[position]
        holder.imageUser.setImageResource(item.image)
        holder.name.text = item.name
    }

    override fun getItemCount(): Int {
        return listMember.size
    }


    class ViewHolder(private val item: View) : RecyclerView.ViewHolder(item) {
        //  here we hold the views of the item we created in item_member.xml
        val imageUser = item.findViewById<ImageView>(R.id.img_user)
        val name = item.findViewById<TextView>(R.id.name)
    }
}