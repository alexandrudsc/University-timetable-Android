package com.developer.alexandru.orarusv.changelog

import android.app.Activity
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import com.developer.alexandru.orarusv.R


class ChangelogActivity : Activity() {
    private val version = "1_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_about)
        val tvVersion = findViewById<TextView>(R.id.about_description)
        val tvChangelog = findViewById<TextView>(R.id.about_git)
        findViewById<View>(R.id.scroll_gitlog).visibility = View.VISIBLE
        tvChangelog.movementMethod = ScrollingMovementMethod()

        val id = resources.getIdentifier("v$version", "raw", packageName)
        var changelog = resources.openRawResource(id).bufferedReader().use {
            it.readText()
        }
        val idxNewline = changelog.indexOf('\n')
        val textVersion = SpannableString(
                changelog.substring(0, idxNewline).replaceFirst('_', '.'))
        textVersion.setSpan(UnderlineSpan(), 0, textVersion.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tvVersion.text = textVersion

        changelog = changelog.removeRange(0, idxNewline)
        val lines = changelog.split("\n")

        val ss = StringBuilder()
        for (i in 0 until lines.size - 2)
            if (lines[i].isNotEmpty())
                ss.append("&#8226; ").append(lines[i]).append("<br/>")
        tvChangelog.text = TextUtils.concat(
                Html.fromHtml(ss.toString()), "\n\n\n", lines[lines.size - 2])
    }

}