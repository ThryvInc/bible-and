package com.thryv.bible.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LightingColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.thryv.bible.R
import com.thryv.bible.adapters.ThemedStringAdapter
import com.thryv.bible.adapters.VerseAdapter
import com.thryv.bible.helpers.ActionAnimationHelper
import com.thryv.bible.helpers.ItemLongPressHelper
import com.thryv.bible.models.*
import com.thryv.bible.views.VerseViewHolder
import kotlinx.android.synthetic.main.activity_reader.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*

class ReaderActivity : AppCompatActivity() {

    private var book: Book? = null
    private var chapter = 1
    private var bibleColorMapper: BibleColorMapper? = null
    private var bookList: List<Book>? = null

    private var chosenVerse: Verse? = null

    private var themeId: Int = R.style.AppTheme_NoActionBar

    protected lateinit var buttons: Array<View>

    protected lateinit var redHighlightButton: ImageButton
    protected lateinit var yellowHighlightButton: ImageButton
    protected lateinit var greenHighlightButton: ImageButton
    protected lateinit var blueHighlightButton: ImageButton
    protected lateinit var shareButton: ImageButton
    protected lateinit var exitButton: ImageButton

    private var xCoordinate: Float = 0.toFloat()
    private var yCoordinate: Float = 0.toFloat()

    protected var mCallback: ItemLongPressHelper.Callback = ItemLongPressHelper.Callback { viewHolder, xCoordinate, yCoordinate ->
        var yCoordinate = yCoordinate
        chosenVerse = (viewHolder as VerseViewHolder).verse

        yCoordinate += recyclerView.y
        val pixelsPerDip = resources.displayMetrics.density
        this@ReaderActivity.xCoordinate = xCoordinate
        this@ReaderActivity.yCoordinate = yCoordinate
        ActionAnimationHelper.animateButtons(true, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
    }

    private val colorMap: Map<Int, String>
        get() {
            val verseNumbersToColorCodes = HashMap<Int, String>()

            val bookChapter = book!!.abbreviation + chapter
            val set = getPreferences(Context.MODE_PRIVATE).getStringSet(bookChapter, HashSet())

            for (highlight in set!!) {
                val verseCode = highlight.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                verseNumbersToColorCodes[Integer.parseInt(verseCode[0])] = verseCode[1]
            }

            return verseNumbersToColorCodes
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkMode", false)) {
            themeId = R.style.DarkAppTheme
        }
        setTheme(themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        bibleColorMapper = BibleColorMapper()

        importOldHighlights()
        loadViews()
        setupRecyclerView()
        setupBookSpinner(toolbar)
        setupInitialBook()
        setupAds()
        setupNagging()
    }

    override fun onResume() {
        super.onResume()

        val isCurrentlyDarkMode = themeId == R.style.DarkAppTheme
        val darkMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkMode", false)
        if ((darkMode && !isCurrentlyDarkMode) || (!darkMode && isCurrentlyDarkMode)) {
            startActivity(Intent(this, ReaderActivity::class.java))
            finish()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            colorMenuItems(menu)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    fun colorMenuItems(menu: Menu) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            val drawable = item.icon
            if (drawable != null) {
                val wrapped = DrawableCompat.wrap(drawable)
                drawable.mutate()
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    DrawableCompat.setTint(wrapped, resources.getColor(R.color.colorAccent, theme))
                } else {
                    DrawableCompat.setTint(wrapped, resources.getColor(R.color.colorAccent))
                }
                item.icon = drawable
            }
        }
    }

