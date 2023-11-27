package com.example.birellerapp.viewpagers

import com.example.birellerapp.DataWeather
import retrofit2.Call
import retrofit2.http.GET


interface APIInterface {
    //get responses from the website
    //Call request to get and set something
    @GET("/gimme") // object has the base url
    //interface has the rest
    //WATCH the forward slashes, if the object contains the last slash the interface would not
    fun getData(): Call<DataWeather>


}
