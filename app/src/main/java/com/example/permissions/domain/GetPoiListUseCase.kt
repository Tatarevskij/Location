package com.example.permissions.domain

import com.example.permissions.data.PoiRepository
import com.example.permissions.entity.PoiList
import javax.inject.Inject

class GetPoiListUseCase @Inject constructor(
    private val poiRepository: PoiRepository
) {
    suspend fun execute(lat: Double, lon: Double): PoiList  {
       return poiRepository.getPoiList(lat.toString(), lon.toString())
    }
}