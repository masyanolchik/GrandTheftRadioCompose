package com.example.grandtheftradio.db.radio

import androidx.room.*

@Dao
interface RadioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRadio(roomRadioEntity: RoomRadioEntity)

    @Update
    fun updateRadio(roomRadioEntity: RoomRadioEntity)

    @Query("SELECT * FROM radio WHERE radioName=:name")
    fun getRadio(name: String) : RoomRadioEntity

    @Query("SELECT * FROM radio")
    fun getRadios():List<RoomRadioEntity>

    @Query("SELECT * from radio where parentGameName=:name")
    fun getRadiosForGame(name: String): MutableList<RoomRadioEntity>
}