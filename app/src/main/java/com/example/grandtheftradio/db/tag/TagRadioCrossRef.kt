package com.example.grandtheftradio.db.tag

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.grandtheftradio.db.game.RoomGameEntity
import com.example.grandtheftradio.db.radio.RoomRadioEntity

@Entity(
    tableName = "radio_tags",
    primaryKeys = ["tagName", "radioName"],
    foreignKeys = [ForeignKey(
        entity = RoomTagEntity::class,
        parentColumns = ["tagName"],
        childColumns = ["tagName"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
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
    indices = [Index("tagName"), Index("radioName")]
)
data class TagRadioCrossRef(
    val tagName: String,
    val radioName: String,
    val gameName: String,
)
