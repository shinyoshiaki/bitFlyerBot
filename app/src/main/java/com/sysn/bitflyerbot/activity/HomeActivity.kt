package com.sysn.bitflyerbot.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.sysn.bitflyerbot.R
import com.sysn.bitflyerbot.activity.home.InfoActivity
import com.sysn.bitflyerbot.activity.home.StateActivity
import com.sysn.bitflyerbot.activity.home.TaskActivity
import com.sysn.bitflyerbot.activity.home.TradeActivity
import com.sysn.bitflyerbot.api.GetInfo
import kotlinx.android.synthetic.main.common_activity_tablayout.*

/**
 * Created by shiny on 2017/12/11.
 */
class HomeActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    val PAGENUM = 4
    var activity: Activity = this

    companion object {
        var infoActivity: InfoActivity? = null
        var tradeActivity: TradeActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_activity_tablayout)

        val adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment = TestFragment.newInstance(position + 1)

            override fun getPageTitle(position: Int): CharSequence {
                when (position + 1) {
                    1 -> return "状況"
                    2 -> return "実行中"
                    3 -> return "タスク"
                    4 -> return "取引"
                }
                return "error"
            }

            override fun getCount(): Int = PAGENUM
        }
        pager.adapter = adapter
        pager.addOnPageChangeListener(this)
        tabs.setupWithViewPager(pager)

        val handler = Handler()
        Handler().post(object : Runnable {
            override fun run() {

                GetInfo().getBtcInfo()
                GetInfo().getMyInfo()

                handler.postDelayed(this, 1000)
            }
        })
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        when (position + 1) {
            1 -> {
                infoActivity?.refresh()
            }
            2 -> {
            }
            3 -> {
            }
            4 -> {
                tradeActivity?.refresh()
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    class TestFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            var view: View? = null
            when (arguments.getInt("page", 0)) {
                1 -> {
                    view = inflater!!.inflate(R.layout.activity_info, container, false)
                    infoActivity = InfoActivity(activity, view)
                    infoActivity?.refresh()
                }
                2 -> {
                    view = inflater!!.inflate(R.layout.activity_state, container, false)
                    StateActivity(activity, view)
                }
                3 -> {
                    view = inflater!!.inflate(R.layout.activity_task, container, false)
                    TaskActivity(activity, view)
                }
                4 -> {
                    view = inflater!!.inflate(R.layout.activity_trade, container, false)
                    tradeActivity = TradeActivity(activity, view)
                    tradeActivity?.refresh()
                }
            }
            return view
        }

        companion object {
            fun newInstance(page: Int): TestFragment {
                val args = Bundle()
                args.putInt("page", page)
                val fragment = TestFragment()
                fragment.arguments = args
                return fragment
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var result = false
        when (item.itemId) {
            R.id.setting -> {
                startActivity(Intent(applicationContext, ResetApiActivity::class.java))
                result = true
            }
            else -> {
            }
        }
        return result
    }
}