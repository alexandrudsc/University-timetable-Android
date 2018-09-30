package com.test;

import android.support.v4.app.Fragment;

import com.developer.alexandru.orarusv.AboutFragment;
import com.developer.alexandru.orarusv.BuildConfig;

import org.hamcrest.CoreMatchers;
import org.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AboutFragmentTest {

    private AboutFragment aboutFragment;

    @Before
    public void setUp() {
        aboutFragment = new AboutFragment();
    }

    @Test
    public void aboutFragmentCreated() {
        startFragment(aboutFragment);

        assertThat(aboutFragment, is(notNullValue()));
    }
}