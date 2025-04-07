package com.example.esemkablogger_v2.domain

import android.util.Log
import com.example.esemkablogger_v2.util.support
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class HttpHandler {
    fun reqeust(
        endpoint: String? = null,
        method: String? = "GET",
        requestBody: String? = null,
        token: String? = null,
       ) : String{

        var response = JSONObject()
        try {
            var url = URL(support.url + endpoint)
            var conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.setRequestProperty("Content-Type", "application/json")

            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer : $token")
            }

            if (requestBody != null) {
               conn.outputStream.use { it.write(requestBody.toByteArray()) }
            }
            var code = conn.responseCode
            var body = try {
                conn.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                conn.errorStream.bufferedReader().use { it.readText()}
            }

            response.put("code", code)
            response.put("body", body)

        } catch (e: Exception) {
            Log.d("HttpHandler", "Eror ${e.message}")
        } catch (e: IOException) {
            Log.d("HttpHandler", "Eror ${e.message}")
        }
        return response.toString()
    }
}