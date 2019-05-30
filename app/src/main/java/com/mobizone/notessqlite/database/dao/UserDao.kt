package com.mobizone.notessqlite.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mobizone.notessqlite.database.entity.Note

@Dao
interface UserDao {
    @get:Query("SELECT * FROM `notes-room`")
    val all: List<Note>

    @Query("SELECT * FROM `notes-room` where id LIKE :id")
    fun findByNote(id: Int): Note

    @Query("SELECT COUNT(*) from `notes-room`")
    fun countUsers(): Int

    @Query("UPDATE `notes-room` SET note= :note WHERE id = :id")
    fun updateNote(id: Int, note: String): Int

    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(user: Note)
}
