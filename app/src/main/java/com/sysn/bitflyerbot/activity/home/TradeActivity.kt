package com.sysn.bitflyerbot.activity.home

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.view.View
import com.sysn.bitflyerbot.api.OrderList
import com.sysn.bitflyerbot.common.A
import kotlinx.android.synthetic.main.activity_trade.view.*
import org.json.JSONException

/**
 * Created by shiny on 2017/12/11.
 */
class TradeActivity(val ac: Activity, val vw: View) {
    init {

    }

    @SuppressLint("SetTextI18n")
    fun refresh() {
        OrderList(A.apiKey, A.apiSecret).execute()

        vw.text_trade_log.text = "注文履歴\n"
        Handler().postDelayed({
            OrderList.orderListJsonArr?.let {
                for (i in 0 until it.length()) {
                    try {
                        it.getJSONObject(i).let {
                            val sideJ = it.getString("side")
                            val price = it.getString("price")
                            val size = it.getString("size")
                            val date = it.getString("child_order_date")
                            ac.runOnUiThread {
                                val str = vw.text_trade_log.text.toString()
                                vw.text_trade_log.text = str +
                                        "side: $sideJ \n" +
                                        "price: $price\n" +
                                        "size: $size\n" +
                                        "date: $date\n\n"
                            }
                        }
                    } catch (e: JSONException) {
                    }
                }
            }
        }, (2000).toLong())
    }
}