package com.example.sort_it_json

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
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

    private lateinit var fabCenter: FloatingActionButton
    private var isCameraFlowActive = false

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {

            val filePath = result.data?.getStringExtra("file_path")

            if (filePath != null) {
                isCameraFlowActive = true
                showLoadingFragment(filePath)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        fabCenter = findViewById(R.id.fab_center)

        bottomNav.selectedItemId = R.id.nav_home

        setupFeedbackLogic(bottomNav)

        if (savedInstanceState == null) {
            switchFragment(NewHomeFragment())
        }

        handleIncomingIntent(intent)

        // FAB → Camera Activity
        fabCenter.setOnClickListener {
            isCameraFlowActive = true

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

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->

            val fragment = when (item.itemId) {
                R.id.nav_home -> NewHomeFragment()
                R.id.nav_bookmark -> BookmarkFragment()
                R.id.nav_faq -> FaqFragment()
                R.id.nav_feedback -> feedbackFragment()
                else -> null
            }

            if (fragment != null) {
                switchFragment(fragment)
                updateFab(fragment)
                updateIconTint(fragment)
                true
            } else {
                false
            }
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        supportFragmentManager.executePendingTransactions()

        updateFab(fragment)
        updateIconTint(fragment)
    }

    fun showLoadingFragment(filePath: String) {

        val fragment = LoadingFragment().apply {
            arguments = Bundle().apply {
                putString("file_path", filePath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateFab(fragment)
        updateIconTint(fragment)
    }

    override fun onResume() {
        super.onResume()

        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (current != null && current !is LoadingFragment) {
            updateFab(current)
            updateIconTint(current)
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


    fun setNav(itemId: Int) {
        findViewById<BottomNavigationView>(R.id.bottomNav)
            .selectedItemId = itemId
    }


    fun updateFab(fragment: Fragment) {

        when (fragment) {

            is LoadingFragment,
            is RecyclableresultFragment,
            is GuideListFragment,
            is WebViewFragment -> {

                fabCenter.imageTintList =
                    ColorStateList.valueOf(Color.parseColor("#F0CD6E"))
            }

            else -> {

                fabCenter.imageTintList =
                    ColorStateList.valueOf(Color.parseColor("#FFFFFF"))
            }
        }
    }

    fun startCamera(intent: Intent) {
        isCameraFlowActive = true
        cameraLauncher.launch(intent)
    }

    fun updateIconTint(fragment: Fragment) {

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        when (fragment) {

            is LoadingFragment,
            is RecyclableresultFragment,
            is GuideListFragment,
            is WebViewFragment -> {

                // visually highlight recycle tab ONLY if it exists
                bottomNav.menu.findItem(R.id.nav_recycle).isChecked = true
            }

            else -> {
                bottomNav.menu.findItem(R.id.nav_home).isChecked = true
            }
        }
    }
}