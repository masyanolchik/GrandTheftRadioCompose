package com.example.grandtheftradio.db.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="tag")
data class RoomTagEntity(
    @PrimaryKey var tagName : String = "",
)
