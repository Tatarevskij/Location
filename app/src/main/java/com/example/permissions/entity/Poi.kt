package com.example.permissions.entity

interface Poi {
    val properties: Properties
    val geometry: Geometry
}

interface Properties {
    val xid: String
    val name: String
}

interface  Geometry {
    val coordinates: List<String>
}

interface PoiList {
    val poiList: List<Poi>
}