package com.thryv.bible.models

import android.app.Activity
import android.content.Context

class PrefsWrapper {
    companion object {
        fun saveString(key: String, string: String, context: Context?) {
            context?.getSharedPreferences("", Activity.MODE_PRIVATE)?.edit()?.putString(key, string)?.apply()
        }

        fun getString(key: String, context: Context?): String? {
            val prefs = context?.getSharedPreferences("", Activity.MODE_PRIVATE)
            return prefs?.getString(key, "")
        }

        fun saveLong(key: String, value: Long, context: Context?) {
            context?.getSharedPreferences("", Activity.MODE_PRIVATE)?.edit()?.putLong(key, value)?.apply()
        }

        fun getLong(key: String, context: Context?): Long? {
            val prefs = context?.getSharedPreferences("", Activity.MODE_PRIVATE)
            return prefs?.getLong(key, -1)
        }

        fun saveBoolean(key: String, value: Boolean, context: Context?) {
            context?.getSharedPreferences("", Activity.MODE_PRIVATE)?.edit()?.putBoolean(key, value)?.apply()
        }

        fun getBoolean(key: String, context: Context?): Boolean? {
            val prefs = context?.getSharedPreferences("", Activity.MODE_PRIVATE)
            return prefs?.getBoolean(key, false)
        }
    }
}
