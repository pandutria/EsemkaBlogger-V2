package com.example.esemkablogger_v2.ui.activity

import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkablogger_v2.R
import com.example.esemkablogger_v2.databinding.ActivityEditProfileBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.ui.fragment.ProfileFragment
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject

class EditProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    var uriImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            if (binding.etUsername.text.toString() == "" || binding.etPassword.text.toString() == ""
                || binding.etFisrtname.text.toString() == "" || binding.etLastname.text.toString() == "" ) {
                support.msi(this, "All fields must be filled")
                return@setOnClickListener
            }

            if (binding.etPassword.text.toString() != binding.etConfirmPassword.text.toString()) {
                support.msi(this, "password and confirm password must be same")
                return@setOnClickListener
            }
            updateData(this).execute()
            uploadPhoto(this).execute()
        }

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uriImage = it
                val fileName = getFileNameFromUri(it)
                binding.etFiled.setText(fileName)
            }
        }

        binding.btnFile.setOnClickListener {
            getContent.launch("image/*")
        }

        dataMe(this).execute()
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var name = "file_tidak_diketahui"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = it.getString(nameIndex)
                }
            }
        }
        return name
    }

    class uploadPhoto(private var activity: EditProfileActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            try {
                val uri = activity.uriImage ?: return ""
                val contentResolver = activity.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val fileName = activity.getFileNameFromUri(uri)

                return HttpHandler().requestPhoto(
                    endpoint = "me/photo",
                    token = support.token,
                    fileName = fileName,
                    inputStream = inputStream!!
                )
            } catch (e: Exception) {
                support.log(e.message.toString())
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }

    class dataMe(private var fragment: EditProfileActivity): AsyncTask<String, Void, String>() {
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
                        var username = user.getString("username")

                        fragment.binding.etUsername.setText("$username")
                        fragment.binding.etFisrtname.setText("$firstName")
                        fragment.binding.etLastname.setText("$lastName")
                        fragment.binding.etPassword.setText("${support.password}")
                        fragment.binding.etConfirmPassword.setText("${support.password}")


                    }

                } catch (e: Exception) {
                    support.log(e.message.toString())
                }
            }
        }
    }

    fun json(): String {
        var jo = JSONObject()
        try {
            jo.put("username", binding.etUsername.text)
            jo.put("lastName", binding.etLastname.text)
            jo.put("firstName", binding.etFisrtname.text)
            jo.put("password", binding.etPassword.text)
        } catch (e: Exception) {
            support.log(e.message.toString())
        }
        return jo.toString()
    }

    class updateData(private var activity: EditProfileActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(endpoint = "me", method = "PUT", token = support.token, requestBody = activity.json())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                if (result!!.isNotEmpty()) {
                    var code = JSONObject(result).getInt("code")

                    if (code == 200) {
                        activity.finish()
                        support.password = activity.binding.etPassword.text.toString()
                    }

                    if (code == 401) {
                        support.msi(activity, "user is not authorized.")
                    }
                }
            } catch (e: Exception) {
                support.log(e.message.toString())
            }
        }
    }
}