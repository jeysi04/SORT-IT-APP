package com.example.sort_it_json

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var fabCenter: FloatingActionButton
    private var isCameraFlowActive = false
    private val cameraFlowFragments = setOf(
        LoadingFragment::class,
        RecyclableresultFragment::class,
        GuideListFragment::class,
        WebViewFragment::class
    )
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {

            val filePath = result.data?.getStringExtra("file_path")

            if (filePath != null) {

                isCameraFlowActive = true

                showLoadingFragment(filePath)
            }

        } else {

            // User backed out of camera
            isCameraFlowActive = false

            val current =
                supportFragmentManager.findFragmentById(R.id.fragment_container)

            if (current != null) {
                updateFab(current)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        fabCenter = findViewById<FloatingActionButton>(R.id.fab_center)

        bottomNav.selectedItemId = R.id.nav_home

        setupFeedbackLogic(bottomNav)

        // Load default fragment
        if (savedInstanceState == null) {
            switchFragment(NewHomeFragment())
        }

        handleIncomingIntent(intent)

        // FAB → Camera Activity
        fabCenter.setOnClickListener {

            // ADDED CHECK: Prevent opening camera if feedback is in progress
            if (!canNavigate()) {
                return@setOnClickListener
            }

            isCameraFlowActive = true

            enterNonNavState()
            changeFabTint(android.graphics.Color.parseColor("#F0CD6E"))

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

            // ADDED CHECK: Harangin agad ang pag-click sa nav bar kung may type sa feedback
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


    // REPLACE FRAGMENT (DESTROYS OLD ONE)
    private fun switchFragment(fragment: Fragment) {

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNav)

        // restore normal navigation behavior
        bottomNav.menu.setGroupCheckable(0, true, true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateFab(fragment)
    }

    // Show loading screen with file
    fun showLoadingFragment(filePath: String) {

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNav)

        // clear highlight immediately
        //bottomNav.menu.setGroupCheckable(0, true, false)

        val fragment = LoadingFragment().apply {
            arguments = Bundle().apply {
                putString("file_path", filePath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        updateFab(fragment)
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

    // UPDATED: Ngayon ay tinatawag na niya ang showCustomToast galing sa feedbackFragment
    private fun canNavigate(): Boolean {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current is feedbackFragment && current.isFeedbackInProgress()) {
            current.showCustomToast("Please finish or send your feedback before leaving.")
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

    fun enterNonNavState() {

        val bottomNav =
            findViewById<BottomNavigationView>(R.id.bottomNav)

        // Clear ALL selections safely
        bottomNav.menu.setGroupCheckable(0, true, false)

        bottomNav.menu.findItem(R.id.nav_home).isChecked = false
        bottomNav.menu.findItem(R.id.nav_bookmark).isChecked = false
        bottomNav.menu.findItem(R.id.nav_faq).isChecked = false
        bottomNav.menu.findItem(R.id.nav_feedback).isChecked = false
    }

    fun setNav(itemId: Int) {
        // ADDED CHECK: Para sure na blocked din kapag programmatic ang pag-set ng nav
        if (!canNavigate()) return

        findViewById<BottomNavigationView>(R.id.bottomNav)
            .selectedItemId = itemId
    }

    fun changeFabTint(color: Int) {
        fabCenter.imageTintList = ColorStateList.valueOf(color)
    }

    fun updateFab(fragment: Fragment) {

        val isCameraFlow = cameraFlowFragments.any {
            it.isInstance(fragment)
        }

        val color = if (isCameraFlow) {
            Color.parseColor("#F0CD6E") // yellow
        } else {
            Color.parseColor("#FFFFFF") // default
        }

        changeFabTint(color)
    }
}