package com.example.esemkablogger_v2.ui.fragment.profileTab

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.adapter.PostAdapter
import com.example.esemkablogger_v2.databinding.FragmentMyLikeBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.model.Category
import com.example.esemkablogger_v2.model.Posts
import com.example.esemkablogger_v2.model.User
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONArray
import org.json.JSONObject

class MyLikeFragment : Fragment() {
    lateinit var binding: FragmentMyLikeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyLikeBinding.inflate(layoutInflater, container, false)
        showData(this).execute()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        showData(this).execute()
    }

    class showData(private var fragment: MyLikeFragment) : AsyncTask<Void, Void, Void>() {
        var postList: MutableList<Posts> = arrayListOf()
        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                var jsonUrl = HttpHandler().reqeust(endpoint = "me/post/liked", token = mySharedPrefrence.getToken(fragment.requireContext()))
                var response = JSONObject(jsonUrl)
                var code = response.getInt("code")
                var body = response.getString("body")

                var jsonArray = JSONArray(body)

                for (i in 0 until jsonArray.length()) {
                    var post = jsonArray.getJSONObject(i)
                    var id = post.getString("id")
                    var thumbnail = post.getString("thumbnail")

                    var user = post.getJSONObject("user")
                    var userId = user.getString("id")

                    postList.add(
                        Posts(
                            id = id,
                            thumbnail = thumbnail,
                            category = Category(),
                            user = User(id = userId)
                        )
                    )
                }

            } catch (e: Exception) {
                support.log(e.message.toString())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            fragment.binding.rv.adapter = PostAdapter(postList)
            fragment.binding.rv.layoutManager = GridLayoutManager(fragment.context, 2)
        }
    }
}