package com.trulyao.eventis.models

import android.database.sqlite.SQLiteDatabase

class Event (database: SQLiteDatabase) {
    companion object {
        private const val TABLE_NAME = "events"

        // Column names
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_COMPLETED = "is_completed"
        private const val COLUMN_USER_ID = "user_id"

        const val MIGRATION_UP = """
            CREATE TABLE $TABLE_NAME ( 
                $COLUMN_ID INTEGER PRIMARY KEY, -- use SQLite default ROW ID
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_USER_ID INTEGER NOT NULL,
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES users (id)
            );
            """

        const val MIGRATION_DOWN = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}