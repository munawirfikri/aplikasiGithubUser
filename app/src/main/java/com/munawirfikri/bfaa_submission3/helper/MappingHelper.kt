package com.munawirfikri.bfaa_submission3.helper

import android.database.Cursor
import com.munawirfikri.bfaa_submission3.database.DatabaseContract
import com.munawirfikri.bfaa_submission3.model.User
import java.util.ArrayList

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<User> {
        val listUser = ArrayList<User>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.FavColumns._ID))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
                val userURL = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USER_URL))
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR))
                listUser.add(User(id, null, username, userURL, avatar))
            }
        }
        return listUser
    }

    fun mapCursorToObject(notesCursor: Cursor?): User {
        var user = User()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(DatabaseContract.FavColumns._ID))
            val username = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USERNAME))
            val userURL = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.USER_URL))
            val avatar = getString(getColumnIndexOrThrow(DatabaseContract.FavColumns.AVATAR))
            user = User(id, null, username, userURL, avatar)
        }
        return user
    }
}