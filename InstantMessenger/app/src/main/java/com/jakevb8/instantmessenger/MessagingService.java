package com.jakevb8.instantmessenger;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jake on 11/26/2014.
 */
public class MessagingService extends Service {

    private final IBinder myBinder = new LocalBinder();
    private String _ip = ""; //your computer IP address should be written here
    private int _port = 5000;
    private ServerSocket _serverSocket;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void IsBoundable() {
        Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        _ip = intent.getStringExtra(Constants.SERVICE_EXTRA_IP);
        _port = intent.getIntExtra(Constants.SERVICE_EXTRA_PORT, 5000);

        Runnable server = new ServerThread();
        new Thread(server).start();
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public MessagingService getService() {
            return MessagingService.this;

        }
    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                _serverSocket = new ServerSocket(_port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = _serverSocket.accept();
                    if (!socket.getInetAddress().getHostAddress().equalsIgnoreCase(_ip)) {
                        CommunicationThread commThread = new CommunicationThread(socket);
                        new Thread(commThread).start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
                try {
                    String messageText = input.readLine();
                    Message message = new Gson().fromJson(messageText, Message.class);
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_RECEIVE_MESSAGE);
                    intent.putExtra(Constants.RECEIVER_EXTRA_MESSAGE, message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }
}