package com.example.notyscan

import android.graphics.Bitmap
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CameraView(
    controller : LifecycleCameraController,
    modifier: Modifier = Modifier

) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(factory = { context ->
        PreviewView(context).apply {
            this.controller = controller
            controller.bindToLifecycle(lifecycleOwner)
        }
    },
        modifier = modifier
    )
}


@Composable
fun BottomSheetPhotosView(
    bitmaps : List<Bitmap>,
    modifier: Modifier = Modifier
) {
    if (bitmaps.isEmpty()) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            Text(text = "NO Photos Yettttttttttttt")
        }
    } else {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalItemSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp),
            modifier = modifier
        ) {
            items(bitmaps) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    Modifier.clip(RoundedCornerShape(10.dp))
                )
            }

        }
    }
}
