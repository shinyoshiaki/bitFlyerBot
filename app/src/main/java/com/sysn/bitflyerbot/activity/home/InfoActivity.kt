package com.sysn.bitflyerbot.activity.home

import android.app.Activity
import android.os.AsyncTask
import android.os.Handler
import android.view.View
import com.sysn.bitflyerbot.api.GetInfo
import com.sysn.bitflyerbot.common.A
import com.sysn.bitflyerbot.common.Encrypt
import kotlinx.android.synthetic.main.activity_info.view.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shiny on 2017/12/11.
 */
class InfoActivity(val ac: Activity, val vw: View) {
    init {
        val handler = Handler()
        Handler().post(object : Runnable {
            override fun run() {

                GetInfo.priceNowBtcAsJpy.let {
                    vw.text_info_nowbtc.text = it.toString()
                }
                GetInfo.priceMyJpy.let {
                    vw.text_info_myjpy.text = it.toString()
                }
                GetInfo.priceMyBtc.let {
                    vw.text_info_mybtc.text = it.toString()
                }

                handler.postDelayed(this, 1000)
            }
        })
    }

    fun refresh() {
        GetInfo().getMyInfo()
        GetInfo().getBtcInfo()

        vw.text_info_log.text = A.log
    }

}