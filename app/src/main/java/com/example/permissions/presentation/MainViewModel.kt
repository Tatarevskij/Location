package com.example.permissions.presentation

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture.OutputFileResults
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.permissions.App
import com.example.permissions.domain.AddPhotoUseCase
import com.example.permissions.domain.DeleteAllPhotosUseCase
import com.example.permissions.domain.GetPhotosUseCase
import com.example.permissions.domain.GetPoiListUseCase
import com.example.permissions.entity.*
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val addPhotoUseCase: AddPhotoUseCase,
    private val deleteAllPhotosUseCase: DeleteAllPhotosUseCase,
    private val getPoiListUseCase: GetPoiListUseCase,
    // private val context: Context,
    val mapKit: MapKit

) : ViewModel() {
    var locationIsAvailable = false
    private val locationManager = mapKit.createLocationManager()
    private val locationListener = object : LocationListener {
        override fun onLocationUpdated(location: Location) {
            _locationFlow.value = location.position
            getPoiList(location.position.latitude, location.position.longitude)
            Log.d("TagCheck", "LocationUpdated " + location.position.longitude)
            Log.d("TagCheck", "LocationUpdated " + location.position.latitude)
        }

        override fun onLocationStatusUpdated(locationStatus: LocationStatus) {
            locationIsAvailable = locationStatus == LocationStatus.AVAILABLE
        }
    }

    private val _poiFlow = MutableStateFlow<List<Poi>>(emptyList())
    val poiFlow = _poiFlow.asStateFlow()

    private val _locationFlow = MutableStateFlow(Point())
    val locationFlow = _locationFlow.asStateFlow()


    fun addPhotoToDb(outputFileResults: OutputFileResults, contentValues: ContentValues) {
        val photo = Photo(
            uri = outputFileResults.savedUri.toString(),
            date = contentValues.get(MediaStore.MediaColumns.DISPLAY_NAME).toString()
        )
        viewModelScope.launch {
            addPhotoUseCase.execute(photo)
        }
    }

    fun getAllPhotos(photosAdapter: PhotosAdapter) {
        viewModelScope.launch(Dispatchers.IO) {
            getPhotosUseCase.execute().collect {
                photosAdapter.submitList(it)
            }
        }
    }

    fun deleteAllPhotos() {
        viewModelScope.launch {
            deleteAllPhotosUseCase.execute()
        }
    }

    fun getPoiList(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                getPoiListUseCase.execute(lat, lon)
            }.fold(
                onSuccess = {
                    _poiFlow.value = it.poiList
                },
                onFailure = { Log.d("MainViewModel", it.message ?: "") }
            )
        }
    }

    fun locationSubscribe() {
        locationManager.subscribeForLocationUpdates(
            0.0,
            0,
            0.0,
            true,
            FilteringMode.OFF,
            locationListener
        )
    }

    fun locationUnsubscribe() {
        locationManager.unsubscribe(locationListener)
    }

}