package com.example.grandtheftradio.radiochooser

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.grandtheftradio.radiochooser.RadioChooserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

class RadioChooserViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    @ExperimentalCoroutinesApi
    @FlowPreview
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RadioChooserViewModel::class.java)) {
            return RadioChooserViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}