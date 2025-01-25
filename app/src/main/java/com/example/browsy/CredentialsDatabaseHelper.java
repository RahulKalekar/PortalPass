package com.example.browsy;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CredentialsDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "credentials.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CREDENTIALS = "credentials";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    public CredentialsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CREDENTIALS_TABLE = "CREATE TABLE " + TABLE_CREDENTIALS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_CREDENTIALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CREDENTIALS);
        onCreate(db);
    }

    public void addCredential(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        db.insert(TABLE_CREDENTIALS, null, values);
        db.close();
    }

    public void deleteCredential(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CREDENTIALS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Credential> getAllCredentials() {
        List<Credential> credentialsList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CREDENTIALS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Credential credential = new Credential();
                credential.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                credential.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
                credential.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
                credentialsList.add(credential);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return credentialsList;
    }
}