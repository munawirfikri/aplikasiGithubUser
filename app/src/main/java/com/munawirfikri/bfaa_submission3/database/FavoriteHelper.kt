package com.munawirfikri.bfaa_submission3.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.munawirfikri.bfaa_submission3.database.DatabaseContract.FavColumns.Companion.TABLE_NAME
import com.munawirfikri.bfaa_submission3.database.DatabaseContract.FavColumns.Companion.USERNAME
import java.sql.SQLException

class FavoriteHelper(context: Context) {

    private var dataBaseHelper: DatabaseHelper = DatabaseHelper(context)

    companion object {
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: FavoriteHelper? = null

        fun getInstance(context: Context): FavoriteHelper =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoriteHelper(context)
            }
    }

    @Throws(SQLException::class)
    fun open() {
        dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (dataBaseHelper.writableDatabase.isOpen)
            dataBaseHelper.writableDatabase.close()
    }

    fun queryAll(): Cursor {
        return dataBaseHelper.writableDatabase.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "${USERNAME} ASC",
            null)
    }

    fun queryByUsername(username: String): Cursor {
        return dataBaseHelper.writableDatabase.query(DATABASE_TABLE, null, "${USERNAME} = ?", arrayOf(username), null, null, null, null)
    }

    fun insert(values: ContentValues?): Long {
        return dataBaseHelper.writableDatabase.insert(DATABASE_TABLE, null, values)
    }

    fun update(username: String, values: ContentValues?): Int {
        return dataBaseHelper.writableDatabase.update(DATABASE_TABLE, values, "${USERNAME} = ?", arrayOf(username))
    }

    fun deleteById(username: String): Int {
        return dataBaseHelper.writableDatabase.delete(DATABASE_TABLE, "${USERNAME} = '$username'", null)
    }
}