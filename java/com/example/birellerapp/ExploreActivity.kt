package com.example.birellerapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreActivity : AppCompatActivity() {
private lateinit var hotspot: ImageButton
    private lateinit var topBirds:ImageButton
    private lateinit var target:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        hotspot=findViewById(R.id.btnHotspots)
        topBirds=findViewById(R.id.btnTopBirds)
        target=findViewById(R.id.btnTarget)

        hotspot.setOnClickListener {
            startActivity(Intent(this,HotspotActivity::class.java))
        }

        topBirds.setOnClickListener {
            startActivity(Intent(this,TopBirds::class.java))
        }

        target.setOnClickListener {
            startActivity(Intent(this,Target_Activity::class.java))
        }
        getData()
    }

    private fun getData() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Data is being loaded - please wait")
        progressDialog.show()

        RetroInstance.someInterface.getData().enqueue(object : Callback<DataWeather?> {
            override fun onResponse(
                call: Call<DataWeather?>,
                response: Response<DataWeather?>
            ) {
            val image = findViewById<ImageView>(R.id.imageViewData)
                Glide.with(this@ExploreActivity).load(response.body()?.url).into(image)

                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<DataWeather?>, t: Throwable) {
                Toast.makeText(this@ExploreActivity, "Failed to read from the site", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        })
    }
}
