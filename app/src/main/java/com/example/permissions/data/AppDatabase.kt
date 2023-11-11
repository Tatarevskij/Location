package com.example.permissions.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.permissions.entity.PhotoInRepo

@Database(entities = [PhotoInRepo::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getPhotoDao(): PhotoDao
}