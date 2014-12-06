package com.jakevb8.instantmessenger;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jvanburen on 12/5/2014.
 */
public class Logger {
    public static void e(Context context, String logMessageTag, Throwable throwableException) {
        if (!Log.isLoggable(logMessageTag, Log.ERROR))
            return;

        int logResult = Log.e(logMessageTag, "", throwableException);
        if (logResult > 0)
            logToFile(context, logMessageTag,  Log.getStackTraceString(throwableException));
    }

    public static void v(Context context, String logMessageTag, String logMessage) {
        if (!BuildConfig.DEBUG || !Log.isLoggable(logMessageTag, Log.VERBOSE))
            return;

        int logResult = Log.v(logMessageTag, logMessage);
        if (logResult > 0)
            logToFile(context, logMessageTag, logMessage);
    }

    public static void v(Context context, String logMessageTag, String logMessage, Throwable throwableException) {
        if (!BuildConfig.DEBUG || !Log.isLoggable(logMessageTag, Log.VERBOSE))
            return;

        int logResult = Log.v(logMessageTag, logMessage, throwableException);
        if (logResult > 0)
            logToFile(context, logMessageTag, logMessage + "\r\n" + Log.getStackTraceString(throwableException));
    }

    private static String getDateTimeStamp() {
        Date dateNow = Calendar.getInstance().getTime();
        return (DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault()).format(dateNow));
    }

    public static void clearLogFile() {
        try {
            File logFile = new File(Environment.getExternalStorageDirectory(), "InstantMessengerLogs.txt");
            if (logFile.exists())
                logFile.delete();

        } catch (Exception e) {
            Log.e("Logger", "Unable to clear file.");
        }
    }
    private static void logToFile(Context context, String logMessageTag, String logMessage) {
        try {
            File logFile = new File(Environment.getExternalStorageDirectory(), "TestApplicationLog.txt");
            if (!logFile.exists())
                logFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(String.format("%1s [%2s]:%3s\r\n", getDateTimeStamp(), logMessageTag, logMessage));
            writer.close();
            MediaScannerConnection.scanFile(context,
                    new String[]{logFile.toString()},
                    null,
                    null);

        } catch (IOException e) {
            Log.e("Logger", "Unable to log exception to file.");
        }
    }
}
