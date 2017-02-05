package com.thryv.bible;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.thryv.bible.models.BaseDbBibleManager;
import com.thryv.bible.models.BibleManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ell on 12/22/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        BibleManager.setBibleManager(new BaseDbBibleManager(this));
    }
}
