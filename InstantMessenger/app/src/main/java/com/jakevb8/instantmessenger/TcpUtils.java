package com.jakevb8.instantmessenger;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TcpUtils {

    public static final int MESSAGE_SEND_FINISHED = 100;
    public static final int MESSAGE_SEND_STARTED = 101;
    private static final String TAG = "TcpUtils";

    public static String getClientData(InputStream inputstream) {
        String s;
        try {
            s = (new DataInputStream(inputstream)).readUTF();
            inputstream.close();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return s;
    }

    public static void sendObject(final Handler handler, String s, final Object obj, final String serverIp) {
        (new Thread(new Runnable() {

            public void run() {
                try {
                    String s1 = (new Gson()).toJson(obj);
                    handler.sendMessage(Message.obtain(handler, MESSAGE_SEND_STARTED, obj));
                    Socket socket = new Socket(InetAddress.getByName(serverIp), 6101);
                    socket.setSoTimeout(10000);
                    DataOutputStream dataoutputstream = new DataOutputStream(socket.getOutputStream());
                    Log.i("TcpUtils", (new StringBuilder()).append("sendObject json: ").append(s1).toString());
                    dataoutputstream.writeUTF(s1);
                    dataoutputstream.flush();
                    dataoutputstream.close();
                    socket.close();
                    handler.sendMessage(Message.obtain(handler, MESSAGE_SEND_FINISHED, obj));
                    return;
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        })).start();
    }
}
