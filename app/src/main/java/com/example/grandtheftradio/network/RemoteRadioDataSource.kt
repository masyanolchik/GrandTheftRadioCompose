package com.example.grandtheftradio.network

import android.content.Context
import com.example.grandtheftradio.entities.Radio
import kotlinx.coroutines.flow.Flow

interface RemoteRadioDataSource {
    suspend fun getRadios(context: Context): Flow<List<Radio>>
}