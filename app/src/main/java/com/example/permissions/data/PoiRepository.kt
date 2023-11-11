package com.example.permissions.data

import com.example.permissions.entity.PoiList
import kotlinx.coroutines.delay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

private const val BASE_URL = "https://api.opentripmap.com/0.1/en/"
private const val API_KEY = "5ae2e3f221c38a28845f05b6ab1979f4e50e8994e25c09e37655b454"

class PoiRepository @Inject constructor(){
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val searchPoiListApi: SearchPoiListApi = retrofit.create(
        SearchPoiListApi::class.java
    )

    suspend fun getPoiList(lat: String, lon: String): PoiList{
        delay(3000)
        return this.searchPoiListApi.getPoiListFromDto(lat, lon)
    }
}

interface SearchPoiListApi {
    @GET("places/radius?radius=200&apikey=$API_KEY")
    suspend fun getPoiListFromDto(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String
    ): PoiListDto
}
