package com.example.sort_it_json

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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

        val openHome = intent.getBooleanExtra("open_home", false)

        fabCenter = findViewById(R.id.fab_center)
        bottomNav = findViewById(R.id.bottomNav)

        // --- UNIVERSAL FIX ---
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentStarted(fm: androidx.fragment.app.FragmentManager, f: Fragment) {
                    super.onFragmentStarted(fm, f)
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

        bottomNav.visibility = View.GONE
        fabCenter.visibility = View.GONE
        bottomNav.selectedItemId = R.id.nav_home

        setupFeedbackLogic(bottomNav)

        if (savedInstanceState == null) {
            if (openHome) {
                // FIXED: Direct to Home if coming from Privacy Policy
                onSplashFinished()
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SplashFragment())
                    .commit()
            }
        }

        handleIncomingIntent(intent)

        setupBottomNavListener()

        fabCenter.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            if (currentFragment is feedbackFragment && currentFragment.isFeedbackInProgress()) {
                currentFragment.showExitOverlay(R.id.nav_recycle)
                return@setOnClickListener
            }

            if (currentFragment is LoadingFragment) {
                currentFragment.pauseProcessing()
                showCancelLoadingDialog(R.id.nav_recycle)
                return@setOnClickListener
            }

            if (currentFragment is RecyclableresultFragment || currentFragment is GuideListFragment) {
                showExitResultDialog(R.id.nav_recycle)
                return@setOnClickListener
            }

            isCameraFlowActive = true
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
        }
    }

    private fun setupBottomNavListener() {
        bottomNav.setOnItemSelectedListener { item ->
            if (item.itemId == bottomNav.selectedItemId) {
                return@setOnItemSelectedListener true
            }

            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            if (currentFragment is feedbackFragment && currentFragment.isFeedbackInProgress()) {
                currentFragment.showExitOverlay(item.itemId)
                return@setOnItemSelectedListener false
            }

            if (currentFragment is LoadingFragment) {
                currentFragment.pauseProcessing()
                showCancelLoadingDialog(item.itemId)
                return@setOnItemSelectedListener false
            }

            if (currentFragment is RecyclableresultFragment || currentFragment is GuideListFragment) {
                showExitResultDialog(item.itemId)
                return@setOnItemSelectedListener false
            }

            performNavigation(item.itemId)
            true
        }
    }

    private fun performNavigation(itemId: Int) {
        val fragment = when (itemId) {
            R.id.nav_home -> NewHomeFragment()
            R.id.nav_bookmark -> BookmarkFragment()
            R.id.nav_faq -> FaqFragment()
            R.id.nav_feedback -> feedbackFragment()
            else -> null
        }

        if (fragment != null) {
            switchFragment(fragment)
        }
    }

    private fun showCancelLoadingDialog(targetNavId: Int) {
        val title = SpannableString("Cancel Process?").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#467750")), 0, length, 0)
        }

        val message = SpannableString("Are you sure you want to cancel the process? Your progress will be lost.").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                if (targetNavId == R.id.nav_recycle) {
                    isCameraFlowActive = true
                    val intent = Intent(this, CameraActivity::class.java)
                    cameraLauncher.launch(intent)
                } else {
                    performNavigation(targetNavId)
                    bottomNav.setOnItemSelectedListener(null)
                    bottomNav.selectedItemId = targetNavId
                    setupBottomNavListener()
                }
            }
            .setNegativeButton("No") { _, _ ->
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                (currentFragment as? LoadingFragment)?.resumeProcessing()
            }
            .setOnCancelListener {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                (currentFragment as? LoadingFragment)?.resumeProcessing()
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    private fun showExitResultDialog(targetNavId: Int) {
        val title = SpannableString("Exit Page").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#467750")), 0, length, 0)
        }

        val message = SpannableString("Are you sure you want to exit? Your progress will be lost.").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                if (targetNavId == R.id.nav_recycle) {
                    isCameraFlowActive = true
                    val intent = Intent(this, CameraActivity::class.java)
                    cameraLauncher.launch(intent)
                } else {
                    performNavigation(targetNavId)
                    bottomNav.setOnItemSelectedListener(null)
                    bottomNav.selectedItemId = targetNavId
                    setupBottomNavListener()
                }
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    fun onSplashFinished() {
        switchFragment(NewHomeFragment())
        bottomNav.visibility = View.VISIBLE
        fabCenter.visibility = View.VISIBLE
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
        supportFragmentManager.executePendingTransactions()
    }

    fun showLoadingFragment(filePath: String) {
        supportFragmentManager.popBackStack(
            null,
            androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        val fragment = LoadingFragment().apply {
            arguments = Bundle().apply {
                putString("file_path", filePath)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent) {
        val filePath = intent.getStringExtra("file_path")
        val goHome = intent.getBooleanExtra("go_home", false)

        if (filePath != null) {
            showLoadingFragment(filePath)
        } else if (goHome) {
            setNav(R.id.nav_home)
        }

        intent.removeExtra("file_path")
        intent.removeExtra("go_home")
    }

    private fun setupFeedbackLogic(bottomNav: BottomNavigationView) {
        val closeGlobalButton = findViewById<MaterialButton>(R.id.close_global_success_button)
        closeGlobalButton?.setOnClickListener {
            hideSuccessOverlay()
            setNav(R.id.nav_home)
        }
    }

    fun showSuccessOverlay() {
        findViewById<View>(R.id.global_success_overlay)?.visibility = View.VISIBLE
    }

    fun hideSuccessOverlay() {
        findViewById<View>(R.id.global_success_overlay)?.visibility = View.GONE
    }

    fun setNav(itemId: Int) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (itemId == bottomNav.selectedItemId && itemId != R.id.nav_recycle) return

        if (currentFragment is feedbackFragment && currentFragment.isFeedbackInProgress()) {
            currentFragment.showExitOverlay(itemId)
            return
        }

        if (currentFragment is LoadingFragment) {
            currentFragment.pauseProcessing()
            showCancelLoadingDialog(itemId)
            return
        }

        if (currentFragment is RecyclableresultFragment || currentFragment is GuideListFragment) {
            showExitResultDialog(itemId)
            return
        }

        if (itemId == R.id.nav_recycle) {
            isCameraFlowActive = true
            val intent = Intent(this, CameraActivity::class.java)
            cameraLauncher.launch(intent)
            return
        }

        if (itemId == R.id.nav_home) {
            supportFragmentManager.popBackStack(
                null,
                androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        }

        bottomNav.setOnItemSelectedListener(null)
        bottomNav.selectedItemId = itemId
        setupBottomNavListener()
        performNavigation(itemId)
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
        when (fragment) {
            is LoadingFragment,
            is RecyclableresultFragment,
            is GuideListFragment,
            is WebViewFragment -> bottomNav.menu.findItem(R.id.nav_recycle).isChecked = true
            is BookmarkFragment -> bottomNav.menu.findItem(R.id.nav_bookmark).isChecked = true
            is FaqFragment -> bottomNav.menu.findItem(R.id.nav_faq).isChecked = true
            is feedbackFragment -> bottomNav.menu.findItem(R.id.nav_feedback).isChecked = true
            else -> bottomNav.menu.findItem(R.id.nav_home).isChecked = true
        }
    }

    companion object {
        var instance: MainActivity? = null
        var latestPredictionResponse: PredictResponse? = null
    }
}
