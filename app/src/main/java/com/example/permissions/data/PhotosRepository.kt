package com.example.permissions.data

import com.example.permissions.entity.PhotoInRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotosRepository @Inject constructor(
    private val photoDao: PhotoDao
) {
    fun getPhotos(): Flow<List<PhotoInRepo>> {
        return photoDao.getAll()
    }

    suspend fun addPhoto(newPhoto: NewPhotoInRepoDto) {
        photoDao.insert(newPhoto)
    }

    suspend fun deleteAll() {
        photoDao.deleteAll()
    }
}