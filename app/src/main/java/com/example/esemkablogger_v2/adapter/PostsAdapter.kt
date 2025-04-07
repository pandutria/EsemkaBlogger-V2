package com.example.esemkablogger_v2.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkablogger_v2.databinding.ItemPostsBinding
import com.example.esemkablogger_v2.model.Posts
import com.example.esemkablogger_v2.ui.activity.DetailPostActivity
import com.example.esemkablogger_v2.util.support

class PostsAdapter(private var postsList: List<Posts>): RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemPostsBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = ItemPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var post = postsList[position]
        holder.binding.tvName.text = "${post.user.firstName} ${post.user.lastName}"
        holder.binding.tvCategory.text = post.category.name
        holder.binding.tvDate.text = post.date
        holder.binding.tvContent.text = post.content
        holder.binding.tvTitile.text = post.title
        holder.binding.tvLike.text = "${post.likeCount} Likes"
        support.showImage(holder.binding.imgImage).execute(support.urlImage + post.thumbnail)

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context, DetailPostActivity::class.java).apply {
                putExtra("id", post.id)
                putExtra("userId", post.user.id)
            })
        }
    }

    override fun getItemCount(): Int {
        return postsList.size
    }

}