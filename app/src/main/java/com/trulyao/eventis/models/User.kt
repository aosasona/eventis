package com.trulyao.eventis.models

import android.database.sqlite.SQLiteDatabase

class User(database: SQLiteDatabase) {
    companion object {
        private const val TABLE_NAME = "users"

        // Column names
        private const val COLUMN_ID = "_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        const val MIGRATION_UP = """
            CREATE TABLE $TABLE_NAME ( 
                $COLUMN_ID INTEGER PRIMARY KEY, -- use SQLite default ROW ID
                $COLUMN_USERNAME TEXT NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL 
            );
            """

        const val MIGRATION_DOWN = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}