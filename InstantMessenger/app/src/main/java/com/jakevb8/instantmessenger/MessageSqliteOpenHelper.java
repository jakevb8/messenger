package com.jakevb8.instantmessenger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jvanburen on 12/1/2014.
 */
public class MessageSqliteOpenHelper extends SQLiteOpenHelper{
    private static final String DATABASE_NAME = "messages.db";
    private static final int DATABASE_VERSION = 1;
    public static final String MESSAGES_TABLE = "messages";
    public static final String ID_COLUMN = "id";
    public static final String MESSAGE_COLUMN = "message";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String USER_NAME_COLUMN = "user_name";
    public static final String IS_NEW_COLUMN = "is_new";

    private static final String DATABASE_CREATE = "create table "
            + MESSAGES_TABLE + "(" + ID_COLUMN + " integer primary key autoincrement, " + MESSAGE_COLUMN + " text, " + USER_ID_COLUMN + " text, " + USER_NAME_COLUMN + " text, " + IS_NEW_COLUMN + " numeric);";

    public MessageSqliteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ContentValues getInsertContentValues(Message message) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MESSAGE_COLUMN, message.Message);
        contentValues.put(USER_ID_COLUMN, message.UserId);
        contentValues.put(USER_NAME_COLUMN, message.UserName);
        contentValues.put(IS_NEW_COLUMN, 1);
        return  contentValues;
    }
}