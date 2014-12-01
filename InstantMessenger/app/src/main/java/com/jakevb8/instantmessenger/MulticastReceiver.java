package com.jakevb8.instantmessenger;

import android.os.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Created by jvanburen on 12/1/2014.
 */
public class MulticastReceiver {
    public static final int MSG_DATA_RECEIVED = 101;

    private int port;

    private boolean running = false;

    private MulticastSocket serverSocket;

    private InetAddress group;

    private String multicastAddress = "230.192.0.11";

    private Handler _handler;

    public MulticastReceiver(Handler handler, int port) {
        super();
        this.port = port;
        _handler = handler;
        init();
    }

    public MulticastReceiver(Handler handler) {
        this(handler, 5500);
    }

    private void init() {

        try {
            group = InetAddress.getByName(multicastAddress);
            serverSocket = new MulticastSocket(port);
            serverSocket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void start() throws IOException {
        System.out.println("server started");

        if (running)
            return;

        running = true;

        new Thread(new Runnable() {

            @Override
            public void run() {

                byte[] buf = new byte[1024];

                DatagramPacket packet = new DatagramPacket(buf, buf.length,
                        group, port);

                String msg = "msg";


                while (running) {

                    packet.setData(msg.getBytes(), 0, msg.length());


                    try {
                        serverSocket.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    public void stop() throws IOException {
        running = false;
    }
}
