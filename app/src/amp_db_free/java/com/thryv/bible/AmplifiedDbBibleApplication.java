package com.thryv.bible;

import android.app.Application;

import com.thryv.bible.models.AmplifiedDbBibleManager;
import com.thryv.bible.models.BibleManager;

/**
 * Created by ell on 10/12/16.
 */

public class AmplifiedDbBibleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BibleManager.setBibleManager(new AmplifiedDbBibleManager(this));
    }

}
