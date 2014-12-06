package com.jakevb8.instantmessenger;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by jvanburen on 12/5/2014.
 */
public class LogActivity extends Activity {
    private ScrollView _scrollView;
    private TextView _logText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        _scrollView = (ScrollView) findViewById(R.id.log_scroll_view);
        _logText = (TextView) findViewById(R.id.log_text);
        loadLogFile();
    }

    private void loadLogFile() {
        String fileContents = null;
        try {
            File logFile = new File(Environment.getExternalStorageDirectory(), "TestApplicationLog.txt");
            fileContents = loadLogFile(logFile.getAbsolutePath());
            _logText.setText(fileContents);
        } catch (Exception e) {

        }
    }

    public String loadLogFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        File file = new File(filePath);
        if (file.exists()) {
            try {
                sb.append(FileUtils.readFileToString(file));
            } catch (IOException e) {
            }
        } else {
            sb.append(String.format("[File does not exist: %s]", filePath)).append(File.separator);
        }
        return sb.toString();
    }
}
