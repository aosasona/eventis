package com.trulyao.eventis.models

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.trulyao.eventis.utils.AppException

class EventModel(
    id: Int?,
    title: String?,
    description: String?,
    isCompleted: Boolean?,
    userId: Int?,
    createdAt: Int?,
) {
    private val id: Int?;
    private val title: String?;
    private val description: String?;
    private val isCompleted: Boolean?;
    private val userId: Int?;
    private val createdAt: Int?;

    init {
        this.id = id
        this.title = title
        this.description = description
        this.isCompleted = isCompleted
        this.userId = userId
        this.createdAt = createdAt
    }
}

class Event(database: SQLiteDatabase) {
    private val database: SQLiteDatabase;

    init {
        this.database = database
    }

    companion object {
        private const val TABLE_NAME = "events"

        // Column names
        private const val COLUMN_ID = "_id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_COMPLETED = "is_completed"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_CREATED_AT = "created_at"

        const val MIGRATION_UP = """
            CREATE TABLE $TABLE_NAME ( 
                $COLUMN_ID INTEGER PRIMARY KEY, -- use SQLite default ROW ID
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_COMPLETED INTEGER NOT NULL DEFAULT 0,
                $COLUMN_USER_ID INTEGER NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL DEFAULT (unixepoch()),
                FOREIGN KEY ($COLUMN_USER_ID) REFERENCES users (id)
            );
            """

        const val MIGRATION_DOWN = "DROP TABLE IF EXISTS $TABLE_NAME"

    }

    public fun findEventsByUserID(userId: Int): List<EventModel> {
        val events = mutableListOf<EventModel>()

        val cursor = this.database.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_USER_ID = ?",
            arrayOf(userId.toString())
        )

        while (cursor.moveToNext()) {
            val event = EventModel(
                id = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_ID)),
                title = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_TITLE)),
                description = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                isCompleted = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_COMPLETED)) == 1,
                userId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_USER_ID)),
                createdAt = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_CREATED_AT)),
            )

            events.add(event)
        }

        cursor.close()

        return events
    }

    public fun createEvent(name: String, description: String, userId: Int) {
        val values = ContentValues()
        values.put(COLUMN_TITLE, name)
        values.put(COLUMN_DESCRIPTION, description)
        values.put(COLUMN_COMPLETED, 0)
        values.put(COLUMN_USER_ID, userId)

        val result = this.database.insert(TABLE_NAME, null, values)
        if (result <= 0) {
            throw AppException("Failed to save event, please try again")
        }
    }
}