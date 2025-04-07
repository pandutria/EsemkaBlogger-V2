package com.example.esemkablogger_v2.ui.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkablogger_v2.MainActivity
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.databinding.ActivityLoginBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etUsername.setText("string")
        binding.etPassword.setText("string")

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.etUsername.text.toString() == "" || binding.etPassword.text.toString() == "") {
                support.msi(this, "All fields must be filled")
                return@setOnClickListener
            }
            login(this).execute()
        }
    }

    fun json(): String {
        var jo = JSONObject()
        try {
            jo.put("username", binding.etUsername.text)
            jo.put("password", binding.etPassword.text)
        } catch (e: Exception) {
            support.log(e.message.toString())
        }
        return jo.toString()
    }

    class login(private var activity: LoginActivity) : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(
                endpoint = "auth/login",
                requestBody = activity.json(),
                method = "POST"
            )
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var code = JSONObject(result).getInt("code")
                var body = JSONObject(result).getString("body")

                var response = JSONObject(body)

                if (code == 200) {
                    mySharedPrefrence.saveToken(activity, response.getString("token"))
                    mySharedPrefrence.getToken(activity)
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                }

                if (code == 404) {
                    support.msi(activity, "${response.getString("title")}")
                }


            } else {
                support.msi(activity, "Please correct the error and try again")
            }
        }
    }
}