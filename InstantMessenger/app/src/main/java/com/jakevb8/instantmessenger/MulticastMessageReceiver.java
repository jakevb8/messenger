package com.jakevb8.instantmessenger;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class MulticastMessageReceiver {
    public static interface OnMessageReceivedListener {
        public abstract void onMessageReceived(MulticastMessage message);
    }

    private MulticastServer _multicastServer;
    private Handler _handler;
    private OnMessageReceivedListener _listener;
    private Context _context;

    public  MulticastMessageReceiver(Context context) {
        _context = context;
    }

    private MulticastMessageReceiver() {
        _listener = null;
        _handler = new Handler() {
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MulticastServer.NEW_MESSAGE_RECEIVED:
                        if (_listener != null) {
                            _listener.onMessageReceived((MulticastMessage) message.getData().
                                    getSerializable(MulticastServer.EXTRA_MULTICAST_MESSAGE));
                        }
                        break;
                }
            }
        };
    }

    public void start(String deviceId, String ip, int port, OnMessageReceivedListener listener) {
        _listener = listener;
        _multicastServer = new MulticastServer(_context, _handler,"224.0.0.1", 6100, deviceId);
        _multicastServer.start();
    }

    public void stop() {
        if (_multicastServer != null) {
            _multicastServer.stop();
            _multicastServer = null;
        }
    }


}
