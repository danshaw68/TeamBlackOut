package com.blackout.paidupdater.Activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.blackout.paidupdater.R;

import java.util.ArrayList;
import java.util.Collections;

public class SettingsActivity extends PreferenceActivity {
        Toolbar bar;

        private SharedPreferences.OnSharedPreferenceChangeListener mListener;

        @SuppressWarnings("deprecation")
        @Override
        public void onCreate(Bundle savedInstanceState) {
                if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean(getString(R.string.ThemeStyle), false)) {
                        setTheme(R.style.AppThemeLight);
                }
                mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                                if (!key.equals(getString(R.string.ThemeStyle))) {
                                        return;
                                }
                                finish();
                                final Intent intent = IntentCompat.makeMainActivity(new ComponentName(
                                        SettingsActivity.this, MainActivity.class));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                        }
                };

                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);

                PreferenceGroup devGroup = (PreferenceGroup) findPreference("dev");
                ArrayList<Preference> dev = new ArrayList<Preference>();
                for (int i = 0; i < devGroup.getPreferenceCount(); i++) {
                        dev.add(devGroup.getPreference(i));
                }
                devGroup.removeAll();
                devGroup.setOrderingAsAdded(false);
                Collections.shuffle(dev);
                for(int i = 0; i < dev.size(); i++) {
                        Preference p = dev.get(i);
                        p.setOrder(i);

                        devGroup.addPreference(p);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
                        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings, root, false);
                        root.addView(bar, 0);
                } else {
                        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
                        ListView content = (ListView) root.getChildAt(0);

                        root.removeAllViews();

                        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings, root, false);


                        int height;
                        TypedValue tv = new TypedValue();
                        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                        }else{
                                height = bar.getHeight();
                        }

                        content.setPadding(0, height, 0, 0);

                        root.addView(content);
                        root.addView(bar);
                }
                bar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                        case android.R.id.home:
                                onBackPressed();
                                return true;
                }

                return super.onOptionsItemSelected(item);
        }

        @Override
        public void onResume() {
                super.onResume();
                getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
        }

        @Override
        public void onPause() {
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
                super.onPause();
        }
}

