package com.example.birellerapp.birds


import android.os.Parcel
import android.os.Parcelable

data class BirdDataClass(
    val imageUrl: String,
    val name: String,
    val location: String,
    val description: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    // Add a no-argument constructor
    constructor() : this("", "", "", "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(name)
        parcel.writeString(location)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BirdDataClass> {
        override fun createFromParcel(parcel: Parcel): BirdDataClass {
            return BirdDataClass(parcel)
        }

        override fun newArray(size: Int): Array<BirdDataClass?> {
            return arrayOfNulls(size)
        }
    }
}
