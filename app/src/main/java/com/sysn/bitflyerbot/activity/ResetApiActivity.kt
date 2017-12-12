package com.sysn.bitflyerbot.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.sysn.bitflyerbot.R
import com.sysn.bitflyerbot.common.A
import com.sysn.bitflyerbot.common.F
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

/**
 * Created by shiny on 2017/12/11.
 */
class ResetApiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_main_save.setOnClickListener {
            A.apiKey = edit_main_key.text.toString()
            A.apiSecret = edit_main_sec.text.toString()

            F.saveJSON("user", JSONObject()
                    .put("key", A.apiKey)
                    .put("sec", A.apiSecret), this)

            startActivity(Intent(applicationContext, HomeActivity::class.java))
        }
    }
}