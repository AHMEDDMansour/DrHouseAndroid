package com.example.appdrhouseandroid.ID

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appdrhouseandroid.ui.theme.OCR.OCRViewModel
import com.example.appdrhouseandroid.data.repositories.OcrRepository
class OCRViewModelFactory(private val ocrRepository: OcrRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OCRViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OCRViewModel(ocrRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
