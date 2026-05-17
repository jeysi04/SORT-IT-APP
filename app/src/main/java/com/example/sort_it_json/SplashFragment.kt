package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.splashProgressBar)

        // Animate progress bar from 0 to 100
        lifecycleScope.launch {
            var progress = 0
            while (progress <= 100) {
                progressBar.progress = progress

                // ADJUSTED FOR SLOWER, SMOOTHER SPEED
                progress += 1 // Increment by 1 instead of 2
                delay(30)     // 100 steps * 30ms = 3 seconds total loading time
            }

            // After loading is done, switch to Home
            (activity as? MainActivity)?.onSplashFinished()
        }
    }
}