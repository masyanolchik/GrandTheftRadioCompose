package com.example.grandtheftradio.db.game

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="game")
data class RoomGameEntity (
    @PrimaryKey var gameName : String = "",
)