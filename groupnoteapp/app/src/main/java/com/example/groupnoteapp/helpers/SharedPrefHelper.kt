package com.example.groupnoteapp.helpers

import android.content.Context
import android.content.SharedPreferences

object SharedPrefHelper {
    private const val PREF_NAME = "NoteAppPrefs"
    private const val KEY_USERNAME = "username"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun clearUsername() {
        prefs.edit().remove(KEY_USERNAME).apply()
    }
}