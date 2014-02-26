package com.blackout.paidupdater;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    //EditTextPreference activityName_text;//
    //EditTextPreference msgName_text;//

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference p = findPreference(key);
        if(p instanceof EditTextPreference)
        {
            p.setSummary(sharedPreferences.getString(key, (String)p.getSummary()));
        }
        else if(p instanceof ListPreference)
        {
            p.setSummary((String)((ListPreference) p).getEntry());
        }
    }
}
