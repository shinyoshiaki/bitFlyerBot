package com.sysn.bitflyerbot.common

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import org.json.JSONException
import org.json.JSONObject

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
    }
}