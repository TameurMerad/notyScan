package com.example.notyscan

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewVM : ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap:Bitmap){
        _bitmaps.value = _bitmaps.value + bitmap
    }

}