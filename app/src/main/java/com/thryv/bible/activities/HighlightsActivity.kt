package com.thryv.bible.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.thryv.bible.R
import kotlinx.android.synthetic.main.activity_highlights.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*
import java.util.regex.Pattern

/**
 * Created by ell on 5/14/17.
 */

class HighlightsActivity : AppCompatActivity() {
    protected var verseAddresses: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        val themeId = if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkMode", false)) R.style.DarkAppTheme else R.style.AppTheme_NoActionBar
        setTheme(themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlights)

        setupVerseAddresses()
        setupRecyclerView()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    protected fun setupVerseAddresses() {
        val preferences = getSharedPreferences("com.thryv.bible.activities.ReaderActivity", Context.MODE_PRIVATE)
        val chapCodes = preferences.all

        for ((chapCode) in chapCodes) {
            if (chapCode.matches("[A-Z][a-z]+[0-9]+".toRegex())) {
                val set = preferences.getStringSet(chapCode, HashSet())
                for (verseCode in set!!) {
                    val verseNumber = verseCode.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                    val matcher = Pattern.compile("[0-9]+").matcher(chapCode)
                    if (matcher.find()) {
                        val bookAbbreviation = chapCode.substring(0, matcher.start())
                        val chapter = chapCode.substring(matcher.start())
                        verseAddresses.add("$bookAbbreviation $chapter:$verseNumber")
                    }
                }
            }
        }
    }

    protected fun setupRecyclerView() {
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1)
        if (verseAddresses.size > 0) {
            arrayAdapter.addAll(verseAddresses)
        } else {
            arrayAdapter.add("You haven't highlighted any verses yet! Press and hold on a verse to see highlighting options.")
        }

        listView?.adapter = arrayAdapter
        listView?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val verseAddress = verseAddresses[i]
            val intent = Intent(this@HighlightsActivity, ReaderActivity::class.java)
            intent.putExtra(ReaderActivity.CHAPTER_CODE, verseAddress.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])
            startActivity(intent)
            finish()
        }
    }
}
