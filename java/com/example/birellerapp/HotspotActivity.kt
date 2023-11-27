package com.example.birellerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class HotspotActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot)

        val webView=findViewById<WebView>(R.id.wvOne)
        webView.webViewClient= WebViewClient()

        //specify the URL
        val url="https://ebird.org/hotspots"
        webView.loadUrl(url)

    }
}