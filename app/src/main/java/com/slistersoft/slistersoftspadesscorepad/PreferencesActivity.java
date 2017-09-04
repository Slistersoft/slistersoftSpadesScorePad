package com.slistersoft.slistersoftspadesscorepad;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Scott Lister on 8/16/2014.
 */
public class PreferencesActivity extends PreferenceActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs);


    }
}
