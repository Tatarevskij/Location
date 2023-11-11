package com.example.permissions.domain

import com.example.permissions.data.PhotosRepository
import javax.inject.Inject

class DeleteAllPhotosUseCase @Inject constructor(
    private val photosRepository: PhotosRepository
) {
    suspend fun execute() {
        photosRepository.deleteAll()
    }
}