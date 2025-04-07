package com.example.esemkablogger_v2.util

import android.content.Context

object mySharedPrefrence {
    var token = "token"
    var sharedKey = "sharedKey"

    fun saveToken(context: Context, tokenUser: String) {
        var shared = context.getSharedPreferences(sharedKey, Context.MODE_PRIVATE)
        with(shared.edit()) {
            putString(token, tokenUser)
            apply()
        }
    }

    fun getToken(context: Context) : String? {
        var shared = context.getSharedPreferences(sharedKey, Context.MODE_PRIVATE)
        return shared.getString(token, "")
    }


}