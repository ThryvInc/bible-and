package com.thryv.bible.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.thryv.bible.R;
import com.thryv.bible.adapters.BookAdapter;
import com.thryv.bible.adapters.VerseAdapter;
import com.thryv.bible.models.BibleManager;
import com.thryv.bible.models.Book;
import com.thryv.bible.models.NagController;
import com.thryv.bible.models.Verse;
import com.thryv.bible.views.VerseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ReaderActivity extends AppCompatActivity {
    private static final String BOOK_KEY = "book_id";
    private static final String CHAPTER_KEY = "chapter_number";

    private RecyclerView recyclerView;
    private Book book;
    private int chapter;
    private List<Book> bookList;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupRecyclerView();
        setupBookSpinner(toolbar);
        setupInitialBook();
        setupAds();

        NagController.incrementNumberOfOpens(this);
        String pkgName = getApplicationContext().getPackageName();
        new NagController(this).startNag("market://details?id=" + pkgName, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void setupInitialBook(){
        SharedPreferences preferences = getPreferences(Activity.MODE_PRIVATE);
        int bookId = preferences.getInt(BOOK_KEY, 550);
        int chapter = preferences.getInt(CHAPTER_KEY, 1);

        Book book = new Book();
        book.setId(bookId);
        setBook(bookList.get(bookList.indexOf(book)), chapter);
    }

    protected void setupRecyclerView(){
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView = (RecyclerView)findViewById(R.id.rv_verses);
        recyclerView.setLayoutManager(manager);
    }

    protected void setupBookSpinner(Toolbar toolbar){
        bookList = BibleManager.getBibleManager().getBooks();
        String[] bookNames = new String[bookList.size()];
        for (int i = 0; i<bookList.size(); i++){
            bookNames[i] = bookList.get(i).getName();
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(new BookAdapter(
                toolbar.getContext(),
                bookNames));

        spinner.setSelection(bookList.indexOf(book));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!bookList.get(position).equals(book)) {
                    setBook(bookList.get(position), 1);
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    protected void setupAds(){
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    private void setBook(final Book book, final int chapter){
        if (!book.equals(this.book) || chapter != this.chapter){
            this.book = book;
            this.chapter = chapter;

            View.OnClickListener nextOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (BibleManager.getBibleManager().getNumberOfChapters(book) >= chapter + 1){
                        setBook(book, chapter + 1);
                    }else if (book.getId() != 730){
                        setBook(bookList.get(bookList.indexOf(book) + 1), 1);
                    }
                }
            };

            View.OnClickListener previousOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (0 < chapter - 1){
                        setBook(book, chapter - 1);
                    }else if (book.getId() != 10){
                        setBook(bookList.get(bookList.indexOf(book) - 1), 1);
                    }
                }
            };

            VerseViewHolder.OnVerseLongClickListener verseLongClickListener = new VerseViewHolder.OnVerseLongClickListener() {
                @Override
                public void onVerseLongClicked(Verse verse) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getShareableText(verse));
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            };

            VerseAdapter adapter = new VerseAdapter(BibleManager.getBibleManager().getVerses(book, chapter),
                    previousOnClickListener, nextOnClickListener, verseLongClickListener);
            recyclerView.setAdapter(adapter);

            invalidateOptionsMenu();
            spinner.setSelection(bookList.indexOf(book));

            getPreferences(MODE_PRIVATE).edit()
                    .putInt(BOOK_KEY, book.getId())
                    .putInt(CHAPTER_KEY, chapter)
                    .apply();
        }
    }

    private String getShareableText(Verse verse){
        String shareableText = "\"" +  verse.getPlainText();
        shareableText += "\" — " + book.getAbbreviation();
        shareableText += " " + verse.getChapter() + ":" + verse.getVerseNumber();
        return shareableText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reader, menu);
        menu.findItem(R.id.action_chapter).setTitle("Chapter " + chapter);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int numberOfChapters = BibleManager.getBibleManager().getNumberOfChapters(book);
        List<String> chapterTitles = new ArrayList<>(numberOfChapters);
        for (int i = 1; i<=numberOfChapters; i++){
            chapterTitles.add("Chapter " + i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose chapter");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                ReaderActivity.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.addAll(chapterTitles);

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setSingleChoiceItems(arrayAdapter, chapter - 1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setBook(book, which + 1);
                        item.setTitle("Chapter " + chapter);
                        dialog.dismiss();
                    }
                });
        builder.show();

        return super.onOptionsItemSelected(item);
    }

}
