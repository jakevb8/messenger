package com.jakevb8.instantmessenger;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jake on 11/26/2014.
 */
public class DirectConnectActivity extends Activity {
    MessagingService _messagingService;
    private boolean _isBound;
    private String _userId;
    private String _userName;
    private String _currentIp;
    private EditText _ip;
    private EditText _port;
    private EditText _message;
    private Button _btnSend;
    private Button _btnConnect;
    private ChatFragment _chatFragment;

    private ServiceConnection _connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            _messagingService = ((MessagingService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            _messagingService = null;
        }
    };

    private void doBindService() {
        bindService(new Intent(DirectConnectActivity.this, MessagingService.class), _connection, Context.BIND_AUTO_CREATE);
        _isBound = true;
        if (_messagingService != null) {
            _messagingService.IsBoundable();
        }
    }


    private void doUnbindService() {
        if (_isBound) {
            // Detach our existing connection.
            unbindService(_connection);
            _isBound = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_connect);
        try {
            final SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferencesKey, MODE_PRIVATE);

            if(sharedPreferences.contains(Constants.UserIdKey)) {
                _userId = sharedPreferences.getString(Constants.UserIdKey, "");
            }
            if(sharedPreferences.contains(Constants.UserNameKey)) {
                _userName = sharedPreferences.getString(Constants.UserNameKey, "");
            }

            _currentIp = NetworkUtils.getLocalIpAddress();

            _ip = (EditText) findViewById(R.id.direct_connect_ip_text);

            if (sharedPreferences.contains(Constants.DirectConnectTargetIp)) {
                _ip.setText(sharedPreferences.getString(Constants.DirectConnectTargetIp, ""));
            } else {
                _ip.setText(_currentIp);
            }

            _ip.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.DirectConnectTargetIp, s.toString());
                    editor.commit();
                }
            });

            _port = (EditText) findViewById(R.id.direct_connect_port_text);
            _message = (EditText) findViewById(R.id.direct_connect_messge_text);
            _btnSend = (Button) findViewById(R.id.direct_connect_send_button);

            ((TextView) findViewById(R.id.direct_connect_my_ip_text)).setText(_currentIp);

            _btnConnect = (Button) findViewById(R.id.direct_connect_button);
            _btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doUnbindService();
                    String port = _port.getText().toString();
                    Intent intent = new Intent(DirectConnectActivity.this, MessagingService.class);
                    intent.putExtra(Constants.SERVICE_EXTRA_IP, _currentIp);
                    intent.putExtra(Constants.SERVICE_EXTRA_PORT, Integer.valueOf(port));
                    startService(intent);
                    doBindService();
                }
            });

            _btnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Message message = new Message();
                        message.UserId = _userId;
                        message.UserName = _userName;
                        message.Message = _message.getText().toString();
                        message.TargetIp = _ip.getText().toString();
                        message.TargetPort = Integer.valueOf(_port.getText().toString());
                        SocketUtils.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            _chatFragment = ChatFragment.newInstance(true);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.direct_connect_chat_fragment, _chatFragment);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_RECEIVE_MESSAGE);
        LocalBroadcastManager.getInstance(this).registerReceiver(_messageReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(_messageReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private BroadcastReceiver _messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                //Message message = (Message) intent.getSerializableExtra(Constants.RECEIVER_EXTRA_MESSAGE);
                MessageDatabase messageDatabase = new MessageDatabase(context);
                List<Message> messages = messageDatabase.getNewMessages();
                messageDatabase.close();
                for(Message message : messages) {
                    _chatFragment.addMessage(message.UserId + ": " + message.UserName + ": " + message.Message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
