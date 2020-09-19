package com.ishanvohra.dateline.adapters

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ishanvohra.dateline.R
import com.ishanvohra.dateline.data.User

class DoubleTakeAdapter : RecyclerView.Adapter<DoubleTakeAdapter.MyViewHolder>() {

    public var profiles: ArrayList<User>? = null
    public var context: Context? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nameTv: TextView = itemView.findViewById(R.id.name_tv)
        var ageTv: TextView = itemView.findViewById(R.id.age_tv)
        var profilePic: ImageView = itemView.findViewById(R.id.profile_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doubletake_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return profiles?.size?: 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user : User = profiles?.get(position)?: User()

        holder.nameTv.setText(user.name)
        holder.ageTv.setText(user.age + ", " + user.gender)

        context?.let {
            Glide.with(it)
                .load(user.imageUrl)
                .into(holder.profilePic)
        }
    }

}