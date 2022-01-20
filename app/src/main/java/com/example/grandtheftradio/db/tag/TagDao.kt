package com.example.grandtheftradio.db.tag

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTag(roomTagEntity: RoomTagEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    @JvmSuppressWildcards
    fun addTags(roomTagEntities: List<RoomTagEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addTagForRadio(tagRadioCross: TagRadioCrossRef)

    @Query("SELECT * FROM radio_tags WHERE radioName=:radioName AND gameName=:gameName")
    fun getTagsForRadio(radioName:String, gameName:String):MutableList<RoomTagEntity>
}