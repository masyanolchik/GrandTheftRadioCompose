package com.example.grandtheftradio.db.game

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addGame(roomGameEntity: RoomGameEntity)

    @Query("SELECT * FROM game WHERE gameName=:name")
    fun getGame(name:String): RoomGameEntity

    @Query("SELECT * FROM game")
    fun getGames():List<RoomGameEntity>
}