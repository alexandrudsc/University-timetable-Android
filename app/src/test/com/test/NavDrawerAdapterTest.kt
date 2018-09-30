package com.test

import com.developer.alexandru.orarusv.BuildConfig
import com.developer.alexandru.orarusv.R
import com.developer.alexandru.orarusv.navigation_drawer.NavDrawerAdapter

import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.RuntimeEnvironment

import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class NavDrawerAdapterTest {

    private var adapter: NavDrawerAdapter? = null
    private var titles: Any? = null

    @Before
    fun setUp() {
        val ctx = RuntimeEnvironment.application.applicationContext
        assertThat(ctx, `is`(notNullValue()))
        titles = ctx.resources.getStringArray(R.array.drawer_list_elements)
        adapter = NavDrawerAdapter(ctx)
        assertThat(adapter, `is`(notNullValue()))
    }

    @Test
    fun allElementsAdded() {
        print ("this is tested")
        for (i in 0 until adapter!!.count) {
            val item = adapter!!.getItem(i)
            assertThat(item, `is`(notNullValue()))
        }
    }
}