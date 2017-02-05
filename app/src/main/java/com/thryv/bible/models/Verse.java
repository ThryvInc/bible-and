package com.thryv.bible.models;

import android.text.Html;
import android.text.Spanned;

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

    public Spanned getTextForReading(){
        String verseText = text;
        verseText = verseText.replaceAll("\\[[0-9]+\\]", "");
        verseText = verseText.replaceAll("\n", "<br>");
        if (verseNumber != 0){
            verseText = "<b>" + verseNumber + "</b>" + "&nbsp;&nbsp;&nbsp;&nbsp;" + verseText;
        }
        return Html.fromHtml(verseText);
    }

    public String getPlainText(){
        String verseText = text;
        verseText = verseText.replaceAll("\\[[0-9]+\\]", "");
        return Html.fromHtml(verseText).toString();
    }
}
