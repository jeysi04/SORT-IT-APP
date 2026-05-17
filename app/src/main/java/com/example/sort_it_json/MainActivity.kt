package com.example.sort_it_json

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabCenter: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fabCenter = findViewById(R.id.fab_center)
        bottomNav = findViewById(R.id.bottomNav)

        // --- THE UNIVERSAL FIX (UPGRADED) ---
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: androidx.fragment.app.FragmentManager, f: Fragment) {
                    super.onFragmentStarted(fm, f)

                    // ONLY hide on Splash Screen. Loading Screen will now show the Nav Bar!
                    if (f is SplashFragment) {
                        bottomNav.visibility = View.GONE
                        fabCenter.visibility = View.GONE
                    } else {
                        bottomNav.visibility = View.VISIBLE
                        fabCenter.visibility = View.VISIBLE

                        updateFab(f)
                        updateIconTint(f)
                    }
                }
            }, true
        )

        // Hide navigation UI initially during splash
        bottomNav.visibility = View.GONE
        fabCenter.visibility = View.GONE

        bottomNav.selectedItemId = R.id.nav_home

        setupFeedbackLogic(bottomNav)

        if (savedInstanceState == null) {
            // Start with SplashFragment instead of Home
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SplashFragment())
                .commit()
        }

        handleIncomingIntent(intent)

        // FAB → Camera Activity
        fabCenter.setOnClickListener {

            if (!canNavigate()) {
                return@setOnClickListener
            }

            isCameraFlowActive = true

            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
        }

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->

            if (!canNavigate()) {
                return@setOnItemSelectedListener false
            }

            val fragment = when (item.itemId) {
                R.id.nav_home -> NewHomeFragment()
                R.id.nav_bookmark -> BookmarkFragment()
                R.id.nav_faq -> FaqFragment()
                R.id.nav_feedback -> feedbackFragment()
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

    // Called by SplashFragment when loading finishes
    fun onSplashFinished() {
        switchFragment(NewHomeFragment())
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        supportFragmentManager.executePendingTransactions()
    }

    fun showLoadingFragment(filePath: String) {
        val fragment = LoadingFragment().apply {
            arguments = Bundle().apply {
                putString("file_path", filePath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNow()
    }

    override fun onResume() {
        super.onResume()
        // We no longer need manual logic here! The Universal Listener handles it.
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
            current.showExitOverlay()
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
        if (!canNavigate()) return

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
            // 1. Recycle / Camera flow
            is LoadingFragment,
            is RecyclableresultFragment,
            is GuideListFragment,
            is WebViewFragment -> {
                bottomNav.menu.findItem(R.id.nav_recycle).isChecked = true
            }
            // 2. Bookmark Tab
            is BookmarkFragment -> {
                bottomNav.menu.findItem(R.id.nav_bookmark).isChecked = true
            }
            // 3. FAQ Tab
            is FaqFragment -> {
                bottomNav.menu.findItem(R.id.nav_faq).isChecked = true
            }
            // 4. Feedback Tab
            is feedbackFragment -> {
                bottomNav.menu.findItem(R.id.nav_feedback).isChecked = true
            }
            // 5. Default (Home)
            else -> {
                bottomNav.menu.findItem(R.id.nav_home).isChecked = true
            }
        }
    }

    companion object {
        var instance: MainActivity? = null
        var latestPredictionResponse: PredictResponse? = null
    }
}