package com.example.grandtheftradio.db.radio

import com.example.grandtheftradio.db.fromDomain
import com.example.grandtheftradio.db.game.GameDao
import com.example.grandtheftradio.db.tag.TagDao
import com.example.grandtheftradio.db.tag.TagRadioCrossRef
import com.example.grandtheftradio.db.toDomain
import com.example.grandtheftradio.entities.Radio

class RadioLocalDataSource(
    private val radioDao: RadioDao,
    private val gameDao: GameDao,
    private val tagDao: TagDao
) {
    fun addRadio(radio: Radio) {
        gameDao.addGame(radio.game.fromDomain())
        tagDao.addTags(radio.tags.fromDomain())
        radioDao.addRadio(radio.fromDomain())
        radio.tags.forEach {
            tagDao.addTagForRadio(TagRadioCrossRef(it.tagName,radio.name,radio.game.gameName))
        }
    }

    fun updateRadio(radio: Radio) {
        radioDao.updateRadio(radio.fromDomain())
    }

    fun getRadios() : MutableList<Radio> {
        val radios = mutableListOf<Radio>()
        radioDao.getRadios().forEach { localRadio ->
            val localGame = gameDao.getGame(localRadio.parentGameName)
            val radioTags = tagDao.getTagsForRadio(localRadio.radioName, localGame.gameName)
            radios.add(localRadio.toDomain(localGame.toDomain(), radioTags.toDomain()))
        }
        return radios
    }
}