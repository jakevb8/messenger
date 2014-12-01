package com.jakevb8.instantmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {
    private String _userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKey, MODE_PRIVATE);
        if(sharedPreferences.contains(Constants.UserIdKey)) {
            _userId = sharedPreferences.getString(Constants.UserIdKey, "");
        }
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            _userId = UUID.randomUUID().toString();
            editor.putString(Constants.UserIdKey, _userId);
            editor.commit();
        }
        ((TextView)findViewById(R.id.main_user_id)).setText(_userId);
        final EditText txtUserName = (EditText) findViewById(R.id.main_user_name);
        if(sharedPreferences.contains(Constants.UserNameKey)) {
            txtUserName.setText(sharedPreferences.getString(Constants.UserNameKey, ""));
        }
        findViewById(R.id.main_direct_connect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DirectConnectActivity.class);
                intent.putExtra(Constants.UserIdKey, _userId);
                intent.putExtra(Constants.UserNameKey, txtUserName.getText());
                startActivity(intent);
            }
        });

        txtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.UserNameKey, s.toString());
                editor.commit();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
