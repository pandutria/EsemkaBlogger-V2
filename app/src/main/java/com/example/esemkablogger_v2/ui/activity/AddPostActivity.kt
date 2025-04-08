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
import com.example.esemkablogger_v2.databinding.ActivityAddPostBinding
import com.example.esemkablogger_v2.domain.HttpHandler
import com.example.esemkablogger_v2.util.mySharedPrefrence
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject

class AddPostActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPostBinding
    var post_id = ""

    var uriThumbnail: Uri? = null
    var uriImage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            if (binding.etTitel.text.toString() == "" || binding.etText.text.toString() == ""
                || binding.etFiledImage.text.toString() == "" || binding.etFileThumbnail.text.toString() == "") {
                support.msi(this, "All field must be filled")
                return@setOnClickListener
            }

            postData(this).execute()
            uploadThumbnail(this).execute()
            uploadImage(this).execute()
        }

        val getContentThumbnail = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uriThumbnail = it
                val fileName = getFileNameFromUriThumbnail(it)
                binding.etFileThumbnail.setText(fileName)
            }
        }

        val getContentImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uriImage = it
                val fileName = getFileNameFromUriImage(it)
                binding.etFiledImage.setText(fileName)
            }
        }

        binding.btnSelectThumbnail.setOnClickListener {
            getContentThumbnail.launch("image/*")
        }

        binding.btnSelectImage.setOnClickListener {
            getContentImage.launch("image/*")
        }
    }

    class uploadThumbnail(private var activity: AddPostActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            try {
                val uri = activity.uriThumbnail ?: return ""
                val contentResolver = activity.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val fileName = activity.getFileNameFromUriThumbnail(uri)

                return HttpHandler().requestPhoto(
                    endpoint = "posts/${activity.post_id}/thumbnail",
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

    class uploadImage(private var activity: AddPostActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String? {
            try {
                val uri = activity.uriImage ?: return ""
                val contentResolver = activity.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val fileName = activity.getFileNameFromUriImage(uri)

                return HttpHandler().requestPhoto(
                    endpoint = "posts/${activity.post_id}/image",
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


    private fun getFileNameFromUriImage(uri: Uri): String {
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

    private fun getFileNameFromUriThumbnail(uri: Uri): String {
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


    fun json(): String {
        var j = JSONObject()
        try {
            j.put("categoryId", "34b20b74-8f29-451b-a6da-7e9d2ba6b731")
            j.put("titel", binding.etTitel.text)
            j.put("content", binding.etText.text)
        } catch (e: Exception) {
            support.msi(this, e.message.toString())
        }
        return j.toString()
    }

    class postData(private var activity: AddPostActivity): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg p0: String?): String {
            return HttpHandler().reqeust(endpoint = "posts", method = "POST", token = mySharedPrefrence.getToken(activity), requestBody = activity.json())
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result!!.isNotEmpty()) {
                var code = JSONObject(result).getInt("code")
                var body = JSONObject(result).getString("body")

                if (code == 201) {
                    activity.finish()
                    activity.post_id = JSONObject(body).getString("id")
                }
            }
        }
    }
}