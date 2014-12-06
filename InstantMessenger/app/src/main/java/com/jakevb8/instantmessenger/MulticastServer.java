package com.jakevb8.instantmessenger;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer {

    public static final int NEW_MESSAGE_RECEIVED = 102;
    public static final String EXTRA_MULTICAST_MESSAGE = "multicast_message";
    private String _ip;
    private int _port;
    private final String _deviceId;
    private Context _ontext;
    private final Handler _handler;
    private Thread _listener;
    private MulticastSocket _socket;

    public MulticastServer(Context context, Handler handler, String ip, int port, String deviceId) {
        _ontext = context;
        _ip = ip;
        _port = port;
        _deviceId = deviceId;
        _handler = handler;
        _listener = new Thread(_discoveryRunnable);
    }

    private Runnable _discoveryRunnable = new Runnable() {
        public void run() {
            android.net.wifi.WifiManager.MulticastLock multicastlock;
            multicastlock = ((WifiManager) _ontext.getSystemService(Context.WIFI_SERVICE)).createMulticastLock("96913f0a-35c3-4cd1-9f3e-dc42ed4c2b3e");
            multicastlock.acquire();

            try {
                _socket = UdpUtils.createMulticastSocket(_ontext, _port);
                _socket.setBroadcast(true);
                _socket.joinGroup(InetAddress.getByName(_ip));
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            while (_listener.isAlive()) {

                byte data[] = new byte[1500];
                DatagramPacket datagrampacket = new DatagramPacket(data, data.length);
                try {
                    _socket.receive(datagrampacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    MulticastMessage multicastMessage = UdpUtils.getBroadcastMessage(datagrampacket);
                    if (multicastMessage != null && !multicastMessage.deviceId.equalsIgnoreCase(_deviceId)) {
                        Bundle bundleData = new Bundle();
                        bundleData.putSerializable(EXTRA_MULTICAST_MESSAGE, multicastMessage);
                        Message message = Message.obtain(_handler, NEW_MESSAGE_RECEIVED);
                        message.setData(bundleData);
                        _handler.sendMessage(message);
                    }
                } catch (Exception e) {
                   Logger.e(_ontext, "MulticastServer", e);
                }
            }
            multicastlock.release();
        }
    };

    public void start() {
        _listener.start();
    }

    public void stop() {
        if (_socket != null) {
            try {
                _socket.leaveGroup(InetAddress.getByName("224.0.0.1"));
                _socket.close();
                _socket = null;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        _listener.interrupt();
    }

}
