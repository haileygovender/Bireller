package com.example.birellerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class TopBirds : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_birds)
        val webView=findViewById<WebView>(R.id.wvTwo)
        webView.webViewClient= WebViewClient()

        //specify the URL
        val url="https://ebird.org/top100?region=South+Africa+%28ZA%29&locInfo.regionCode=ZA&year=2023&rankedBy=spp"
        webView.loadUrl(url)
    }
}