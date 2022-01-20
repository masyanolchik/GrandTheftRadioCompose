package com.example.grandtheftradio

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import coil.annotation.ExperimentalCoilApi
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.radiochooser.PlaybackState
import com.example.grandtheftradio.radiochooser.RadioChooserViewModel
import com.example.grandtheftradio.radiochooser.RadioChooserViewModelFactory
import com.example.grandtheftradio.ui.MainScreen
import com.example.grandtheftradio.ui.explore.RadioClickListener
import com.example.grandtheftradio.ui.theme.GrandTheftRadioTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

class MainActivity : AppCompatActivity() {
    @ExperimentalCoroutinesApi
    @FlowPreview
    private lateinit var viewModel: RadioChooserViewModel

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = RadioChooserViewModelFactory(application)
            .create(RadioChooserViewModel::class.java)

        val onPlayPause: (PlaybackState) -> Unit = { state ->
            when(state) {
                PlaybackState.PLAYING -> viewModel.playCurrent()
                else -> viewModel.stopPlaying()
            }
        }

        val onDismiss: () -> Unit = {
            viewModel.stopPlaying(true)
        }

        setContent {
            GrandTheftRadioTheme {
                MainScreen(
                    radioClickListener = object : RadioClickListener{
                        override fun onClick(radio: Radio) {
                            viewModel._isPlayerHidden.value = false
                            viewModel.playRadio(radio)
                        }

                    },
                    onDismiss = onDismiss,
                    onPlayPause = onPlayPause,
                    viewModel = viewModel)
            }
        }
    }
}