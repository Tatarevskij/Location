package com.example.permissions.data

import androidx.room.*
import com.example.permissions.entity.PhotoInRepo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Transaction
    @Query("SELECT * FROM photo ORDER BY id DESC")
    fun getAll(): Flow<List<PhotoInRepo>>

    @Transaction
    @Query("DELETE FROM photo")
    suspend fun deleteAll()

    @Insert(entity = PhotoInRepo::class)
    suspend fun insert(photo: NewPhotoInRepoDto)

    @Delete
    suspend fun delete(photo: PhotoInRepo)

    @Update
    suspend fun update(photo: PhotoInRepo)
}

