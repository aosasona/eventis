package com.trulyao.eventis

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.trulyao.eventis.models.Event
import com.trulyao.eventis.models.User

class Database(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_NAME = "eventis.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db == null) {
            System.err.println("DB is null, this is certainly unexpected behaviour")
            return;
        }

        db.execSQL(User.MIGRATION_UP)
        db.execSQL(Event.MIGRATION_UP)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        if (db == null) {
            System.err.println("DB is null, this is certainly unexpected behaviour")
            return;
        }

        db.execSQL(Event.MIGRATION_DOWN)
        db.execSQL(User.MIGRATION_DOWN)
    }
}