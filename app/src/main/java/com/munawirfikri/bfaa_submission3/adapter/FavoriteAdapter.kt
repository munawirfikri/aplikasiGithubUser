package com.munawirfikri.bfaa_submission3.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.munawirfikri.bfaa_submission3.R
import com.munawirfikri.bfaa_submission3.databinding.ItemRowUserBinding
import com.munawirfikri.bfaa_submission3.model.User
import java.util.ArrayList

class FavoriteAdapter(private val activity: Activity) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>()
{
    var listFavorite = ArrayList<User>()
        set(listFavorite) {
            if (listFavorite.size > 0) {
                this.listFavorite.clear()
            }
            this.listFavorite.addAll(listFavorite)
            notifyDataSetChanged()
        }

    fun addItem(user: User) {
        this.listFavorite.add(user)
        notifyItemInserted(this.listFavorite.size - 1)
    }

    fun updateItem(position: Int, user: User) {
        this.listFavorite[position] = user
        notifyItemChanged(position, user)
    }

    fun removeItem(position: Int) {
        this.listFavorite.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, this.listFavorite.size)
    }

    private lateinit var onItemClickCallback: FavoriteAdapter.OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: FavoriteAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_user, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteAdapter.FavoriteViewHolder, position: Int) {
        val user = listFavorite[position]
        Glide.with(holder.itemView.context)
            .load(user.avatar)
            .apply(RequestOptions().override(60, 60))
            .into(holder.imgAvatar)

        holder.tvUsername.text = user.username
        holder.tvURL.text = user.userURL
        holder.tvID.text = null
        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(listFavorite[holder.adapterPosition]) }

    }

    override fun getItemCount(): Int = this.listFavorite.size

    inner class FavoriteViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView = itemView.findViewById(R.id.tv_item_username)
        var tvURL: TextView = itemView.findViewById(R.id.tv_item_url)
        var tvID: TextView = itemView.findViewById(R.id.tv_item_id)
        var imgAvatar: ImageView = itemView.findViewById(R.id.img_item_avatar)
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: User)
    }
}