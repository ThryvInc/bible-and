package com.thryv.bible.activities

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.thryv.bible.R

open class AdActivity: AppCompatActivity() {

    protected fun setupAds() {
        val adView = findViewById<View>(R.id.adView) as AdView
        val adRequest = AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build()
        adView.loadAd(adRequest)
    }
}