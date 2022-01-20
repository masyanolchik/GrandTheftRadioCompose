package com.example.grandtheftradio.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Game(
    var gameName : String = "",
) : Parcelable