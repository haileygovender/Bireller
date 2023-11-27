package com.example.birellerapp

import com.example.birellerapp.viewpagers.APIInterface
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroInstance {
    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://meme-api.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //pull the interface

    }// end of retrofit lazy method

    val someInterface by lazy {
        retrofit.create(APIInterface::class.java)
    }
}