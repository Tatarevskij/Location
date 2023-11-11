package com.example.permissions.data

import com.example.permissions.entity.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PoiListDto(
    @Json(name = "features") override val poiList: List<PoiDto>
) : PoiList {
    @JsonClass(generateAdapter = true)
    data class PoiDto(
        @Json(name = "geometry") override val geometry: GeometryDto,
        @Json(name = "properties") override val properties: PropertiesDto
    ) : Poi {
        @JsonClass(generateAdapter = true)
        data class GeometryDto(
            override val coordinates: List<String>
        ) : Geometry
        @JsonClass(generateAdapter = true)
        data class PropertiesDto(
            @Json(name = "xid") override val xid: String,
            @Json(name = "name") override val name: String
        ) : Properties
    }
}