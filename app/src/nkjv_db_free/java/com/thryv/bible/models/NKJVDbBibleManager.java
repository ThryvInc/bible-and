package com.thryv.bible.models;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.thryv.bible.db.DBHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ell on 10/12/16.
 */

public class NKJVDbBibleManager extends BibleManager {
    private DBHelper dbHelper;

    private static final String VERSES_TABLE = "verses";
    private static final String VERSE_NUMBER = "verse";
    private static final String VERSE_TEXT = "text";
    private static final String VERSE_CHAPTER = "chapter";

    private static final String BOOK_TABLE = "books";
    private static final String BOOK_NAME = "long_name";
    private static final String BOOK_NUMBER = "book_number";

    public NKJVDbBibleManager(Context context){
        dbHelper = new DBHelper(context);

        try {
            dbHelper.createDatabase();
            dbHelper.openDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch (SQLException sqle) {
            throw sqle;
        }
    }

    @Override
    public List<Book> getBooks(){
        ArrayList<Book> books = new ArrayList<>();

        String queryString = "SELECT " + BOOK_NUMBER + "," + BOOK_NAME + " FROM " + BOOK_TABLE + " ORDER BY " + BOOK_NUMBER;
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
        queryCursor.moveToFirst();

        while (!queryCursor.isAfterLast()) {
            Book book = new Book();
            book.setId(queryCursor.getInt(queryCursor.getColumnIndex(BOOK_NUMBER)));
            book.setName(queryCursor.getString(queryCursor.getColumnIndex(BOOK_NAME)));
            books.add(book);

            queryCursor.moveToNext();
        }
        queryCursor.close();
        return books;
    }

    @Override
    public int getNumberOfChapters(Book book){
        String queryString = "SELECT " + VERSE_CHAPTER + " FROM " + VERSES_TABLE
                + " WHERE " + BOOK_NUMBER + " = " + book.getId() + " ORDER BY " + VERSE_CHAPTER + " DESC LIMIT 1 ";
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
        queryCursor.moveToFirst();
        int numberOfChapters = queryCursor.getInt(queryCursor.getColumnIndex(VERSE_CHAPTER));
        queryCursor.close();
        return numberOfChapters;
    }

    @Override
    public List<Verse> getVerses(Book book, int chapter){
        ArrayList<Verse> verses = new ArrayList<>();

        String queryString = "SELECT " + VERSE_NUMBER + "," + VERSE_TEXT + " FROM " + VERSES_TABLE
                + " WHERE " + BOOK_NUMBER + " = " + book.getId() + " AND " + VERSE_CHAPTER + " = " + chapter;
        Cursor queryCursor = dbHelper.getSqLiteDatabase().rawQuery(queryString, null);
        queryCursor.moveToFirst();

        while (!queryCursor.isAfterLast()) {
            Verse verse = new Verse();
            verse.setBookId(book.getId());
            verse.setChapter(chapter);
            verse.setVerseNumber(queryCursor.getInt(queryCursor.getColumnIndex(VERSE_NUMBER)));
            verse.setText(queryCursor.getString(queryCursor.getColumnIndex(VERSE_TEXT)));
            verses.add(verse);

            queryCursor.moveToNext();
        }
        queryCursor.close();

        return verses;
    }
}
