package com.example.notyscan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.notyscan.ui.theme.NotyScanTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class cameraScan : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel by viewModels<CameraViewVM>()

        if (!hasRequiredPermissions()) {
            requestPermissions(CAMERAX_PERMISSIONS, 0)
        }



        setContent {
            NotyScanTheme {

                val scaffoldState = rememberBottomSheetScaffoldState()
                val scope = rememberCoroutineScope()
                val controller = remember{
                    LifecycleCameraController(applicationContext).apply {
                        setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
                        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    }
                }
                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetPeekHeight = 0.dp,
                    sheetContent = {
                        val bitmaps by viewModel.bitmaps.collectAsState()
                        BottomSheetPhotosView(bitmaps = bitmaps, modifier = Modifier.fillMaxWidth())
                    }
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                    ) {
                        CameraView(controller,Modifier.fillMaxSize())
                    }
                    val clickScope = rememberCoroutineScope()
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                        controller.cameraSelector =
                                            CameraSelector.DEFAULT_FRONT_CAMERA
                                    } else {
                                        controller.cameraSelector =
                                            CameraSelector.DEFAULT_BACK_CAMERA
                                    }
                                }
                            )
                        },
                        contentAlignment = Alignment.BottomCenter){
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.SpaceAround){
                            IconButton(onClick = {
                                takePicture(
                                    controller,
                                    ontakenPicture = viewModel::onTakePhoto
                                )
                            }) {
                                Icon(imageVector = Icons.Default.Face,null)

                            }
                            IconButton(onClick = {
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Menu,null)

                            }

                        }

                    }





                }
            }
        }
    }
    private fun takePicture(
        controller : LifecycleCameraController,
        ontakenPicture: (Bitmap) -> Unit
    ){
        controller.takePicture(
            ContextCompat.getMainExecutor(applicationContext),
            object : OnImageCapturedCallback(){
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    ontakenPicture(image.toBitmap())

                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.d("cameraErr", "cannot take picture")
                }
            }

        )
    }


    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }


    companion object {
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }

}




