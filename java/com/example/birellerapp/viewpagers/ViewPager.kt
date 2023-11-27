package com.example.birellerapp.viewpagers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.birellerapp.R
import com.example.birellerapp.authentication.AuthenticationActivity

class ViewPager : AppCompatActivity() {
    //variables
    private lateinit var slideViewPager: ViewPager
    private lateinit var dotIndicator: LinearLayout
    private lateinit var backButton: Button
    private lateinit var nextButton: Button
    private lateinit var skipButton: Button
    private lateinit var dots: Array<TextView?>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private val viewPagerListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            setDotIndicator(position)

            if (position > 0) {
                backButton.visibility = View.VISIBLE
            } else {
                backButton.visibility = View.INVISIBLE
            }
            if (position == 3) {
                nextButton.text = "Finish"
                nextButton.setOnClickListener {
                    startActivity(Intent(this@ViewPager,LandingPage::class.java))
                }
            } else {
                nextButton.text = "Next"
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        backButton = findViewById(R.id.backButton)
        nextButton = findViewById(R.id.nextButton)
        skipButton = findViewById(R.id.skipButton)

        backButton.setOnClickListener {
            if (getItem(0) > 0) {
                slideViewPager.setCurrentItem(getItem(-1), true)
            }
        }

        nextButton.setOnClickListener {
            if (getItem(0) < 4) {
                slideViewPager.setCurrentItem(getItem(1), true)
            } else {
                val intent = Intent(this@ViewPager, LandingPage::class.java)
                startActivity(intent)
                finish()
            }
        }

        skipButton.setOnClickListener {
            val intent = Intent(this@ViewPager, LandingPage::class.java)
            startActivity(intent)
            finish()
        }

        slideViewPager = findViewById(R.id.slideViewPager)
        dotIndicator = findViewById(R.id.dotIndicator)

        viewPagerAdapter = ViewPagerAdapter(this)
        slideViewPager.adapter = viewPagerAdapter

        setDotIndicator(0)
        slideViewPager.addOnPageChangeListener(viewPagerListener)
    }

    private fun setDotIndicator(position: Int) {
        dots = arrayOfNulls(4)
        dotIndicator.removeAllViews()

        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226", Html.FROM_HTML_MODE_LEGACY)
            dots[i]?.textSize = 35F
            dots[i]?.setTextColor(resources.getColor(R.color.B4, theme))
            dotIndicator.addView(dots[i])
        }
        dots[position]?.setTextColor(resources.getColor(R.color.BL2, theme))
    }

    private fun getItem(i: Int): Int {
        return slideViewPager.currentItem + i
    }
}