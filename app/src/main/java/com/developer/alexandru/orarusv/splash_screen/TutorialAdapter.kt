package com.developer.alexandru.orarusv.splash_screen

import android.graphics.Color
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.developer.alexandru.orarusv.R

/**
 * Adapter for the view pager fragment adapter. Also selects a dot indicator at the bottom
 */
class TutorialAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TutorialFragment.newInstance(R.color.tutorial_first_background, position)
            }
            1 -> {
                TutorialFragment.newInstance(R.color.tutorial_second_background, position)
            }
            else -> {
                TutorialFragment.newInstance(R.color.tutorial_third_background, position)
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}