package com.sysn.bitflyerbot.api

/**
 * Created by shiny on 2017/12/11.
 */

import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.sysn.bitflyerbot.common.A
import com.sysn.bitflyerbot.common.Encrypt
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL

class Order(private val apiKey: String, private val apiSecret: String, private val cont: Context?, uri: Boolean,
            /** 価格  */
            private val price: Int,
            /** 注文数量  */
            private val size: Double) : AsyncTask<Void, Void, String>() {

    /** プロダクト  */
    private val productCode: String
    /** 注文方法  */
    private var childOrderType: String? = null
    /** 売買  */
    private var side: String? = null

    init {
        this.productCode = PRODUCT_CODE_BTC

        this.childOrderType = CHILD_ORDER_TYPE_SASHINE

        if (uri) {
            this.side = SIDE_URI
        } else {
            this.side = SIDE_KAI
        }
    }

    override fun doInBackground(vararg params: Void): String? {
        if (A.isTest) return null

        val unixTimestampStr = java.lang.Long.toString(System.currentTimeMillis() / 1000)
        val bodyJson = JSONObject()
        try {
            bodyJson.put("product_code", productCode)
            bodyJson.put("child_order_type", childOrderType)
            bodyJson.put("side", side)
            bodyJson.put("price", price)
            bodyJson.put("size", size)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = bodyJson.toString()
        val data = unixTimestampStr + METHOD + PATH + body
        val hash = Encrypt.sha256(apiSecret, data)

        var conn: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var line: String? = null
        try {
            val urlStr = BASE_URL + PATH
            val url = URL(urlStr)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = METHOD
            conn.setRequestProperty("ACCESS-KEY", apiKey)
            conn.setRequestProperty("ACCESS-TIMESTAMP", unixTimestampStr)
            conn.setRequestProperty("ACCESS-SIGN", hash)
            conn.setRequestProperty("Content-Type", "application/json")
            val printWriter = PrintWriter(conn.outputStream)
            printWriter.print(body)
            printWriter.close()
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

        return line
    }

    override fun onPostExecute(s: String?) {
        cont.let {
            if (s != null) {
                Toast.makeText(it, "注文成功", Toast.LENGTH_LONG).show()
            } else {
                if (A.isTest) Toast.makeText(it, "注文失敗(テスト環境です)", Toast.LENGTH_LONG).show()
                else Toast.makeText(it, "注文失敗", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private val BASE_URL = "https://api.bitflyer.jp"
        private val PATH = "/v1/me/sendchildorder"
        private val METHOD = "POST"

        private val PRODUCT_CODE_BTC = "BTC_JPY"
        private val CHILD_ORDER_TYPE_SASHINE = "LIMIT"
        //        private val CHILD_ORDER_TYPE_NARIYUKI = "MARKET"
        private val SIDE_URI = "SELL"
        private val SIDE_KAI = "BUY"
    }
}
