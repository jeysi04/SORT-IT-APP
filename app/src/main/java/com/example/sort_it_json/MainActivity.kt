package com.example.sort_it_json

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.core.view.ViewCompat
import android.content.res.ColorStateList
import androidx.core.widget.ImageViewCompat


class MainActivity : AppCompatActivity() {

    lateinit var homeBtn: ImageButton
    lateinit var aboutBtn: ImageButton
    lateinit var recycleBtn: ImageButton
    lateinit var faqBtn: ImageButton
    lateinit var feedbackBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Splash Screen before setContentView
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //FOR THE MENU TO BE ABOVE APP BUTTONS
        val root = findViewById<View>(R.id.rootLayout)

        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }


            //Finds the button on the MainActivity fragment
            val logoButton = findViewById<ImageButton>(R.id.logoButton)

            // Setting the logo icon to go to home page
            //Setting a click listener
            logoButton.setOnClickListener {
                // Start a fragment transaction using the Activity's FragmentManager
                supportFragmentManager.beginTransaction()
                    // Replace whatever fragment is currently inside fragment_container
                    // with a new instance of HomeFragment
                    .replace(R.id.fragment_container, HomeFragment())

                    // This allows the user to press the back button
                    .addToBackStack(null)
                    .commit()
            }

        // Buttons
        homeBtn = findViewById<ImageButton>(R.id.nav_home)
        aboutBtn = findViewById<ImageButton>(R.id.nav_about)
        recycleBtn = findViewById<ImageButton>(R.id.nav_rec)
        faqBtn = findViewById<ImageButton>(R.id.nav_faq)
        feedbackBtn = findViewById<ImageButton>(R.id.nav_feedback)

        // Default fragment
        replaceFragment(HomeFragment())

        // Button Click Listeners
        homeBtn.setOnClickListener {
            resetNavColors()
            ImageViewCompat.setImageTintList(homeBtn, ColorStateList.valueOf(getColor(R.color.yellow)))
            replaceFragment(HomeFragment())
        }

        aboutBtn.setOnClickListener {
            resetNavColors()
            ImageViewCompat.setImageTintList(aboutBtn, ColorStateList.valueOf(getColor(R.color.yellow)))
            replaceFragment(AboutFragment())
        }

        recycleBtn.setOnClickListener {
            resetNavColors()
            ImageViewCompat.setImageTintList(recycleBtn, ColorStateList.valueOf(getColor(R.color.yellow)))
            replaceFragment(SampleDecideFragment())
        }

        faqBtn.setOnClickListener {
            resetNavColors()
            ImageViewCompat.setImageTintList(faqBtn, ColorStateList.valueOf(getColor(R.color.yellow)))
            replaceFragment(FaqFragment())
        }

        feedbackBtn.setOnClickListener {
            resetNavColors()
            ImageViewCompat.setImageTintList(feedbackBtn, ColorStateList.valueOf(getColor(R.color.yellow)))
            replaceFragment(feedbackFragment())
        }



        }

    //Function to reset colors of buttons to white
    fun resetNavColors() {
        ImageViewCompat.setImageTintList(homeBtn, ColorStateList.valueOf(getColor(R.color.white)))
        ImageViewCompat.setImageTintList(aboutBtn, ColorStateList.valueOf(getColor(R.color.white)))
        ImageViewCompat.setImageTintList(recycleBtn, ColorStateList.valueOf(getColor(R.color.white)))
        ImageViewCompat.setImageTintList(faqBtn, ColorStateList.valueOf(getColor(R.color.white)))
        ImageViewCompat.setImageTintList(feedbackBtn, ColorStateList.valueOf(getColor(R.color.white)))
    }

    // Function to change fragment
    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    fun finishOnboarding() {
        // Hide the ViewPager
        val pager = findViewById<ViewPager>(R.id.pager)
        pager.visibility = View.GONE

        val dots = findViewById<View>(R.id.dots_indicator)
        dots.visibility = View.GONE

        // Load the main fragment into fragment_container
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SampleDecideFragment())
            .commit()
    }

}