package com.example.birellerapp.settings

class SettingsClass {
    var userID: String = " "
    var travel:Boolean = true
    var distance: Double = 0.0


    // Default no-argument constructor
    constructor()

    constructor(
        userID: String,
        travel: Boolean,
        distance: Double

    ) {
        this.userID = userID
        this.travel = travel
        this.distance = distance

    }
}