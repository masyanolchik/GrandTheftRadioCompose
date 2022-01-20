package com.example.grandtheftradio.db.radio

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.grandtheftradio.db.game.RoomGameEntity

@Entity(
    primaryKeys = ["radioName", "gameName"],
    foreignKeys = [ForeignKey(
        entity = RoomRadioEntity::class,
        parentColumns = ["radioName"],
        childColumns = ["radioName"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = RoomGameEntity::class,
        parentColumns = ["gameName"],
        childColumns = ["gameName"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("radioName"), Index("gameName")]
)
class RadioGameCrossRef (
    val radioName: String,
    val gameName: String,
)