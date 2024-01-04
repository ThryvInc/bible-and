package com.thryv.bible.models;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;

/**
 * Created by ell on 10/12/16.
 */

public class Verse {
    private int bookId;
    private int chapter;
    private int verseNumber;
    private String text;

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public void setVerseNumber(int verseNumber) {
        this.verseNumber = verseNumber;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Spanned getTextForReading(String hexColor){
        String verseText = text;
        verseText = verseText.replaceAll("\\[[0-9]+\\]", "");
        verseText = verseText.replaceAll("\n", "<br>");
        if (verseNumber != 0){
            verseText = "<i>" + verseNumber + "</i>" + "&nbsp;&nbsp;&nbsp;&nbsp;" +  verseText;
        }
        SpannableString spannable = new SpannableString(Html.fromHtml(verseText));
        if (!TextUtils.isEmpty(hexColor)){
            spannable.setSpan(new BackgroundColorSpan(Color.parseColor(hexColor)), 5, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    public String getPlainText(){
        String verseText = text;
        verseText = verseText.replaceAll("\\[[0-9]+\\]", "");
        return Html.fromHtml(verseText).toString();
    }
}
