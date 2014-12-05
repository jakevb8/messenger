package com.jakevb8.instantmessenger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jvanburen on 12/4/2014.
 */
public class SettingsActivity extends Activity {
    private String _userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKey, MODE_PRIVATE);
        if(sharedPreferences.contains(Constants.UserIdKey)) {
            _userId = sharedPreferences.getString(Constants.UserIdKey, "");
        }
        ((TextView)findViewById(R.id.settings_user_id)).setText(_userId);

        EditText multicastIp = (EditText) findViewById(R.id.settings_multicast_ip_text);
        if (sharedPreferences.contains(Constants.MulticastIp)) {
            multicastIp.setText(sharedPreferences.getString(Constants.MulticastIp, ""));
        }
        multicastIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.MulticastIp, s.toString());
                editor.commit();
            }
        });
        EditText multicastPort = (EditText) findViewById(R.id.settings_multicast_port_text);
        if (sharedPreferences.contains(Constants.MulticastPort)) {
            multicastPort.setText(sharedPreferences.getInt(Constants.MulticastPort, 6100));
        }
        multicastPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Constants.MulticastPort, Integer.valueOf( s.toString()));
                editor.commit();
            }
        });
    }
}
