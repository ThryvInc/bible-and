package com.thryv.bible;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.thryv.bible.models.BaseDbBibleManager;
import com.thryv.bible.models.BibleManager;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by ell on 12/22/16.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        BibleManager.setBibleManager(new BaseDbBibleManager(this));
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lora-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }
}
