package com.jakevb8.instantmessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jvanburen on 12/1/2014.
 */
public class MessageDatabase {
    private Context _context;
    private MessageSqliteOpenHelper _dbHelper;
    private SQLiteDatabase _database;

    public MessageDatabase(Context context) {
        _context = context;
        _dbHelper = new MessageSqliteOpenHelper(context);
    }

    public void open() {
        try {
            if(_database == null || !_database.isOpen()) {
                _database = _dbHelper.getWritableDatabase();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            _dbHelper.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessage(Message message) {
        open();
        try {
            long success = _database.insert(MessageSqliteOpenHelper.MESSAGES_TABLE, null, MessageSqliteOpenHelper.getInsertContentValues(message));
            Log.d("MessageDatabase.addMessage", "Success=" + success);
        }
        catch (Exception e) {

        }
    }

    public void setMessageRead(Message message) {
        try {
            open();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MessageSqliteOpenHelper.IS_NEW_COLUMN, 0);
            _database.update(MessageSqliteOpenHelper.MESSAGES_TABLE, contentValues, MessageSqliteOpenHelper.ID_COLUMN + "=" + message.Id, null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getNewMessageCount() {
        open();
        int count = 0;
        try {
            String countQuery = "SELECT  * FROM " + MessageSqliteOpenHelper.MESSAGES_TABLE + " WHERE " + MessageSqliteOpenHelper.IS_NEW_COLUMN + "='1'";
            Cursor cursor = _database.rawQuery(countQuery, null);
            count = cursor.getCount();
            cursor.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public List<Message> getNewMessages() {
        List<Message> messages = new ArrayList<Message>();

        open();
        Cursor cursor =_database.query(MessageSqliteOpenHelper.MESSAGES_TABLE, null, MessageSqliteOpenHelper.IS_NEW_COLUMN + "='1'", null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            messages.add(fromCursor(cursor));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return messages;
    }

    private Message fromCursor(Cursor cursor) {
        Message message = new Message();
        message.Id = cursor.getInt(cursor.getColumnIndex(MessageSqliteOpenHelper.ID_COLUMN));
        message.Message = cursor.getString(cursor.getColumnIndex(MessageSqliteOpenHelper.MESSAGE_COLUMN));
        message.UserId = cursor.getString(cursor.getColumnIndex(MessageSqliteOpenHelper.USER_ID_COLUMN));
        message.UserName = cursor.getString(cursor.getColumnIndex(MessageSqliteOpenHelper.USER_NAME_COLUMN));
        return  message;
    }
}
