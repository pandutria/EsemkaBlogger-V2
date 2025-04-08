package com.example.esemkablogger_v2.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkablogger_v2.databinding.ItemPostBinding
import com.example.esemkablogger_v2.model.Posts
import com.example.esemkablogger_v2.ui.activity.DetailPostActivity
import com.example.esemkablogger_v2.util.support

class PostAdapter(private var postsList: List<Posts>): RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var post = postsList[position]
        support.showImage(holder.binding.img).execute(support.urlImage + post.thumbnail)

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