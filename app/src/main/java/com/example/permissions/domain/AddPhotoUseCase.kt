package com.example.permissions.domain

import com.example.permissions.data.NewPhotoInRepoDto
import com.example.permissions.data.PhotosRepository
import com.example.permissions.entity.Photo
import javax.inject.Inject

class AddPhotoUseCase @Inject constructor(
    private val photosRepository: PhotosRepository
) {
    suspend fun execute(newPhoto: Photo) {
        val  newPhotoInRepoDto = NewPhotoInRepoDto(
            uri = newPhoto.uri,
            date = newPhoto.date
        )
        photosRepository.addPhoto(newPhotoInRepoDto)
    }
}