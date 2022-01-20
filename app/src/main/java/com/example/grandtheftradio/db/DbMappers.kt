package com.example.grandtheftradio.db

import com.example.grandtheftradio.db.game.RoomGameEntity
import com.example.grandtheftradio.db.radio.RoomRadioEntity
import com.example.grandtheftradio.db.tag.RoomTagEntity
import com.example.grandtheftradio.entities.Game
import com.example.grandtheftradio.entities.Radio
import com.example.grandtheftradio.entities.Tag

fun Game.fromDomain(): RoomGameEntity = RoomGameEntity(this.gameName)

fun Radio.fromDomain(): RoomRadioEntity =
    RoomRadioEntity(this.game.gameName, this.name, this.link, this.picLink, this.favorite)

fun Tag.fromDomain(): RoomTagEntity = RoomTagEntity(this.tagName)

fun MutableList<Tag>.fromDomain(): MutableList<RoomTagEntity> =
    mutableListOf<RoomTagEntity>().also { list ->
        this.forEach {
            list.add(it.fromDomain())
        }
    }

fun RoomGameEntity.toDomain() = Game(this.gameName)

fun RoomRadioEntity.toDomain(game: Game, tags: MutableList<Tag>) =
    Radio(
        this.radioName,
        this.link,
        this.picLink,
        favorite = this.favorite,
        game = game,
        tags = tags
    )

fun RoomTagEntity.toDomain(): Tag = Tag(this.tagName)

fun MutableList<RoomTagEntity>.toDomain(): MutableList<Tag> = mutableListOf<Tag>().also { list ->
    this.forEach {
        list.add(it.toDomain())
    }
}