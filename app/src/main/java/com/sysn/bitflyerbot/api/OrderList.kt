package com.sysn.bitflyerbot.api

/**
 * Created by shiny on 2017/12/11.
 */

import android.os.AsyncTask
import com.sysn.bitflyerbot.common.Encrypt
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

class OrderList(private val apiKey: String, private val apiSecret: String) : AsyncTask<Void, Void, List<OrderList.Order>>() {
    private val BASE_URL = "https://api.bitflyer.jp"
    private val PATH = "/v1/me/getchildorders?child_order_state=ACTIVE"
    private val METHOD = "GET"

    override fun doInBackground(vararg params: Void): List<Order> {

        val unixTimestampStr = java.lang.Long.toString(System.currentTimeMillis() / 1000)
        val data = unixTimestampStr + METHOD + PATH

        val result = ArrayList<Order>()

        Encrypt.sha256(apiSecret, data)?.let {
            var conn: HttpURLConnection? = null
            var reader: BufferedReader? = null
            var line: String? = null
            try {
                val urlStr = BASE_URL + PATH
                val url = URL(urlStr)
                conn = url.openConnection() as HttpURLConnection
                conn.setRequestProperty("ACCESS-KEY", apiKey)
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
                        val order = Order()
                        val obj = array.getJSONObject(i)
                        order.id = obj.getLong("id")
                        order.childOrderId = obj.getString("child_order_id")
                        order.productCode = obj.getString("product_code")
                        order.side = obj.getString("side")
                        order.childOrderType = obj.getString("child_order_type")
                        order.price = obj.getInt("price")
                        order.averagePrice = obj.getInt("average_price")
                        order.size = obj.getDouble("size")
                        order.childOrderState = obj.getString("child_order_state")
                        order.setExpireDate(obj.getString("expire_date"))
                        order.setChildOrderDate(obj.getString("child_order_date"))
                        order.childOrderAcceptanceId = obj.getString("child_order_acceptance_id")
                        order.outstandingSize = obj.getDouble("outstanding_size")
                        order.cancelSize = obj.getDouble("cancel_size")
                        order.executedSize = obj.getDouble("executed_size")
                        order.totalCommission = obj.getDouble("total_commission")
                        result.add(order)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        return result
    }

    override fun onPostExecute(orders: List<Order>) {
        orderListJsonArr = JSONArray()

        for (order in orders) {
            var sideJ = order.side
            if (sideJ == "BUY") {
                sideJ = "買い"
            } else if (sideJ == "SELL") {
                sideJ = "売り"
            }
            orderListJsonArr?.put(JSONObject()
                    .put("side", sideJ)
                    .put("price", Integer.valueOf(order.price)!!.toString())
                    .put("size", java.lang.Double.valueOf(order.size)!!.toString())
                    .put("child_order_date", order.childOrderDateString)
            )
        }

        println("log:" + orderListJsonArr.toString())
    }

    inner class Order {

        /** id  */
        var id: Long = 0
        /** child_order_id  */
        var childOrderId: String? = null
        /** product_code  */
        var productCode: String? = null
        /** side  */
        var side: String? = null
        /** child_order_type  */
        var childOrderType: String? = null
        /** price  */
        var price: Int = 0
        /** average_price  */
        var averagePrice: Int = 0
        /** size  */
        var size: Double = 0.toDouble()
        /** child_order_state  */
        var childOrderState: String? = null
        /** expire_date  */
        var expireDate: Date? = null
            private set
        /** child_order_date  */
        var childOrderDate: Date? = null
            private set
        /** child_order_acceptance_id  */
        var childOrderAcceptanceId: String? = null
        /** outstanding_size  */
        var outstandingSize: Double = 0.toDouble()
        /** cancel_size  */
        var cancelSize: Double = 0.toDouble()
        /** executed_size  */
        var executedSize: Double = 0.toDouble()
        /** total_commission  */
        var totalCommission: Double = 0.toDouble()

        val expireDateString: String
            get() {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                sdf.timeZone = TimeZone.getDefault()
                return sdf.format(expireDate)
            }

        val childOrderDateString: String
            get() {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                sdf.timeZone = TimeZone.getDefault()
                return sdf.format(childOrderDate)
            }

        fun setExpireDate(expireDate: String) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            try {
                this.expireDate = sdf.parse(expireDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }

        fun setChildOrderDate(childOrderDate: String) {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            try {
                this.childOrderDate = sdf.parse(childOrderDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

        }
    }

    companion object {
        var orderListJsonArr: JSONArray? = null
    }
}
