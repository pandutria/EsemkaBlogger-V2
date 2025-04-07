package com.example.esemkablogger_v2.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import java.net.URL

object support {
    var url = "http://10.0.2.2:5000/api/"
    var urlImage = url.replace("api/", "images/")

    var token = ""
    var userId = ""

    fun log (text: String) {
        Log.d("DataApi", "Eror : $text")
    }

    fun msi(context: Context, string: String) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    class showImage(private var imageView: ImageView): AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg p0: String?): Bitmap? {
            return try {
                var input = URL(p0[0]).openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: Exception) {
                log(e.message.toString())
                null
            }
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            imageView.setImageBitmap(result)
        }
    }
}