package com.jakevb8.instantmessenger;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UdpUtils {

    public UdpUtils() {
    }

    public static MulticastSocket createMulticastSocket(Context context, int port)
            throws UnknownHostException, SocketException, IOException {
        MulticastSocket multicastsocket = new MulticastSocket(port);
        if (IpUtils.connectedToEthernet(context)) {
            return null;
        }
        NetworkInterface networkinterface = NetworkInterface.getByName("eth0");
        if (networkinterface != null) {
            multicastsocket.setNetworkInterface(networkinterface);
        }
        _L4:
        multicastsocket.setReuseAddress(true);
        multicastsocket.setTimeToLive(255);
        return multicastsocket;
    }

    public static void sendBroadcastMessage(final MulticastMessage message, final String ip, final int port) {
        (new Thread(new Runnable() {
            public void run() {
                try {
                    String json = new Gson().toJson(message);
                    DatagramSocket datagramsocket = new DatagramSocket();
                    byte data[] = json.getBytes();
                    datagramsocket.send(new DatagramPacket(data, data.length, InetAddress.getByName(ip), port));
                    datagramsocket.close();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        })).start();
    }

    public static void sendMessage(final String message, final String ipAddress) {
        (new Thread(new Runnable() {

            public void run() {
                try {
                    DatagramSocket datagramsocket = new DatagramSocket();
                    byte abyte0[] = (new String((new StringBuilder()).append(NetworkUtils.getLocalIpAddress()).append(": ").append(message).toString())).getBytes();
                    datagramsocket.send(new DatagramPacket(abyte0, abyte0.length, InetAddress.getByName(ipAddress), 6101));
                    datagramsocket.close();
                    return;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        })).start();
    }
}
