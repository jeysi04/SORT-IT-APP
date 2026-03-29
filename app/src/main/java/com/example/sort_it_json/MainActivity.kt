package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Camera launcher to receive photo path from CameraActivity
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            val photoPath = result.data?.getStringExtra("photo_path")

            if (!photoPath.isNullOrEmpty()) {
                val fragment = ConfirmImageFragment()
                fragment.arguments = Bundle().apply {
                    putString("photo_path", photoPath)
                }

                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Splash Screen before setContentView
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.itemIconTintList = null  // Keep original icon colors

        // Set Home as default selection on startup
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
        }

        // Logo button to go home
        val logoButton = findViewById<ImageButton>(R.id.logoButton)
        logoButton.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        // FAB button to open camera
        val fabCenter = findViewById<FloatingActionButton>(R.id.fab_center)
        fabCenter.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent) // launch camera and receive result
        }

        // Adjust bottom nav and FAB for system bars
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { _, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            bottomNav.setPadding(0, 0, 0, navBarInsets.bottom)
            fabCenter.translationY = -navBarInsets.bottom.toFloat()
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(fabCenter) { _, insets ->
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            fabCenter.translationY = -navBarInsets.bottom.toFloat()
            insets
        }

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment? = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_about -> AboutFragment()
                R.id.nav_faq -> FaqFragment()
                R.id.nav_feedback -> feedbackFragment()
                else -> null
            }

            if (fragment != null) {
                loadFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    // Function to load/replace fragments
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    // Optional onboarding finish function
    fun finishOnboarding() {
        val pager = findViewById<ViewPager>(R.id.pager)
        pager.visibility = View.GONE

        val dots = findViewById<View>(R.id.dots_indicator)
        dots.visibility = View.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SampleDecideFragment())
            .commit()
    }
}