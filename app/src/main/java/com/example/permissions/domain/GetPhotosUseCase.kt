package com.example.permissions.domain

import com.example.permissions.entity.PhotoInRepo
import com.example.permissions.data.PhotosRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotosUseCase @Inject constructor(
    private val photosRepository: PhotosRepository
) {
    fun execute(): Flow<List<PhotoInRepo>>{
        return photosRepository.getPhotos()
    }
}