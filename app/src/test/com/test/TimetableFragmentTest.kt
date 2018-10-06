package com.test

import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.developer.alexandru.orarusv.BuildConfig
import com.developer.alexandru.orarusv.R
import com.developer.alexandru.orarusv.main.MainActivity
import com.developer.alexandru.orarusv.main.TimetableFragment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
import android.app.FragmentTransaction
import android.R.attr.fragment
import android.app.FragmentManager
import android.view.MenuInflater
import com.developer.alexandru.orarusv.main.MainActivityPresenterImpl.TIMETABLE_FRAGMENT_TAG
import org.junit.After
import org.robolectric.*
import org.robolectric.fakes.RoboMenuItem






@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class TimetableFragmentTest {

    private var fragment: TimetableFragment? = null
    private var mainActivity: MainActivity? = null

    @Before
    fun setUp() {
        fragment = TimetableFragment()
        mainActivity = org.robolectric.Robolectric.setupActivity(MainActivity::class.java)

        assertThat(mainActivity, `is`(notNullValue()))

        val fragmentManager = mainActivity!!.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(
                R.id.fragment_container, fragment!!, TIMETABLE_FRAGMENT_TAG)
        fragmentTransaction.commit()

    }

    @Test
    fun fragmentCreated() {
        assertThat(fragment!!.getView(), `is`(notNullValue()))
    }

    @Test
    fun menuItemExist() {
        val shadowActivity = org.robolectric.Shadows.shadowOf(mainActivity)
        assertThat(shadowActivity, `is`(notNullValue()))
        var menuItem = RoboMenuItem(R.id.menu_show_changelog)
        assertThat(menuItem, `is`(notNullValue()))
        menuItem = RoboMenuItem(R.id.choose_timetable_from_menu)
        assertThat(menuItem, `is`(notNullValue()))
        menuItem = RoboMenuItem(R.id.download_from_menu)
        assertThat(menuItem, `is`(notNullValue()))
        menuItem = RoboMenuItem(R.id.search_from_menu)
        assertThat(menuItem, `is`(notNullValue()))
    }

    @After
    fun tearDown() {

    }
}