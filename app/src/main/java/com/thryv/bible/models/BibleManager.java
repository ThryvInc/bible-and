package com.thryv.bible.models;

import java.util.List;

/**
 * Created by ell on 10/12/16.
 */

public abstract class BibleManager {
    private static BibleManager bibleManager;

    public static BibleManager getBibleManager() {
        return bibleManager;
    }

    public static void setBibleManager(BibleManager manager) {
        bibleManager = manager;
    }

    public abstract List<Book> getBooks();
    public abstract int getNumberOfChapters(Book book);
    public abstract List<Verse> getVerses(Book book, int chapter);
}
