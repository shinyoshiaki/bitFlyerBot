package com.sysn.bitflyerbot.activity.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.sysn.bitflyerbot.Task
import com.sysn.bitflyerbot.common.A.log
import com.sysn.bitflyerbot.common.F.Companion.getNowDate
import com.sysn.bitflyerbot.common.F.Companion.outPutLog
import kotlinx.android.synthetic.main.activity_task.view.*


/**
 * Created by shiny on 2017/12/11.
 */
class TaskActivity(val ac: Activity, val vw: View) {

    init {
        vw.btn_task_buyset.setOnClickListener {
            intentBuy = Intent(ac, Task.Buy::class.java)
            intentBuy!!
                    .putExtra("price_when", vw.edit_task_buywhen.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_buynum.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_buyopt.isChecked)
            ac.startService(intentBuy)
            isTaskBuy = true
            vw.btn_task_buyset.text = "実行中"

            Log.d("task_activity", "start buy task")
            log += getNowDate() + ":start buy task\n"
            outPutLog("task_sell.txt", getNowDate() + ":start buy task", ac)
        }

        vw.btn_task_sellset.setOnClickListener {
            intentSell = Intent(ac, Task.Sell::class.java)
            intentSell!!
                    .putExtra("price_when", vw.edit_task_sellwhen.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_sellnum.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_sellopt.isChecked)
            ac.startService(intentSell)
            isTaskSell = true
            vw.btn_task_sellset.text = "実行中"

            Log.d("task_activity", "start sell task")
            log += getNowDate() + ":start sell task\n"
            outPutLog("task_sell.txt", getNowDate() + ":start sell task", ac)
        }

        vw.btn_task_buyset2.setOnClickListener {
            intentBuySub = Intent(ac, Task.BuySub::class.java)
            intentBuySub!!
                    .putExtra("price_when", vw.edit_task_buywhen2.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_buynum2.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_buyopt2.isChecked)

            ac.startService(intentBuySub)
            isTaskBuy = true
            vw.btn_task_buyset2.text = "実行中"
        }

        vw.btn_task_sellset2.setOnClickListener {
            intentSellSub = Intent(ac, Task.SellSub::class.java)
            intentSellSub!!
                    .putExtra("price_when", vw.edit_task_sellwhen2.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_sellnum2.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_sellopt2.isChecked)
            ac.startService(intentSellSub)
            isTaskSell = true
            vw.btn_task_sellset2.text = "実行中"
        }
    }

    fun refresh() {

    }

    companion object {
        var isTaskBuy = false
        var isTaskSell = false
        var isTaskBuySub = false
        var isTaskSellSub = false

        var intentBuy: Intent? = null
        var intentBuySub: Intent? = null
        var intentSell: Intent? = null
        var intentSellSub: Intent? = null
    }
}