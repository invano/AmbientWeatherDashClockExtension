package com.invano.ambientweather;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class AmbientWeatherSettingsActivity extends PreferenceActivity {
    
	private static String appVersion = ""; 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setIcon(R.drawable.ic_ambientweather_extension);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        try {
			appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // TODO: if the previous activity on the stack isn't a ConfigurationActivity,
            // launch it.
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSimplePreferencesScreen() {
        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.prefs);
		findPreference("version").setSummary("Version " + appVersion);
		findPreference("developer").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
			    intent.setData(Uri.parse("https://plus.google.com/+EmanueleCozzi"));
			    startActivity(intent);
				return false;
				}
		});
		findPreference("report").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
			            "mailto","cozziemanuele@gmail.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[REPORT] AmbientTemperature v."+ appVersion);
				emailIntent.putExtra(Intent.EXTRA_TEXT, "Device info: \n" + 
						"device: " + android.os.Build.DEVICE + "\n" +
						"model: " + android.os.Build.MODEL + "\n" +
						"product: " + android.os.Build.PRODUCT + "\n" +
						"api level: " + android.os.Build.VERSION.SDK_INT + "\n" +
						"os version: " + System.getProperty("os.version") + "\n" +
						"\nMessage: \n");
				startActivity(emailIntent);
				return false;
			}
		});

        bindPreferenceSummaryToValue(findPreference("prefTempUnit"),"C");
        findPreference("prefHumidityOnTop").setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        bindPreferenceSummaryToValue(findPreference("prefTempDigit"),"0");
        bindPreferenceSummaryToValue(findPreference("prefHumidityDigit"),"0");
        bindPreferenceSummaryToValue(findPreference("prefPressionDigit"),"0");
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
//        bindPreferenceSummaryToValue(findPreference(TemperatureExtension.PREF_NAME));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return false;
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new
     * value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
            } 
            else if (preference instanceof CheckBoxPreference) {
                // Trigger the listener immediately with the preference's
                // current value.
                if (preference.getKey().equals("prefHumidityOnTop")) {
                	preference.setSummary(Boolean.valueOf(stringValue) ? R.string.pref_on_top_humidity_summary_yes : R.string.pref_on_top_humidity_summary_no);
                }
            }
            else if (preference instanceof EditTextPreference) {
            }
            else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
            	preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is
     * changed, its summary (line of text below the preference title) is updated to reflect the
     * value. The summary is also immediately updated upon calling this method. The exact display
     * format is dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference, String value) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), value));
    }
}