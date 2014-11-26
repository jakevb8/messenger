package com.jakevb8.instantmessenger;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Jake on 11/26/2014.
 */
public class SocketUtils {

    public static void sendMessage(Message message) { new MessageSender(message).execute(); }

    private static class MessageSender extends AsyncTask<Void, Void, Void> {
        private Message _message;

        private MessageSender(Message message) {
            _message = message;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                //here you must put your computer's IP address.
                InetAddress serverAddr = InetAddress.getByName(_message.TargetIp);
                Socket socket = new Socket(serverAddr, _message.TargetPort);

                try {
                    //send the message to the server
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    if (out != null && !out.checkError()) {
                        out.println(new Gson().toJson(_message));
                        out.flush();
                    }
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
