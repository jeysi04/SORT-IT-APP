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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    // Camera launcher to receive PredictResponse from CameraActivity
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val response = result.data?.getParcelableExtra<PredictResponse>("predict_response")
            if (response != null) {
                // Show the result fragment on top of current fragment
                showAnalysisResult(response)
            }
        }
    }

    // Keep references to fragments
    private val homeFragment = HomeFragment()
    private val aboutFragment = AboutFragment()
    private val faqFragment = FaqFragment()
    private val feedbackFragment = feedbackFragment()

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Splash Screen before setContentView
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.itemIconTintList = null  // Keep original icon colors

        // Initialize Feedback Logic
        setupFeedbackLogic(bottomNav)

        // Add all fragments once, hide all except home
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, feedbackFragment, "feedback").hide(feedbackFragment)
            .add(R.id.fragment_container, faqFragment, "faq").hide(faqFragment)
            .add(R.id.fragment_container, aboutFragment, "about").hide(aboutFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()

        // Logo button to go home
        val logoButton = findViewById<ImageButton>(R.id.logoButton)
        logoButton.setOnClickListener {
            switchFragment(homeFragment)
            bottomNav.selectedItemId = R.id.nav_home
        }

        // FAB button to open camera
        val fabCenter = findViewById<FloatingActionButton>(R.id.fab_center)
        fabCenter.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
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
                R.id.nav_home -> homeFragment
                R.id.nav_about -> aboutFragment
                R.id.nav_faq -> faqFragment
                R.id.nav_feedback -> feedbackFragment
                else -> null
            }

            if (fragment != null) {
                switchFragment(fragment)
                true
            } else {
                false
            }
        }
    }

    // Switch fragments without recreating
    private fun switchFragment(target: Fragment) {
        if (activeFragment == target) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commit()

        activeFragment = target
    }

    // Standard Navigation Helper
    fun loadFragment(fragment: Fragment) {
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

        switchFragment(SampleDecideFragment())
    }

    // # Feedback Section - Copy everything below this line for feedback integration

    private fun setupFeedbackLogic(bottomNav: BottomNavigationView) {
        val globalOverlay = findViewById<View>(R.id.global_success_overlay)
        val closeGlobalButton = findViewById<MaterialButton>(R.id.close_global_success_button)

        closeGlobalButton?.setOnClickListener {
            hideSuccessOverlay()
            // Redirect to Home
            bottomNav.selectedItemId = R.id.nav_home
            loadFragment(HomeFragment())
        }
    }

    // === New: Show analysis result fragment on top ===
    private fun showAnalysisResult(response: PredictResponse) {
        val resultFragment = RecyclableresultFragment().apply {
            arguments = Bundle().apply {
                putParcelable("predict_response", response)
            }
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, resultFragment, "predict_result")
            .addToBackStack("predict_result")
            .commit()
    }

    private fun canNavigate(): Boolean {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is feedbackFragment) {
            if (currentFragment.isFeedbackInProgress()) {
                Toast.makeText(this, "Please finish or send your feedback before leaving!", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    fun showSuccessOverlay() {
        findViewById<View>(R.id.global_success_overlay)?.visibility = View.VISIBLE
    }

    fun hideSuccessOverlay() {
        findViewById<View>(R.id.global_success_overlay)?.visibility = View.GONE
    }
}