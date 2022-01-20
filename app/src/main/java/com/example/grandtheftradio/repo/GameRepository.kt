package com.example.grandtheftradio.repo

import android.content.Context
import com.example.grandtheftradio.db.game.GameLocalDataSource
import com.example.grandtheftradio.db.readGameDataState
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.network.FirebaseRemoteGameDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class GameRepository(
    private val firebaseRemoteGameDataSource: FirebaseRemoteGameDataSource,
    private val gameLocalDataSource: GameLocalDataSource
) {
    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getGames(context: Context): Flow<List<Game>> {
        return readGameDataState(context).flatMapConcat { savedTimeInMillis ->
            val currentDate = System.currentTimeMillis()
            val daysBetween = TimeUnit.MILLISECONDS.toDays(abs(currentDate - savedTimeInMillis))
            if (daysBetween > 1) {
                firebaseRemoteGameDataSource.getGames(context).onEach { games ->
                    games.forEach {
                        gameLocalDataSource.addGame(it)
                    }
                }.flatMapConcat {
                    flow {
                        emit(gameLocalDataSource.getAllGames())
                    }
                }
            } else {
                flow {
                    emit(gameLocalDataSource.getAllGames())
                }
            }
        }
    }
}
