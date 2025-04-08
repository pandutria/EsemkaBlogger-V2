package com.example.esemkablogger_v2.ui.fragment

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.adapter.ViewPagerAdapter2
import com.example.esemkablogger_v2.databinding.FragmentProfileBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.ui.activity.AddPostActivity
import com.example.esemkablogger_v2.ui.activity.EditProfileActivity
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject

class ProfileFragment : Fragment() {
    lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        binding.viewPager.adapter = ViewPagerAdapter2(childFragmentManager)
        binding.tablayout.setupWithViewPager(binding.viewPager)

        dataMe(this).execute()

        binding.btnUpdate.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(context, AddPostActivity::class.java))
        }


        return binding.root

    }

    override fun onResume() {
        super.onResume()
        dataMe(this).execute()
    }

    class dataMe(private var fragment: ProfileFragment): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(endpoint = "me", token = support.token)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                try {
                    var body = JSONObject(result).getString("body")
                    var code = JSONObject(result).getInt("code")

                    if (code == 200) {
                        var user = JSONObject(body)
                        var firstName = user.getString("firstName")
                        var lastName = user.getString("lastName")
                        var dateOfBirth = user.getString("dateOfBirth")
                        var joinDate = user.getString("joinDate")
                        var photo = user.getString("photo")


                        fragment.binding.tvName.text = "$firstName $lastName"
                        fragment.binding.tvDate.text = dateOfBirth.split("T")[0]
                        fragment.binding.tvJoin.text =  "Join at  ${joinDate.split("T")[0]}"

                        if (photo != null) {
                            support.showImage(fragment.binding.imgPhoto).execute(support.urlImage + photo)
                        }

                    }

                } catch (e: Exception) {
//                    support.log(e.message.toString())
                }
            }
        }
    }
}