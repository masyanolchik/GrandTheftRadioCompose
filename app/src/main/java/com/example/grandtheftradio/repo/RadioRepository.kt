package com.example.grandtheftradio.repo

import android.content.Context
import android.util.ArrayMap
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.grandtheftradio.db.radio.RadioLocalDataSource
import com.example.grandtheftradio.db.readRadioDataState
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.network.FirebaseRemoteRadioDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class RadioRepository(
    private val firebaseRemoteRadioDataSource: FirebaseRemoteRadioDataSource,
    private val radioLocalDataSource: RadioLocalDataSource
) {

    fun updateRadio(radio:Radio) {
        radioLocalDataSource.updateRadio(radio)
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getRadios(context: Context): Flow<List<Radio>> {
        return readRadioDataState(context).flatMapConcat { savedTimeInMillis ->
            val currentDate = System.currentTimeMillis()
            val daysBetween = TimeUnit.MILLISECONDS.toDays(abs(currentDate - savedTimeInMillis))
            if (daysBetween > 1) {
                firebaseRemoteRadioDataSource.getRadios(context).map { radios ->
                    radios.forEach {
                        radioLocalDataSource.addRadio(it)
                    }
                }.flatMapConcat {
                    flow {
                        emit(radioLocalDataSource.getRadios())
                    }
                }
            } else {
                flow {
                    emit(radioLocalDataSource.getRadios())
                }
            }
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getRadiosByGame(context: Context): Flow<ArrayMap<Game, SnapshotStateList<Radio>>> {
        return readRadioDataState(context).flatMapConcat { savedTimeInMillis ->
            val currentDate = System.currentTimeMillis()
            val daysBetween = TimeUnit.MILLISECONDS.toDays(abs(currentDate - savedTimeInMillis))
            if (daysBetween > 1) {
                firebaseRemoteRadioDataSource.getRadios(context).map { radios ->
                    radios.forEach {
                        radioLocalDataSource.addRadio(it)
                    }
                }.flatMapConcat {
                    flow {
                        val map = ArrayMap<Game,SnapshotStateList<Radio>>()
                        radioLocalDataSource.getRadios().also { list ->
                            list.forEach { map[it.game] = SnapshotStateList() }
                            list.forEach {
                                map[it.game]?.add(it)
                            }
                        }
                        emit(map)
                    }
                }
            } else {
                flow {
                    val map = ArrayMap<Game,SnapshotStateList<Radio>>()
                    radioLocalDataSource.getRadios().also { list ->
                        list.forEach { map[it.game] = SnapshotStateList() }
                        list.forEach {
                            map[it.game]?.add(it)
                        }
                    }
                    emit(map)
                }
            }
        }
    }
}