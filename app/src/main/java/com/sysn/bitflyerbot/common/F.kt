package com.sysn.bitflyerbot.common

import android.app.Activity
import android.content.Context.*
import android.icu.text.SimpleDateFormat
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by shiny on 2017/12/11.
 */
class F {
    companion object {
        fun saveJSON(address: String, jsonObject: JSONObject, activity: Activity) {
            val preferences = activity.getSharedPreferences("database", MODE_PRIVATE)
            preferences.edit().putString(address, jsonObject.toString()).apply()
        }

        fun loadJSON(address: String, activity: Activity): JSONObject? {
            val preferences = activity.getSharedPreferences("database", MODE_PRIVATE)
            return try {
                JSONObject(preferences.getString(address, "null"))
            } catch (e: JSONException) {
                null
            }
        }

        fun outPutLog(filename: String, value: String, activity: Activity) {
            try {
                val out = activity.openFileOutput("$filename", MODE_APPEND)
                out.write((value + "\n").toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        fun getNowDate(): String {
            val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date(System.currentTimeMillis())
            return df.format(date)
        }
    }
}