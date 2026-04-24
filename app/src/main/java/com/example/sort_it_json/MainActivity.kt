package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {

            val filePath = result.data?.getStringExtra("file_path")

            if (filePath != null) {

                // FORCE LOADING SCREEN
                showLoadingFragment(filePath)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val fabCenter = findViewById<FloatingActionButton>(R.id.fab_center)

        bottomNav.itemIconTintList = null

        setupFeedbackLogic(bottomNav)

        // Load default fragment
        if (savedInstanceState == null) {
            switchFragment(NewHomeFragment())
        }

        handleIncomingIntent(intent)

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

        // Bottom Navigation (REPLACE MODE)
        bottomNav.setOnItemSelectedListener { item ->

            if (!canNavigate()) return@setOnItemSelectedListener false

            when (item.itemId) {
                R.id.nav_home -> switchFragment(NewHomeFragment())
                R.id.nav_bookmark -> switchFragment(BookmarkFragment())
                R.id.nav_faq -> switchFragment(FaqFragment())
                R.id.nav_feedback -> switchFragment(feedbackFragment())
            }

            true
        }
    }

    // REPLACE FRAGMENT (DESTROYS OLD ONE)
    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Show loading screen with file
    public fun showLoadingFragment(filePath: String) {
        val fragment = LoadingFragment().apply {
            arguments = Bundle().apply {
                putString("file_path", filePath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onResume() {
        super.onResume()

        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)

        // DO NOT override loading fragment
        if (current !is LoadingFragment && current == null) {
            switchFragment(NewHomeFragment())
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {

        val filePath = intent.getStringExtra("file_path")

        if (filePath != null) {
            showLoadingFragment(filePath)
        }
    }

    // Feedback logic
    private fun setupFeedbackLogic(bottomNav: BottomNavigationView) {
        val closeGlobalButton = findViewById<MaterialButton>(R.id.close_global_success_button)

        closeGlobalButton?.setOnClickListener {
            hideSuccessOverlay()
            bottomNav.selectedItemId = R.id.nav_home
            switchFragment(NewHomeFragment())
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