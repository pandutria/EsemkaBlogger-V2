package com.example.esemkablogger_v2.ui.fragment

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.adapter.UserAdapter
import com.example.esemkablogger_v2.databinding.FragmentUsersBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.model.User
import com.example.esemkablogger_v2.util.support
import org.json.JSONArray
import org.json.JSONObject

class UsersFragment : Fragment() {
    lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        showData(this).execute()
        return binding.root
    }

    class showData(private var fragment: UsersFragment): AsyncTask<Void, Void, Void>() {
        var userList: MutableList<User> = arrayListOf()
        override fun doInBackground(vararg p0: Void?): Void? {
            try {
                var jsonToUrl = HttpHandler().reqeust(endpoint = "users")
                var body = JSONObject(jsonToUrl).getString("body")
                var jsonToAray = JSONArray(body)

                for (i in 0 until jsonToAray.length()) {
                    var user = jsonToAray.getJSONObject(i)
                    var id = user.getString("id")
                    var firstName = user.getString("firstName")
                    var lastName = user.getString("lastName")
                    var joinDate = user.getString("joinDate")
                    var photo = user.getString("photo")

                    userList.add(User(
                        id = id,
                        firstName = firstName,
                        lastName = lastName,
                        joinDate = joinDate,
                        photo = photo
                    ))
                }
            } catch (e: Exception) {
                support.log(e.message.toString())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            fragment.binding.rv.adapter = UserAdapter(userList)
            fragment.binding.rv.layoutManager = GridLayoutManager(fragment.context, 2)
        }
    }
}