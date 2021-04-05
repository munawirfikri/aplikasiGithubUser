package com.munawirfikri.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.munawirfikri.consumerapp.R
import com.munawirfikri.consumerapp.model.User

class UserAdapter (private val listUser: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = listUser[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(60, 60))
            .into(holder.imgAvatar)

        holder.tvUsername.text = user.username
        holder.tvURL.text = user.userURL
        holder.tvID.text = StringBuilder("ID: ${user.id}")
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listUser[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tvUsername: TextView = itemView.findViewById(R.id.tv_item_username)
        var tvURL: TextView = itemView.findViewById(R.id.tv_item_url)
        var tvID: TextView = itemView.findViewById(R.id.tv_item_id)
        var imgAvatar: ImageView = itemView.findViewById(R.id.img_item_avatar)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}