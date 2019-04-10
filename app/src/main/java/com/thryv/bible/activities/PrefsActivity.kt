package com.thryv.bible.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.thryv.bible.R
import com.thryv.bible.models.PrefsWrapper

class PrefsActivity: PreferenceActivity() {
    companion object {
        val hasAdsKey = "appHasAds"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val themeId = if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkMode", false)) R.style.DarkAppTheme else R.style.AppTheme_NoActionBar
        setTheme(themeId)
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction().replace(android.R.id.content, PrefsFragment()).commit()
    }

    class PrefsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val defaultHasAds = (getString(R.string.has_ads) == "yes")
            val hasAds = preferenceManager.sharedPreferences.getBoolean(hasAdsKey, defaultHasAds)
            preferenceManager.sharedPreferences.edit().putBoolean(hasAdsKey, hasAds).apply()

            addPreferencesFromResource(R.xml.prefs)

            if (hasAds) {
                findPreference(hasAdsKey)?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val urlPart = "market://details?id="
                    val pkgName = activity.applicationContext.packageName
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$urlPart$pkgName.paid")))
                    return@OnPreferenceClickListener false
                }
            }

            findPreference("darkMode")?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                startActivity(Intent(activity, PrefsActivity::class.java))
                activity?.finish()
                return@OnPreferenceChangeListener true
            }

            findPreference("contact")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:ephherd@gmail.com"))
                intent.action = Intent.ACTION_SENDTO
                intent.type = "text/html"
                intent.data = Uri.parse("mailto:ephherd@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, "ephherd@gmail.com")
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                startActivity(Intent.createChooser(intent, "Email"))
                return@OnPreferenceClickListener false
            }
            findPreference("translations")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val urlPart = "https://play.google.com/store/apps/developer?id=Thryv,+Inc&hl=en_US"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlPart)))
                return@OnPreferenceClickListener false
            }
            findPreference("privacy")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val urlPart = "https://elliotschrock.com/app-privacy-policy/"
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(urlPart)))
                return@OnPreferenceClickListener false
            }
        }
    }
}
