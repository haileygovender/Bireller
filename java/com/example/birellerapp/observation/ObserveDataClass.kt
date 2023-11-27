package com.example.birellerapp.observation
import android.os.Parcel
import android.os.Parcelable

class ObserveDataClass(
    var id: String? = null,
    var imageURL: String? = null,
    var latitude: String? = null,
    var longitude: String? = null,
    var location: String? = null,
    var birds: String? = null,
    var date: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(imageURL)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(location)
        parcel.writeString(birds)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ObserveDataClass> {
        override fun createFromParcel(parcel: Parcel): ObserveDataClass {
            return ObserveDataClass(parcel)
        }

        override fun newArray(size: Int): Array<ObserveDataClass?> {
            return arrayOfNulls(size)
        }
    }
}
