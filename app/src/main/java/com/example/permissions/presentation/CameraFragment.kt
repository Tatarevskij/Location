package com.example.permissions.presentation

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.permissions.databinding.FragmentCameraBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss"

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var executor: Executor
    private var imageCapture: ImageCapture? = null
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    private val name: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
        .format(System.currentTimeMillis())

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        executor = this.context?.let { ContextCompat.getMainExecutor(it) }!!
        startCamera()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.makePhotoBtn.setOnClickListener{
           makePhoto()
        }
    }

    private fun startCamera() {
        val cameraProvideFuture = this.context?.let { ProcessCameraProvider.getInstance(it) }
        cameraProvideFuture?.addListener(
            {
                val cameraProvider = cameraProvideFuture.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.cameraView.surfaceProvider)
                imageCapture = ImageCapture.Builder().build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            }, executor
        )
    }


    private fun makePhoto() {
        val imageCapture: ImageCapture = imageCapture ?: return
        val contentValues: ContentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outputOptions: ImageCapture.OutputFileOptions? = this.context?.contentResolver?.let {
            ImageCapture.OutputFileOptions
                .Builder(
                    it,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
                .build()
        }

        if (outputOptions != null) {
            imageCapture.takePicture(
                outputOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        viewModel.addPhotoToDb(outputFileResults, contentValues)
                        Toast.makeText(this@CameraFragment.context, "Photo saved on ${outputFileResults.savedUri}", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(this@CameraFragment.context, "Photo failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                        exception.printStackTrace()
                    }
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}