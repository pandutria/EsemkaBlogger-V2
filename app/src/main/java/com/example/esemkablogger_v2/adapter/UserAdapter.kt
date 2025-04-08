package com.example.esemkablogger_v2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkablogger_v2.databinding.ItemUsersBinding
import com.example.esemkablogger_v2.model.User
import com.example.esemkablogger_v2.util.support

class UserAdapter(private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    class ViewHolder(var binding: ItemUsersBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = ItemUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var user = userList[position]
        holder.binding.tvName.text = "${user.firstName} ${user.lastName}"
        holder.binding.tvDate.text = "Join Date: ${user.joinDate!!.split("T")[0]}"
        support.showImage(holder.binding.imgPhoto).execute(support.urlImage + user.photo)

    }

    override fun getItemCount(): Int {
        return userList.size
    }
}