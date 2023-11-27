package com.example.birellerapp.authentication

import android.text.Editable

class UserProfile{
    var userID: String? = null
    var userImage:String? = null
    var name: String? = null
    var email: String? = null
    var phoneNumber: String? = null
    var birthDate: String? = null

    // Default no-argument constructor
    constructor()

    constructor(
        userID: String,
        userImage: String,
        name: String,
        email: String,
        phoneNumber: String,
        birthDate: String
    ) {
        this.userID = userID
        this.userImage = userImage
        this.name = name
        this.email = email
        this.phoneNumber = phoneNumber
        this.birthDate = birthDate
    }
}






