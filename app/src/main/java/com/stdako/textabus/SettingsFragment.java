package com.stdako.textabus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import java.util.regex.Pattern;


public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    String smsNumber;

    public static final String keySMSNumber = "textabus.SMS_NUMBER_KEY";
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE
        );
        editor = sharedPref.edit();

        // EditTextPreference smsETP = (EditTextPreference) findPreference(keySMSNumber);
        // smsETP.setText(sharedPref.getString(keySMSNumber, "57555"));

        // get the current number (should be valid...)
        smsNumber = sharedPref.getString(keySMSNumber, "57555");

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // this is the shared preference listener - listens for changes.
        if (key.equals(keySMSNumber)) {
            String newSMSNumber = sharedPreferences.getString(key, "57555");

            if (checkValidPhoneNumber(newSMSNumber)) {
                // phone number is good to go; put it in memory
                smsNumber = newSMSNumber;

                // and put it in storage
                editor.putString(key, newSMSNumber);
                editor.apply();
            } else {
                // phone number is bad; replace the ETP's text with the old number
                EditTextPreference etp = (EditTextPreference) findPreference(key);
                etp.setText(smsNumber);

                // alert user with TOAST
                Toast.makeText(
                        etp.getContext(),
                        "Invalid phone number. Old phone number (" + smsNumber + ") will be used instead.",
                        Toast.LENGTH_LONG
                     ).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    public boolean checkValidPhoneNumber(String num) {
        // determine if a string 'num' is a valid phone number
        // regex: any characters that are not numbers.
        String pattern = "[^0-9]";
        Pattern r = Pattern.compile(pattern);

        if (r.matcher(num).find())
            return false;

        return true;
    }
}
