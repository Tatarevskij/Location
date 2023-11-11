package com.example.permissions.data

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class NewPhotoInRepoDto(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int? = null,
    @ColumnInfo(name = "uri")
    var uri: String,
    @ColumnInfo(name = "date")
    var date: String
)
