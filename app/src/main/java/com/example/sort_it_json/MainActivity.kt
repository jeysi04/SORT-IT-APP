package com.example.sort_it_json

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Splash Screen before setContentView
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.itemIconTintList = null  // keep original icon colors

        // Set Home as default selection on startup
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
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

        // Set a listener that triggers when a BottomNavigationView item is selected
        bottomNav.setOnItemSelectedListener { item ->

            // Determine which menu item was clicked using its ID
            // Create the corresponding Fragment based on the selected item
            val fragment = when(item.itemId) {

                // If "Home" is clicked, create a new HomeFragment
                R.id.nav_home -> HomeFragment()

                // If "About" is clicked, create a new AboutFragment
                R.id.nav_about -> AboutFragment()

                // If "Recycle" is clicked, create a new SampleDecideFragment
                R.id.nav_recycle -> SampleDecideFragment()

                // If "FAQ" is clicked, create a new FaqFragment
                R.id.nav_faq -> FaqFragment()

                // If none of the IDs match, return null
                else -> null
            }

            // Check if a valid fragment was created
            if (fragment != null) {

                // Call your custom function to load/replace the fragment
                loadFragment(fragment)

                // Return true to indicate the selection was handled successfully
                true
            } else {

                // Return false if no fragment matched (selection not handled)
                false
            }
        }

    }

    //Function to replace the existing fragment with the fragment on the parameter
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)  // optional, allows back navigation
            .commit()
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