package com.example.module5taskspart3.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.module5taskspart3.data.model.TodoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO — интерфейс доступа к данным Room.
 * Room генерирует реализацию через KSP автоматически.
 */
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(todos: List<TodoItemEntity>)

    @Update
    suspend fun update(todo: TodoItemEntity)

    @Delete
    suspend fun delete(todo: TodoItemEntity)

    // Flow — UI обновляется автоматически при изменении данных
    @Query("SELECT * FROM todos ORDER BY id DESC")
    fun getAll(): Flow<List<TodoItemEntity>>

    // Для проверки однократного импорта JSON
    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getCount(): Int
}