package com.example.grandtheftradio.network

import android.content.Context
import com.example.grandtheftradio.entities.Game
import kotlinx.coroutines.flow.Flow

interface RemoteGameDataSource {
    suspend fun getGames(context: Context) : Flow<List<Game>>
}