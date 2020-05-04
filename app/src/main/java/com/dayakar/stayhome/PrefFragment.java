package com.dayakar.stayhome;

import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.dayakar.stayhome.R;

public class PrefFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_about);
        Preference mp=findPreference("developer");
        mp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

         mp=findPreference("appVersion");
        String current= BuildConfig.VERSION_NAME;

        mp.setSummary("current version "+current);


    }
}

