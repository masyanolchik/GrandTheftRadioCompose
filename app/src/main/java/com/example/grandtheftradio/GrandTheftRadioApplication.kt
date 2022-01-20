package com.example.grandtheftradio

import android.app.Application
import androidx.room.Room
import com.example.grandtheftradio.db.GamesDatabase
import timber.log.Timber

class GrandTheftRadioApplication : Application()
{
    lateinit var db : GamesDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            this,
            GamesDatabase::class.java, "database"
        ).build()
        Timber.plant(Timber.DebugTree())
    }
}