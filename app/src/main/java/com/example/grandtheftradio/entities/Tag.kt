package com.example.grandtheftradio.entities

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(val tagName:String = ""): Parcelable
