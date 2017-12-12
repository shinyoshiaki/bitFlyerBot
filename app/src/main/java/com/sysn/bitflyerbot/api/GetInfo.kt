package com.sysn.bitflyerbot.api

import android.os.AsyncTask
import com.sysn.bitflyerbot.common.A
import com.sysn.bitflyerbot.common.Encrypt
import com.sysn.bitflyerbot.common.F
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
class GetInfo {
    private inner class GetMyInfo : AsyncTask<Void, Void, List<GetMyInfo.Info>>() {
        private val BASE_URL = "https://api.bitflyer.jp"
        private val PATH = "/v1/me/getbalance"
        private val METHOD = "GET"

        val unixTimestampStr = java.lang.Long.toString(System.currentTimeMillis() / 1000)
        val data = unixTimestampStr + METHOD + PATH
        val result = ArrayList<Info>()

        inner class Info {
            var currencyCode: String? = null
            var amount: Double = 0.toDouble()
            var available: Double = 0.toDouble()
        }

        override fun doInBackground(vararg params: Void): List<GetMyInfo.Info>? {
            Encrypt.sha256(A.apiSecret, data)?.let {
                var conn: HttpURLConnection? = null
                var reader: BufferedReader? = null
                var line: String? = null
                try {
                    val urlStr = BASE_URL + PATH
                    val url = URL(urlStr)
                    conn = url.openConnection() as HttpURLConnection
                    conn.setRequestProperty("ACCESS-KEY", A.apiKey)
                    conn.setRequestProperty("ACCESS-TIMESTAMP", unixTimestampStr)
                    conn.setRequestProperty("ACCESS-SIGN", it)
                    reader = BufferedReader(InputStreamReader(conn.inputStream))
                    line = reader.readLine()

                } catch (ioe: IOException) {
                    ioe.printStackTrace()

                } finally {
                    if (conn != null) {
                        conn.disconnect()
                    }
                    if (reader != null) {
                        try {
                            reader.close()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                }

                if (line != null) {
                    try {
                        val array = JSONArray(line)
                        for (i in 0 until array.length()) {
                            val info = Info()
                            val obj = array.getJSONObject(i)
                            info.currencyCode = obj.getString("currency_code")
                            info.amount = obj.getDouble("amount")
                            info.available = obj.getDouble("available")
                            result.add(info)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }
            return result
        }

        override fun onPostExecute(infos: List<Info>) {
            for (info in infos) {
                when (info.currencyCode) {
                    "JPY" -> {
                        priceMyJpy = info.available
                    }
                    "BTC" -> {
                        //vw.text_info_mybtc.text = java.lang.Double.toString(info.available) + " BTC"
                        priceMyBtc = info.available
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun getMyInfo() {
        GetMyInfo().execute()
    }


    private inner class GetBtcInfo : AsyncTask<Void, Void, GetBtcInfo.Info>() {
        private val BASE_URL = "https://api.bitflyer.jp/v1/getticker"
        var conn: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var line: String? = null

        inner class Info {

            /**
             * product_code
             */
            var productCode: String? = null
            /**
             * timestamp
             */
            var timestamp: Date? = null
                private set
            /**
             * tick_id
             */
            var tickId: Long = 0
            /**
             * best_bid
             */
            var bestBid: Int = 0
            /**
             * best_ask
             */
            var bestAsk: Int = 0
            /**
             * best_bid_size
             */
            var bestBidSize: Double = 0.toDouble()
            /**
             * best_ask_size
             */
            var bestAskSize: Double = 0.toDouble()
            /**
             * total_bid_depth
             */
            var totalBidDepth: Double = 0.toDouble()
            /**
             * total_ask_depth
             */
            var totalAskDepth: Double = 0.toDouble()
            /**
             * ltp
             */
            var ltp: Int = 0
            /**
             * volume
             */
            var volume: Double = 0.toDouble()
            /**
             * volume_by_product
             */
            var volumeByProduct: Double = 0.toDouble()

            val timestampString: String
                get() {
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    sdf.timeZone = TimeZone.getDefault()
                    return sdf.format(timestamp)
                }

            fun setTimestamp(timestamp: String) {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                try {
                    this.timestamp = sdf.parse(timestamp)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }
        }

        override fun doInBackground(vararg params: Void): Info? {
            try {
                val urlStr = BASE_URL + "?product_code=" + "BTC_JPY";
                val url = URL(urlStr)
                conn = url.openConnection() as HttpURLConnection
                reader = BufferedReader(InputStreamReader(conn!!.getInputStream()))
                line = reader!!.readLine()

            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }

            conn?.disconnect()
            reader?.close()

            val info = Info()
            line.let {
                try {
                    val obj = JSONObject(it)
                    info.productCode = obj.getString("product_code")
                    info.setTimestamp(obj.getString("timestamp"))
                    info.tickId = obj.getLong("tick_id")
                    info.bestBid = obj.getInt("best_bid")
                    info.bestAsk = obj.getInt("best_ask")
                    info.bestBidSize = obj.getDouble("best_bid_size")
                    info.bestAskSize = obj.getDouble("best_ask_size")
                    info.totalBidDepth = obj.getDouble("total_bid_depth")
                    info.totalAskDepth = obj.getDouble("total_ask_depth")
                    info.ltp = obj.getInt("ltp")
                    info.volume = obj.getDouble("volume")
                    info.volumeByProduct = obj.getDouble("volume_by_product")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            return info
        }

        override fun onPostExecute(info: GetBtcInfo.Info) {
            priceNowBtcAsJpy = info.ltp
            //vw.text_info_nowbtc.text = info.productCode + "価格 : " + Integer.toString(info.ltp)
        }
    }

    fun getBtcInfo() {
        GetBtcInfo().execute()
    }

    companion object {
        var priceNowBtcAsJpy: Int? = null
        var priceMyJpy: Double? = null
        var priceMyBtc: Double? = null
    }
}