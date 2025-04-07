package com.example.esemkablogger_v2.ui.activity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.databinding.ActivityRegisterBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btn.setOnClickListener {
            if (binding.etUsername.text.toString() == "" || binding.etPassword.text.toString() == ""
                || binding.etFisrtname.text.toString() == "" || binding.etLastname.text.toString() == "" || binding.dateOfBirth.text.toString() == "") {
                support.msi(this, "All fields must be filled")
                return@setOnClickListener
            }

            register(this).execute()
        }
    }

    fun json(): String {
        var jo = JSONObject()
        try {
            jo.put("firstName", binding.etFisrtname.text)
            jo.put("lastName", binding.etLastname.text)
            jo.put("username", binding.etUsername.text)
            jo.put("password", binding.etPassword.text)
            jo.put("dateOfBirth", binding.dateOfBirth.text)
        }catch (e: Exception) {
            support.log(e.message.toString())
        }
        return jo.toString()
    }

    class register(private var activity: RegisterActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(method = "POST", endpoint = "auth/register", requestBody = activity.json())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var code = JSONObject(result).getInt("code")

                if (code == 201) {
                    activity.startActivity(Intent(activity, LoginActivity::class.java))
                    activity.finish()
                }

                if (code == 409) {
                    support.msi(activity, "Username already use")
                }
            } else {
                support.msi(activity, "Try again")
            }
        }
    }
}