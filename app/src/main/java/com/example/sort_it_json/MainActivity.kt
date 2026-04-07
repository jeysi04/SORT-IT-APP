package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val response = result.data?.getParcelableExtra<PredictResponse>("predict_response")
            response?.let { showAnalysisResult(it) }
        }
    }

    // Main fragments (ONLY these are persistent)
    private val homeFragment = HomeFragment()
    private val aboutFragment = AboutFragment()
    private val faqFragment = FaqFragment()
    private val feedbackFragment = feedbackFragment()

    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val fabCenter = findViewById<FloatingActionButton>(R.id.fab_center)
        bottomNav.itemIconTintList = null

        setupFeedbackLogic(bottomNav)

        // Add base fragments ONCE
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, feedbackFragment, "feedback").hide(feedbackFragment)
            .add(R.id.fragment_container, faqFragment, "faq").hide(faqFragment)
            .add(R.id.fragment_container, aboutFragment, "about").hide(aboutFragment)
            .add(R.id.fragment_container, homeFragment, "home")
            .commit()

        // Logo → Home
        findViewById<ImageButton>(R.id.logoButton).setOnClickListener {
            switchFragment(homeFragment)
            bottomNav.selectedItemId = R.id.nav_home
        }

        // FAB → Camera Activity
        fabCenter.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
        }

        // Insets
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

        // Bottom nav
        bottomNav.setOnItemSelectedListener { item ->
            if (!canNavigate()) return@setOnItemSelectedListener false

            // IMPORTANT: clear result fragment if open
            supportFragmentManager.popBackStack()

            val target = when (item.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_about -> aboutFragment
                R.id.nav_faq -> faqFragment
                R.id.nav_feedback -> feedbackFragment
                else -> null
            }

            target?.let {
                switchFragment(it)
                true
            } ?: false
        }
    }

    // ✅ SAFE fragment switching (tabs only)
    private fun switchFragment(target: Fragment) {
        if (activeFragment == target) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commit()

        activeFragment = target
    }

    // ✅ RESULT SCREEN (temporary)
    private fun showAnalysisResult(response: PredictResponse) {
        val resultFragment = RecyclableresultFragment().apply {
            arguments = Bundle().apply {
                putParcelable("predict_response", response)
            }
        }

        supportFragmentManager.beginTransaction()
            .hide(activeFragment) // hide current tab
            .add(R.id.fragment_container, resultFragment, "predict_result")
            .addToBackStack("predict_result") // allow back
            .commit()
    }

    // Slider
    fun finishOnboarding() {
        val intent = Intent(this, CameraActivity::class.java)
        cameraLauncher.launch(intent)
    }

    // Feedback
    private fun setupFeedbackLogic(bottomNav: BottomNavigationView) {
        val closeGlobalButton = findViewById<MaterialButton>(R.id.close_global_success_button)

        closeGlobalButton?.setOnClickListener {
            hideSuccessOverlay()
            bottomNav.selectedItemId = R.id.nav_home
            switchFragment(homeFragment)
        }
    }

    private fun canNavigate(): Boolean {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current is feedbackFragment && current.isFeedbackInProgress()) {
            Toast.makeText(this, "Finish feedback first!", Toast.LENGTH_SHORT).show()
            return false
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