    protected fun loadViews() {
        redHighlightButton = redHighlightView!!.findViewById<View>(R.id.btn_action) as ImageButton
        yellowHighlightButton = yellowHighlightView!!.findViewById<View>(R.id.btn_action) as ImageButton
        greenHighlightButton = greenHighlightView!!.findViewById<View>(R.id.btn_action) as ImageButton
        blueHighlightButton = blueHighlightView!!.findViewById<View>(R.id.btn_action) as ImageButton
        shareButton = shareView!!.findViewById<View>(R.id.btn_action) as ImageButton
        exitButton = exitView!!.findViewById<View>(R.id.btn_action) as ImageButton

        val buttonsArray = arrayOf<View>(redHighlightView, yellowHighlightView, greenHighlightView, blueHighlightView, shareView)
        buttons = buttonsArray

        for (view in buttons) {
            view.visibility = View.GONE
        }
        exitView!!.visibility = View.GONE

        val primaryHighlighter = ResourcesCompat.getDrawable(resources, R.drawable.highlighter, null)!!.constantState!!.newDrawable()
        colorFilterImage(R.color.colorAccent, primaryHighlighter)
        val redHighlighter = ResourcesCompat.getDrawable(resources, R.drawable.highlighter, null)!!.mutate().constantState!!.newDrawable()
        colorFilterImage(R.color.red, redHighlighter)
        val blueHighlighter = ResourcesCompat.getDrawable(resources, R.drawable.highlighter, null)!!.mutate().constantState!!.newDrawable()
        colorFilterImage(R.color.blue, blueHighlighter)
        val greenHighlighter = ResourcesCompat.getDrawable(resources, R.drawable.highlighter, null)!!.mutate().constantState!!.newDrawable()
        colorFilterImage(R.color.green, greenHighlighter)
        val yellowHighlighter = ResourcesCompat.getDrawable(resources, R.drawable.highlighter, null)!!.mutate().constantState!!.newDrawable()
        colorFilterImage(R.color.yellow, yellowHighlighter)
        redHighlightButton.setImageDrawable(redHighlighter)
        blueHighlightButton.setImageDrawable(blueHighlighter)
        yellowHighlightButton.setImageDrawable(yellowHighlighter)
        greenHighlightButton.setImageDrawable(greenHighlighter)

        val share = ResourcesCompat.getDrawable(resources, R.drawable.ic_share_black_24dp, null)
        colorFilterImage(R.color.colorAccent, share!!)
        val exit = ResourcesCompat.getDrawable(resources, R.drawable.x_mark, null)
        colorFilterImage(R.color.colorAccent, exit!!)
        shareButton.setImageDrawable(share)
        exitButton.setImageDrawable(exit)

        exitButton.setOnClickListener {
            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
            chosenVerse = null
        }

        redHighlightButton.setOnClickListener {
            highlightChosenVerse("a")
            chosenVerse = null

            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
        }
        blueHighlightButton.setOnClickListener {
            highlightChosenVerse("b")
            chosenVerse = null

            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
        }
        greenHighlightButton.setOnClickListener {
            highlightChosenVerse("c")
            chosenVerse = null

            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
        }
        yellowHighlightButton.setOnClickListener {
            highlightChosenVerse("d")
            chosenVerse = null

            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
        }
        shareButton.setOnClickListener {
            shareVerse(chosenVerse)
            val pixelsPerDip = resources.displayMetrics.density
            ActionAnimationHelper.animateButtons(false, xCoordinate, yCoordinate, pixelsPerDip, buttons, exitView)
            chosenVerse = null
        }
    }

    protected fun importOldHighlights() {
        val preferences = getPreferences(Context.MODE_PRIVATE)
        val chapCodes = preferences.getStringSet("highlights", HashSet())

        val editor = preferences.edit()

        for ((chapterCode) in preferences.all) {
            if (chapterCode != ReaderActivity.BOOK_KEY && chapterCode != ReaderActivity.CHAPTER_KEY) {
                if (TextUtils.isEmpty(getPreviousReference(chapCodes, chapterCode))) {
                    chapCodes!!.add(chapterCode)
                    editor.putStringSet("highlights", chapCodes)
                }
            }
        }
        editor.apply()
    }

