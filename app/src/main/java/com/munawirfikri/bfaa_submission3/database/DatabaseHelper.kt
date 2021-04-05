package com.munawirfikri.bfaa_submission3.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.munawirfikri.bfaa_submission3.database.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.munawirfikri.bfaa_submission3.database.DatabaseContract.FavColumns

internal class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "dbgithubuser"
        private const val DATABASE_VERSION = 1
        private const val SQL_CREATE_TABLE_FAVORITE = " CREATE TABLE $TABLE_NAME " +
                " (${FavColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${FavColumns.ID_USER} TEXT NOT NULL UNIQUE," +
                " ${FavColumns.USERNAME} TEXT NOT NULL UNIQUE," +
                " ${FavColumns.USER_URL} TEXT NOT NULL UNIQUE," +
                " ${FavColumns.AVATAR} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_FAVORITE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}