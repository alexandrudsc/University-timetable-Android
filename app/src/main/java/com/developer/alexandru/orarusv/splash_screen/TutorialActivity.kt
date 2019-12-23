package com.developer.alexandru.orarusv.splash_screen

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.developer.alexandru.orarusv.R

/**
 * View pager presenting animated pages with a short tutorial
 */
class TutorialActivity : FragmentActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {
    private var mViewPager: ViewPager? = null
    private var dotsIndicator: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)
        mViewPager = findViewById<View>(R.id.viewpager) as ViewPager
        mViewPager!!.addOnPageChangeListener(this)
        // Set an Adapter on the ViewPager
        dotsIndicator = findViewById(R.id.tutorial_vp_indicator)
        mViewPager!!.adapter = TutorialAdapter(supportFragmentManager)
        // Set a PageTransformer
        mViewPager!!.setPageTransformer(false, TutorialPagerTransformer())
        // set buttons click listeners exit or next
        findViewById<Button>(R.id.btn_skip_tutorial).setOnClickListener(this)
        findViewById<Button>(R.id.btn_next_tutorial).setOnClickListener(this)

    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        setIndicator(position)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btn_skip_tutorial -> this.finish()
            R.id.btn_next_tutorial -> {
                if ((v as Button).text == "Gata") {
                    this.finish()
                    return
                }
                mViewPager!!.setCurrentItem(mViewPager!!.currentItem + 1, true)
            }
        }
    }

    private fun setIndicator(position: Int) {
        val firstDot = dotsIndicator!!.findViewById<ImageView>(R.id.first_dot)
        val secondDot = dotsIndicator!!.findViewById<ImageView>(R.id.second_dot)
        val thirdDot = dotsIndicator!!.findViewById<ImageView>(R.id.third_dot)
        when (position) {
            0 -> {
                firstDot.setImageResource(R.drawable.dot_lightgrey)
                secondDot.setImageResource(R.drawable.dot_darkgrey)
                thirdDot.setImageResource(R.drawable.dot_darkgrey)
            }
            1 -> {
                firstDot.setImageResource(R.drawable.dot_darkgrey)
                secondDot.setImageResource(R.drawable.dot_lightgrey)
                thirdDot.setImageResource(R.drawable.dot_darkgrey)
            }
            2 -> {
                firstDot.setImageResource(R.drawable.dot_darkgrey)
                secondDot.setImageResource(R.drawable.dot_darkgrey)
                thirdDot.setImageResource(R.drawable.dot_lightgrey)
                this.findViewById<View>(R.id.btn_skip_tutorial).visibility = View.GONE
                this.findViewById<Button>(R.id.btn_next_tutorial).text = "Gata"
            }
        }
    }

}