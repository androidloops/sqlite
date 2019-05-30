package com.mobizone.notessqlite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes-room")
class Note {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "note")
    var note: String? = null
    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null

    constructor()

    constructor(id: Int, note: String, timestamp: String) {
        this.id = id
        this.note = note
        this.timestamp = timestamp
    }

    companion object {
        const val TABLE_NAME = "notes"

        const val COLUMN_ID = "id"
        const val COLUMN_NOTE = "note"
        const val COLUMN_TIMESTAMP = "timestamp"

        // Create table SQL query
        const val CREATE_TABLE = (
                "CREATE TABLE " + TABLE_NAME + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NOTE + " TEXT,"
                        + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                        + ")")
    }
}
