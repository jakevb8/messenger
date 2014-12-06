package com.jakevb8.instantmessenger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jvanburen on 12/4/2014.
 */
public class MulticastActivity extends Activity {
    private MulticastMessageReceiver _receiver;
    private ArrayAdapter<String> _adapter;
    private ListView _messageList;
    private String _userId;
    private String _userName;
    private String _multicastIp;
    private int _multicastPort;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multicast);

        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKey, MODE_PRIVATE);

        _userId = sharedPreferences.getString(Constants.UserIdKey, "");
        _userName = sharedPreferences.getString(Constants.UserNameKey, "");
        _multicastIp = sharedPreferences.getString(Constants.MulticastIp, "224.0.0.1");
        _multicastPort = sharedPreferences.getInt(Constants.MulticastPort, 6100);

        final EditText message = (EditText) findViewById(R.id.multicast_messge_text);
        Button sendButton = (Button) findViewById(R.id._multicast_send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MulticastMessage multicastMessage = new MulticastMessage();
                    multicastMessage.deviceId = _userId;
                    multicastMessage.userName = _userName;
                    multicastMessage.message = message.getText().toString();
                    UdpUtils.sendBroadcastMessage(multicastMessage, _multicastIp, _multicastPort);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        _messageList = (ListView) findViewById(R.id.multicast_message_list);

        _adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>());
        _messageList.setAdapter(_adapter);
    }

    @Override
    protected void onResume() {
        final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKey, MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.MulticastIp)) {
            _multicastIp = sharedPreferences.getString(Constants.MulticastIp, "224.0.0.1");
        }
        if (sharedPreferences.contains(Constants.MulticastPort)) {
            _multicastPort = sharedPreferences.getInt(Constants.MulticastPort, 6100);
        }

        _receiver = new MulticastMessageReceiver(this);
        _receiver.start(_userId, _multicastIp, _multicastPort, new MulticastMessageReceiver.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MulticastMessage message) {
                _adapter.add(message.userName + ": " + message.message);
            }
        });
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (_receiver != null) {
            _receiver.stop();
            _receiver = null;
        }
        super.onPause();
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
            Intent intent = new Intent(MulticastActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
