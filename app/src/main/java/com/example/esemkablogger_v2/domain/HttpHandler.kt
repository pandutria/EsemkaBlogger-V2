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
                conn.setRequestProperty("Authorization", "Bearer $token")
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

    fun requestPhoto(
        endpoint: String? = null,
        token: String? = null,
        fileName: String,
        inputStream: java.io.InputStream
    ): String {
        var response = JSONObject()
        try {
            val boundary = "Boundary-" + System.currentTimeMillis()
            val lineEnd = "\r\n"
            val twoHyphens = "--"

            val url = URL(support.url + endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.doOutput = true
            connection.useCaches = false
            connection.requestMethod = "POST"
            connection.setRequestProperty("Connection", "Keep-Alive")
            connection.setRequestProperty("ENCTYPE", "multipart/form-data")
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=$boundary")
            if (token != null) {
                connection.setRequestProperty("Authorization", "Bearer $token")
            }

            val outputStream = connection.outputStream
            val writer = outputStream.bufferedWriter()

            writer.write(twoHyphens + boundary + lineEnd)
            writer.write("Content-Disposition: form-data; name=\"photo\"; filename=\"$fileName\"$lineEnd")
            writer.write("Content-Type: image/jpeg$lineEnd")
            writer.write(lineEnd)
            writer.flush()

            inputStream.copyTo(outputStream)
            outputStream.write(lineEnd.toByteArray())

            writer.write(twoHyphens + boundary + twoHyphens + lineEnd)
            writer.flush()

            writer.close()
            outputStream.close()

            val code = connection.responseCode
            val body = try {
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                connection.errorStream.bufferedReader().use { it.readText() }
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