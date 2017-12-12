package com.sysn.bitflyerbot

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import com.sysn.bitflyerbot.activity.HomeActivity
import com.sysn.bitflyerbot.activity.home.TaskActivity
import com.sysn.bitflyerbot.api.GetInfo
import com.sysn.bitflyerbot.api.Order
import com.sysn.bitflyerbot.common.A
import com.sysn.bitflyerbot.common.F


/**
 * Created by shiny on 2017/12/11.
 */
class TaskBuy : Service() {
    var priceWhen: Int = 0
    var pricePer: Int = 0
    var isOpt: Boolean = false
    var intentAction = ""
    var intentInx = ""

    val handler = Handler()
    val run = arrayOfNulls<Runnable>(100)
    var inx: Int = 0

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("service", "onStartCommand")
        intentAction = intent.action
        intentInx = intent.getStringExtra("inx")
        inx = intent.action.toInt()

        if (intentAction == intentInx) {
            priceWhen = intent.getIntExtra("price_when", -1)
            pricePer = intent.getIntExtra("price_per", -1)
            isOpt = intent.getBooleanExtra("is_opt", false)

            if (priceWhen > 0) {
                run[inx] = (object : Runnable {
                    override fun run() {
                        val msg = F.getNowDate() + "_now_btc:" + GetInfo.priceNowBtcAsJpy.toString() + ",when:" + priceWhen + ",per:" + pricePer
                        Log.d("task_buy_live", msg)
                        F.outPutLog("task_buy_live.txt", msg, A.mainActivity)

                        if (!isTaskFinished) buy()

                        handler.postDelayed(this, 2000)
                    }
                })

                handler.post(run[inx])

                val activityIntent = Intent(this, HomeActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)
                val notification = Notification.Builder(this)
                        .setContentTitle("bitFlyerBot")
                        .setContentText("実行中")
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .build()
                startForeground(startId, notification)

                return Service.START_STICKY;
            } else {
                stopSelf()
                return super.onStartCommand(intent, flags, startId);
            }
        } else {
            stopSelf()
            return super.onStartCommand(intent, flags, startId);
        }
    }

    private var low: Int = -1
    private var isTaskFinished = false
    private fun buy() {
        fun togari(now: Int): Boolean {
            if (low == -1) low = now
            else {
                if (now < low) low = now
                else {
                    return true
                }
            }
            return false
        }

        GetInfo.priceNowBtcAsJpy?.let {
            fun job() {
                val btc: Double = GetInfo.priceMyBtc!!.toDouble() * (pricePer.toDouble() / 100.toDouble())

                Log.d("task", "buy: $btc")
                A.log += F.getNowDate() + "_buy: $btc\n"
                F.outPutLog("task_buy.txt", F.getNowDate() + ":buy $btc", A.mainActivity)

                if (!A.isTest)
                    Order(A.apiKey, A.apiSecret, null, false, it, btc).execute()

                TaskActivity.isTaskBuy = false
                isTaskFinished = true
                stopSelf()
            }
            if (it < priceWhen) {
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

        if (intentAction == intentInx) {
            handler.removeCallbacks(run[inx])
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}