    protected fun setupInitialBook() {
        val book = Book()
        var chapter = 1
        val chapCode: String? = intent.extras?.getString(CHAPTER_CODE)
        if (intent.extras != null && chapCode != null) {
            val bookAbbrev = chapCode.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]
            book.abbreviation = bookAbbrev
            chapter = Integer.valueOf(chapCode.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1])
        } else {
            val preferences = getPreferences(Activity.MODE_PRIVATE)
            val bookId = preferences.getInt(BOOK_KEY, 550)
            chapter = preferences.getInt(CHAPTER_KEY, 1)

            book.id = bookId
        }
        setBook(bookList!![bookList!!.indexOf(book)], chapter)
    }

    protected fun setupRecyclerView() {
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = manager
    }

    protected fun setupBookSpinner(toolbar: Toolbar?) {
        bookList = BibleManager.getBibleManager().books
        val bookNames = arrayOfNulls<String>(bookList!!.size)
        for (i in bookList!!.indices) {
            bookNames[i] = bookList!![i].name
        }

        spinner?.adapter = ThemedStringAdapter(
                toolbar!!.context,
                bookNames)

        spinner?.setSelection(bookList!!.indexOf(book))

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (bookList!![position] != book) {
                    setBook(bookList!![position], 1)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    protected fun setupAds() {
        val adView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build()
        adView.loadAd(adRequest)
    }

    protected fun setupNagging() {
        NagController.incrementNumberOfOpens(this)
        val pkgName = applicationContext.packageName
        var paidPkgUrl: String? = null
        if (pkgName.endsWith("_free")) {
            var paidPkgName = pkgName.substring(0, pkgName.length - 5)
            paidPkgName += "_paid"
            paidPkgUrl = "market://details?id=$paidPkgName"
        }
        NagController(this).startNag("market://details?id=$pkgName", paidPkgUrl)
    }

    protected fun highlightChosenVerse(colorCode: String) {
        val chapterCode = book!!.abbreviation + chapter
        val highlightCode = chosenVerse!!.verseNumber.toString() + ":" + colorCode

        val preferences = getPreferences(Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val set = preferences.getStringSet(chapterCode, HashSet())
        val previousReference = getPreviousReference(set, chosenVerse!!.verseNumber.toString() + ":")
        if (!TextUtils.isEmpty(previousReference) && previousReference == highlightCode) {
            set!!.remove(highlightCode)
        } else {
            if (previousReference != null) set!!.remove(previousReference)
            set!!.add(highlightCode)
        }

        //        Set<String> chapCodes = preferences.getStringSet("highlights", new HashSet<String>());
        //
        //        if (TextUtils.isEmpty(getPreviousReference(chapCodes, chapterCode))) {
        //            chapCodes.add(chapterCode);
        //            editor.putStringSet("highlights", chapCodes);
        //        }

        editor.putStringSet(chapterCode, set).apply()

        bibleColorMapper?.setVerseToCodeMap(colorMap)
        recyclerView?.adapter?.notifyDataSetChanged()
    }

    protected fun getPreviousReference(set: Set<String>, relevantCode: String): String? {
        for (code in set) {
            if (code.startsWith(relevantCode)) return code
        }
        return null
    }

    protected fun shareVerse(verse: Verse?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, getShareableText(verse!!))
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    private fun setBook(book: Book, chapter: Int) {
        if (book != this.book || chapter != this.chapter) {
            this.book = book
            this.chapter = chapter

            val nextOnClickListener = View.OnClickListener {
                if (BibleManager.getBibleManager().getNumberOfChapters(book) >= chapter + 1) {
                    setBook(book, chapter + 1)
                } else if (book.id != 730) {
                    setBook(bookList!![bookList!!.indexOf(book) + 1], 1)
                }
            }

            val previousOnClickListener = View.OnClickListener {
                if (0 < chapter - 1) {
                    setBook(book, chapter - 1)
                } else if (book.id != 10) {
                    setBook(bookList!![bookList!!.indexOf(book) - 1], 1)
                }
            }

            bibleColorMapper!!.setVerseToCodeMap(colorMap)
            val adapter = VerseAdapter(BibleManager.getBibleManager().getVerses(book, chapter),
                    previousOnClickListener, nextOnClickListener, mCallback, bibleColorMapper)
            recyclerView?.adapter = adapter

            spinner!!.setSelection(bookList!!.indexOf(book))
            setChapter()

            getPreferences(Context.MODE_PRIVATE).edit()
                    .putInt(BOOK_KEY, book.id)
                    .putInt(CHAPTER_KEY, chapter)
                    .apply()
        }
    }

    protected fun setChapter() {
        val numberOfChapters = BibleManager.getBibleManager().getNumberOfChapters(book)
        val chapterTitles = arrayOfNulls<String>(numberOfChapters)
        for (i in 1..numberOfChapters) {
            chapterTitles[i - 1] = i.toString()
        }

        chapterSpinner!!.adapter = ThemedStringAdapter(toolbar!!.context, chapterTitles)
        chapterSpinner!!.setSelection(chapter - 1)
        chapterSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                setBook(book!!, i + 1)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {

            }
        }
    }

    private fun colorFilterImage(colorId: Int, drawable: Drawable) {
        val filter = LightingColorFilter(Color.WHITE, ResourcesCompat.getColor(resources, colorId, null))
        drawable.colorFilter = filter
    }

    private fun getShareableText(verse: Verse): String {
        var shareableText = "\"" + verse.plainText
        shareableText += "\" — " + book!!.abbreviation
        shareableText += " " + verse.chapter + ":" + verse.verseNumber
        return shareableText
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_reader, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_highlights) {
            startActivity(Intent(this, HighlightsActivity::class.java))
        } else if (item.itemId == R.id.action_settings) {
            startActivity(Intent(this, PrefsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        protected val BOOK_KEY = "book_id"
        protected val CHAPTER_KEY = "chapter_number"
        val CHAPTER_CODE = "chapter_code"
    }

}
