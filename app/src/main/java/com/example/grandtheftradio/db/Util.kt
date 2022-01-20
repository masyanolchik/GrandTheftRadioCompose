package com.example.grandtheftradio.db

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lastUpdate")
val GAME_STATE: Preferences.Key<String> = stringPreferencesKey("lastGameDataUpdateDate")
val RADIO_STATE: Preferences.Key<String> = stringPreferencesKey("lastRadioDataUpdateDate")

suspend fun readGameDataState(context: Context) : Flow<Long> {
    return context.dataStore.data.map { date ->
        (date[GAME_STATE] ?: "0").toLong()
    }
}

suspend fun writeGameDataDate(context: Context) {
    context.dataStore.edit {
        date ->
        date[GAME_STATE] = System.currentTimeMillis().toString()
    }
}

suspend fun readRadioDataState(context: Context) : Flow<Long> {
    return context.dataStore.data.map { date ->
        (date[RADIO_STATE] ?: "0").toLong()
    }
}

suspend fun writeRadioDataDate(context: Context) {
    context.dataStore.edit {
            date ->
        date[RADIO_STATE] = System.currentTimeMillis().toString()
    }
}