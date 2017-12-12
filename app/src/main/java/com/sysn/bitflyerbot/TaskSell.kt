package com.sysn.bitflyerbot

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.sysn.bitflyerbot.activity.home.TaskActivity
import com.sysn.bitflyerbot.api.GetInfo
import com.sysn.bitflyerbot.api.Order
import com.sysn.bitflyerbot.common.A


/**
 * Created by shiny on 2017/12/11.
 */
class TaskSell : Service() {
    var priceWhen: Int = 0
    var pricePer: Int = 0
    var isOpt: Boolean = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("service", "onStartCommand")

        priceWhen = intent.getIntExtra("price_when", -1)
        pricePer = intent.getIntExtra("price_per", -1)
        isOpt = intent.getBooleanExtra("is_opt", false)

        if (priceWhen > 0) {
            val handler = Handler()
            Handler().post(object : Runnable {
                override fun run() {
                    GetInfo().getBtcInfo()
                    GetInfo().getMyInfo()

                    Log.d("task_sell_get_info", GetInfo.priceNowBtcAsJpy.toString() + "," + GetInfo.priceMyJpy + "," + GetInfo.priceMyBtc)

                    sell()

                    handler.postDelayed(this, 2000)
                }
            })
            return Service.START_STICKY;
        } else {
            stopSelf()
            return super.onStartCommand(intent, flags, startId);
        }
    }

    private var high: Int = -1
    private fun sell() {
        Log.d("task","sell")

        fun togari(now: Int): Boolean {
            if (high == -1) high = now
            else {
                if (now > high) high = now
                else {
                    return true
                }
            }
            return false
        }

        GetInfo.priceNowBtcAsJpy?.let {
            fun job() {
                val btc: Double = GetInfo.priceMyBtc!! * (pricePer / 100)
                Log.d("task", "sell: $btc")
                A.log+=java.lang.Long.toString(System.currentTimeMillis() / 1000)+"sell: $btc\n"

                if (!A.isTest)
                    Order(A.apiKey, A.apiSecret, null, false, it, btc).execute()

                TaskActivity.isTaskSell = false
                stopSelf()
            }
            if (it > priceWhen) {
                if (isOpt) {
                    if (togari(it)) job()
                } else {
                    job()
                }
            }
        }
    }


    override fun onDestroy() {
        Log.d("service_task", "onDestroy")
        super.onDestroy()

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}