package com.example.birellerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class Target_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_target)

        val webView=findViewById<WebView>(R.id.wvThree)
        webView.webViewClient= WebViewClient()

        //specify the URL
        val url="https://ebird.org/targets?region=South+Africa+%28ZA%29&r1=ZA&bmo=1&emo=12&r2=ZA&t2=life&mediaType="
        webView.loadUrl(url)
    }
}