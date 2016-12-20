package com.thryv.bible;

import android.app.Application;

import com.thryv.bible.models.NKJVDbBibleManager;
import com.thryv.bible.models.BibleManager;

/**
 * Created by ell on 10/12/16.
 */

public class NKJVDbBibleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BibleManager.setBibleManager(new NKJVDbBibleManager(this));
    }

}
