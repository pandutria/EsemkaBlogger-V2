package com.example.esemkablogger_v2.ui.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.adapter.PostsAdapter
import com.example.esemkablogger_v2.databinding.FragmentPostsBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.model.Category
import com.example.esemkablogger_v2.model.Posts
import com.example.esemkablogger_v2.model.User
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONArray
import org.json.JSONObject

class PostsFragment : Fragment() {
    lateinit var binding: FragmentPostsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostsBinding.inflate(layoutInflater, container, false)

//        binding.etSearch.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                showData(this@PostsFragment).execute()
//            }
//
//            override fun afterTextChanged(p0: Editable?) {
//
//            }
//        })

        binding.btb.setOnClickListener {
            showData(this).execute()
        }

        dataMe(this).execute()

        showData(this).execute()
        return binding.root
    }

    class dataMe(private var fragment: PostsFragment): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust("me", token = mySharedPrefrence.getToken(fragment.requireContext()))
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var body = JSONObject(result).getString("body")
                var code = JSONObject(result).getInt("code")

                if (code == 200) {
                    support.userId = JSONObject(body).getString("id")
                    support.log(JSONObject(body).getString("id"))
                }
            }
        }
    }

    class showData(private var fragment: PostsFragment): AsyncTask<Void, Void, Void>() {
        var postsList: MutableList<Posts> = arrayListOf()
        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                var jsonToUrl = HttpHandler().reqeust(endpoint = "posts")

                if (fragment.binding.etSearch.text.toString() != null) {
                    jsonToUrl = HttpHandler().reqeust(endpoint = "posts?title=${fragment.binding.etSearch.text}")
                }

//                if (fragment.binding.spinner.toString() != "")

                var response = JSONObject(jsonToUrl).getString("body")
                var jsonToArray = JSONArray(response)

                for (i in 0 until jsonToArray.length()) {
                    var post = jsonToArray.getJSONObject(i)
                    var id = post.getString("id")
                    var title = post.getString("title")
                    var content = post.getString("content")
                    var thumbnail = post.getString("thumbnail")
                    var imageContent = post.getString("imageContent")
                    var date = post.getString("date")
                    var like = post.getInt("likeCount")

                    var user = post.getJSONObject("user")
                    var user_id = user.getString("id")
                    var firstName = user.getString("firstName")
                    var lastName = user.getString("lastName")

                    var category = post.getJSONObject("category")
                    var category_id = category.getString("id")
                    var category_name = category.getString("name")

                    postsList.add(Posts(
                        id = id,
                        title = title,
                        content = content,
                        thumbnail = thumbnail,
                        date = date,
                        likeCount = like,
                        user = User(firstName = firstName, lastName = lastName, id = user_id),
                        category = Category(category_id , category_name)
                    ))

                }
            } catch (e: Exception) {
                support.log(e.message.toString())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            fragment.binding.rv.adapter = PostsAdapter(postsList)
            fragment.binding.rv.layoutManager = LinearLayoutManager(fragment.context)
        }
    }
}