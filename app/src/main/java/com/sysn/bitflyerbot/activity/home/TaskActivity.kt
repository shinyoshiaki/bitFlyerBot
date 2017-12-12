package com.sysn.bitflyerbot.activity.home

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import com.sysn.bitflyerbot.TaskBuy
import com.sysn.bitflyerbot.TaskSell
import com.sysn.bitflyerbot.common.A
import kotlinx.android.synthetic.main.activity_task.view.*


/**
 * Created by shiny on 2017/12/11.
 */
class TaskActivity(val ac: Activity, val vw: View) {
    init {
        vw.btn_task_buyset.setOnClickListener {
            val intent = Intent(ac, TaskBuy::class.java)
            intent
                    .putExtra("price_when", vw.edit_task_buywhen.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_buynum.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_buyopt.isChecked)
            ac.startService(intent)
            isTaskBuy = true
            vw.btn_task_buyset.text = "実行中"
            Log.d("task_activity", "start buy task")
            A.log+=java.lang.Long.toString(System.currentTimeMillis() / 1000)+":start buy task\n"
        }
        vw.btn_task_sellset.setOnClickListener {
            val intent = Intent(A.mainActivity, TaskSell::class.java)
            intent
                    .putExtra("price_when", vw.edit_task_sellwhen.text.toString().toInt())
                    .putExtra("price_per", vw.edit_task_sellnum.text.toString().toInt())
                    .putExtra("is_opt", vw.check_task_sellopt.isChecked)
            A.mainActivity.startService(intent)
            isTaskSell = true
            vw.btn_task_sellset.text = "実行中"
            Log.d("task_activity", "start sell task")
            A.log+=java.lang.Long.toString(System.currentTimeMillis() / 1000)+":start sell task\n"
        }
    }

    fun refresh() {

    }

    companion object {
        var isTaskBuy = false
        var isTaskSell = false
    }
}