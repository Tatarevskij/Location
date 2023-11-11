package com.example.permissions.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.permissions.R
import com.example.permissions.databinding.FragmentMapsBinding
import com.google.android.gms.tasks.CancellationTokenSource
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {
    private val viewModel: MainViewModel by viewModels()
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var cancellationToken: CancellationTokenSource
    private lateinit var mapObjects: MapObjectCollection
    private lateinit var markerDataList: MutableList<MapObject>
    private lateinit var mapObjectTapListener: MapObjectTapListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        cancellationToken = CancellationTokenSource()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById(R.id.mapview)
        mapObjects = mapView.map.mapObjects
        markerDataList = mutableListOf()
        val userLocationLayer = viewModel.mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true

        mapObjectTapListener = MapObjectTapListener { mapObject, _ ->
            binding.info.text = mapObject.userData.toString()
            true
        }

        viewModel.locationSubscribe()

        viewLifecycleOwner.lifecycleScope
            .launchWhenStarted {
                viewModel.poiFlow.collect { poiList ->
                    removeMarkers()
                    if (viewModel.locationIsAvailable) {
                        poiList.forEach { poi ->
                            addMarker(
                                poi.geometry.coordinates[1].toDouble(),
                                poi.geometry.coordinates[0].toDouble(),
                                poi.properties.name
                            )
                        }
                    }
                }
            }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.locationFlow.collect {
                if (viewModel.locationIsAvailable) {
                    moveCamera(it, "0")
                }
            }
        }

        binding.currentLocationBtn.setOnClickListener {
            viewModel.locationUnsubscribe()
            val point = userLocationLayer.cameraPosition()?.target
            if (point != null) {
                moveCamera(point, "0")
            }
            viewModel.locationSubscribe()
        }

        binding.zoomInBtn.setOnClickListener {
            viewModel.locationUnsubscribe()
            val point = userLocationLayer.cameraPosition()?.target
            if (point != null) {
                moveCamera(point, "+")
            }
        }

        binding.zoomOutBtn.setOnClickListener {
            viewModel.locationUnsubscribe()
            val point = userLocationLayer.cameraPosition()?.target
            if (point != null) {
                moveCamera(point, "-")
            }
        }
    }

    private fun addMarker(
        latitude: Double,
        longitude: Double,
        userData: Any? = null
    ): PlacemarkMapObject {
        val point = Point(latitude, longitude)
        val marker = mapObjects.addPlacemark(
            point,
            ImageProvider.fromResource(this.context, R.drawable.pointer)
        )
        marker.userData = userData
        marker.addTapListener(mapObjectTapListener)
        markerDataList.add(marker)
        return marker
    }

    private fun removeMarkers() {
        markerDataList.forEach {
            mapObjects.remove(it)
        }
        markerDataList.clear()
    }

    private fun moveCamera(point: Point, zoom: String) {
        when (zoom) {
            "+" ->
                mapView.map.move(
                    CameraPosition(
                        point,
                        mapView.map.cameraPosition.zoom + 2.toFloat(),
                        0.0f,
                        0.0f
                    ),
                    Animation(Animation.Type.SMOOTH, 1F),
                    null
                )
            "-" ->
                mapView.map.move(
                    CameraPosition(point, mapView.map.cameraPosition.zoom - 2, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1F),
                    null
                )
            "0" ->
                mapView.map.move(
                    CameraPosition(point, 17F, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1F),
                    null
                )
        }

    }

    override fun onStop() {
        // Вызов onStop нужно передавать инстансам MapView и MapKit.
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        // Вызов onStart нужно передавать инстансам MapView и MapKit.
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cancellationToken.cancel()
    }
}