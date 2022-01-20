package com.example.grandtheftradio.db.radio

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.grandtheftradio.db.game.RoomGameEntity

@Entity(tableName = "radio", foreignKeys = [ForeignKey(
    entity = RoomGameEntity::class,
    parentColumns = ["gameName"],
    childColumns = ["parentGameName"],
    onDelete = ForeignKey.CASCADE
)])
data class RoomRadioEntity(
    var parentGameName : String = "",
    @PrimaryKey var radioName: String = "",
    var link: String = "",
    var picLink: String = "",
    var favorite: Boolean = false
)