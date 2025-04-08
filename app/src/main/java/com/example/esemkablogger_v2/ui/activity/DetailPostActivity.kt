package com.example.esemkablogger_v2.ui.activity

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.adapter.PostsAdapter
import com.example.esemkablogger_v2.databinding.ActivityDetailPostBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.model.Category
import com.example.esemkablogger_v2.model.Posts
import com.example.esemkablogger_v2.model.User
import com.example.esemkablogger_v2.ui.fragment.PostsFragment
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONArray
import org.json.JSONObject

class DetailPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetailPostBinding
    var postsId = ""
    var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postsId = intent.getStringExtra("id").toString()
        userId = intent.getStringExtra("userId").toString()
//        support.l

        if (userId == support.userId) {
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnLike.visibility = View.GONE
        } else {
            binding.btnDelete.visibility = View.GONE
            binding.btnLike.visibility = View.VISIBLE
        }

        binding.btnLike.setOnClickListener {
            like(this).execute()
        }

        binding.btnDelete.setOnClickListener {
            deleteData(this).execute()
        }

        isLiked(this).execute()

        showData(this).execute()

    }

    class deleteData(private var activity: DetailPostActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(endpoint = "posts/${activity.postsId}", method = "DELETE", token = mySharedPrefrence.getToken(activity))
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                try {
                    var code = JSONObject(result).getInt("code")

                    if (code == 204) {
                        activity.finish()
                    }
                } catch (e: Exception) {
                    support.log(e.message.toString())
                }

            }
        }
    }

    fun json(): String {
        var j = JSONObject()
        j.put("postId", postsId)
        return j.toString()
    }

    class like(private var activity: DetailPostActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(endpoint = "posts/like", method = "POST", requestBody = activity.json(), token = mySharedPrefrence.getToken(activity))
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            var body = JSONObject(result).getString("body")
            var code = JSONObject(result).getInt("code")

            if (code == 200) {
                if (activity.binding.btnLike.text == "Like") {
                    activity.binding.btnLike.text = "Unlike"
                } else {
                    activity.binding.btnLike.text = "Like"
                }
                showData(activity).execute()
            }
        }
    }

    class isLiked(private var activity: DetailPostActivity): AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust("me/is-liked/post/${activity.postsId}")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var body = JSONObject(result).getString("body")
                var code = JSONObject(result).getInt("code")

                if (code == 200) {
                    if (JSONObject(body).toString() == "false") {
                        activity.binding.btnLike.text = "Liked"
                    } else {
                        activity.binding.tvLike.text = "Unlike"
                    }
                }
            }
        }
    }

    class showData(private var activity: DetailPostActivity) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust("posts/${activity.postsId}")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var body = JSONObject(result).getString("body")
                var code = JSONObject(result).getInt("code")

                if (code == 200) {
                    var response = JSONObject(body)
                    activity.binding.tvDate.text = response.getString("date")
                    activity.binding.tvTitile.text = response.getString("title")
                    activity.binding.tvContent.text = response.getString("content")
                    activity.binding.tvLike.text = "${response.getInt("likeCount")} Likes"

                    var category = response.getJSONObject("category")
                    activity.binding.tvCategory.text = category.getString("name")


                    var user = response.getJSONObject("user")
                    activity.binding.tvName.text =
                        "${user.getString("firstName")} ${user.getString("lastName")}"

                    support.showImage(activity.binding.img)
                        .execute(support.urlImage + response.getString("imageContent"))

                }

            }
        }
    }
}