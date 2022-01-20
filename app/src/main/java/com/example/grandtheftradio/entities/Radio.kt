package com.example.grandtheftradio.entities

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Radio(
    val name: String = "",
    val link: String = "",
    val picLink: String = "",
    val game: Game = Game(),
    var tags:MutableList<Tag> = arrayListOf(),
    var pic: Bitmap? = null,
    var mediaLink:String = "",
    var favorite: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Game::class.java.classLoader) ?: Game(""),
    ){
        parcel.readList(tags,Tag::class.java.classLoader)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeString(link)
        dest?.writeString(picLink)
        dest?.writeParcelable(game, flags)
        dest?.writeList(tags)
    }

    companion object CREATOR : Parcelable.Creator<Radio> {
        override fun createFromParcel(parcel: Parcel): Radio {
            return Radio(parcel)
        }

        override fun newArray(size: Int): Array<Radio?> {
            return arrayOfNulls(size)
        }
    }
}