package com.example.grandtheftradio.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.grandtheftradio.db.game.GameDao
import com.example.grandtheftradio.db.game.RoomGameEntity
import com.example.grandtheftradio.db.radio.RadioDao
import com.example.grandtheftradio.db.radio.RadioGameCrossRef
import com.example.grandtheftradio.db.radio.RoomRadioEntity
import com.example.grandtheftradio.db.tag.RoomTagEntity
import com.example.grandtheftradio.db.tag.TagDao
import com.example.grandtheftradio.db.tag.TagRadioCrossRef

@Database(
    entities = [RoomTagEntity::class,
                RoomRadioEntity::class,
               RoomGameEntity::class,
                RadioGameCrossRef::class,
               TagRadioCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class GamesDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao

    abstract fun radioDao(): RadioDao

    abstract fun tagDao(): TagDao
}