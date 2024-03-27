package com.trulyao.eventis.models

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2Mode
import com.trulyao.eventis.utils.AppException
import java.util.Optional

data class UserModel(
    val id: Int? = null,
    val username: String? = null,
    val password: String? = null,
)

class User(database: SQLiteDatabase) {
    private val database: SQLiteDatabase;

    init {
        this.database = database
    }

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

    public fun findByUsername(username: String): Optional<UserModel> {
        val cursor = this.database.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE username = ?",
            arrayOf(username.lowercase())
        )

        if (cursor.moveToFirst()) {
            val user = UserModel(
                id = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_ID)),
                username = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_USERNAME)),
                password = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_PASSWORD)),
            )

            return Optional.of(user)
        }

        cursor.close()

        return Optional.empty()
    }

    public fun findByID(id: Int): Optional<UserModel> {
        val cursor = this.database.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?",
            arrayOf(id.toString())
        )

        if (cursor.moveToFirst()) {
            val user = UserModel(
                id = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_ID)),
                username = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_USERNAME)),
                password = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_PASSWORD)),
            )

            return Optional.of(user)
        }

        cursor.close()

        return Optional.empty()
    }

    public fun create(user: UserModel) {
        val values = ContentValues()
        val argon2 = Argon2Kt()
        val hashedPassword =
            user.password?.toByteArray()?.let {
                argon2.hash(
                    password = it,
                    mode = Argon2Mode.ARGON2_I,
                    salt = "thingmakagin".toByteArray(),
                    tCostInIterations = 5,
                    mCostInKibibyte = 65536
                ).encodedOutputAsString()
            } ?: throw AppException("Failed to hash password!")

        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_PASSWORD, hashedPassword)

        val result = this.database.insert(TABLE_NAME, null, values)
        if (result <= 0) {
            throw AppException("Failed to save user, please try again")
        }
    }

    public fun verifyPassword(password: String, hashedPassword: String): Boolean {
        val argon2 = Argon2Kt()
        return argon2.verify(
            mode = Argon2Mode.ARGON2_I,
            encoded = hashedPassword,
            password = password.toByteArray()
        )
    }
}