package com.test;

import android.support.v4.app.Fragment;
import android.text.Html;
import android.widget.TextView;

import com.developer.alexandru.orarusv.AboutFragment;
import com.developer.alexandru.orarusv.BuildConfig;
import com.developer.alexandru.orarusv.R;

import org.hamcrest.CoreMatchers;
import org.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import org.robolectric.shadows.support.v4.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AboutFragmentTest {

    private AboutFragment fragment;

    @Before
    public void setUp() {
        fragment = new AboutFragment();
        SupportFragmentTestUtil.startVisibleFragment(fragment);
    }

    @Test
    public void aboutFragmentCreated() {
        assertThat(fragment.getView(), is(notNullValue()));
    }

    @Test
    public void aboutFragmentTextDisplayed() {
        assertThat(fragment, is(notNullValue()));
        final TextView tv_description = (TextView)fragment.getView().
                findViewById(R.id.about_description);
        assertThat(tv_description, is(notNullValue()));
        assertThat(tv_description.getText().toString(),
                is(CoreMatchers.equalTo("Adaptat pentru orar.usv.ro")));
        final TextView tv_git_repo = (TextView) fragment.getView().
                findViewById(R.id.about_git);
        assertThat(tv_git_repo, is(notNullValue()));
        assertThat(tv_git_repo.getText().toString(),
                is(CoreMatchers.equalTo(
                        "Contribuțiile și ideilor tuturor sunt binevenite pe Github sau pe Google Play"
                )));
    }
}