package com.example.permissions.presentation


import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.permissions.R
import com.example.permissions.databinding.FragmentPhotosListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PhotosListFragment : Fragment() {
    private var _binding: FragmentPhotosListBinding? = null
    private val binding get() = _binding!!
    private val photosAdapter = PhotosAdapter()
    private val viewModel: MainViewModel by viewModels()
    @SuppressLint("SuspiciousIndentation")
    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (!map.values.all { it })
            Toast.makeText(this.context, "Permission is not granted", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotosListBinding.inflate(inflater, container, false)
        checkPermission()
        binding.recyclerView.adapter = photosAdapter
        viewModel.getAllPhotos(photosAdapter)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cameraBtn.setOnClickListener {
            if (checkPermission()) {
                findNavController().navigate(R.id.action_photosListFragment_to_cameraFragment)
            }
        }
        binding.deleteAllBtn.setOnClickListener{
            viewModel.deleteAllPhotos()
        }

        binding.mapBtn.setOnClickListener{
            if (checkPermission()) {
                findNavController().navigate(R.id.action_photosListFragment_to_mapsFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermission(): Boolean {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            this.context?.let { ContextCompat.checkSelfPermission(it, permission) } == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(this.context, "permission is Granted", Toast.LENGTH_SHORT).show()
            return true
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            Toast.makeText(this.context, "To use this app you need camera and geolocation.", Toast.LENGTH_SHORT)
                .show()
            launcher.launch(REQUEST_PERMISSIONS)
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
        return false
    }


    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.CAMERA)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }.toTypedArray()

    }
}