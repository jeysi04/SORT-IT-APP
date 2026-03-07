package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import android.widget.Button

class WebViewFragment : Fragment() {

    private var htmlFile: String? = null

    private var hasShownDialog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        htmlFile = arguments?.getString("html_file")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)

        // Find the WebView inside the inflated layout
        val webView = view.findViewById<WebView>(R.id.webView)

        // Hide WebView first (important)
        webView.visibility = View.INVISIBLE

        // Set a WebViewClient to handle page loading inside the WebView
        webView.webViewClient = object : WebViewClient() {

            // Called when the web page has finished loading
            override fun onPageFinished(view: WebView?, url: String?) {

                // Make the WebView visible now that the page is fully loaded
                webView.visibility = View.VISIBLE
            }
        }

        // Basic WebView settings
        webView.settings.apply {
            javaScriptEnabled = true        // For Finish Button
            domStorageEnabled = true         // required for HTML5 local storage
            allowFileAccess = true           // allows access to assets
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        webView.addJavascriptInterface(WebAppInterface(), "Android")

        // Disable system forced dark mode
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_OFF)
        }

        // WebViewClient to show content after load
        webView.webViewClient = object : WebViewClient() {

            // Called when a web page has finished loading
            override fun onPageFinished(view: WebView?, url: String?) {
                // Make the WebView visible only after the page fully loads
                webView.visibility = View.VISIBLE
            }
        }

        // Hide the WebView initially to avoid showing a blank or partially loaded page
        webView.visibility = View.INVISIBLE

        // Load HTML from assets directly
        htmlFile?.let { fileName ->
            webView.loadUrl("file:///android_asset/$fileName")
        }

        return view
    }

    // Called when the fragment’s view is being destroyed
    override fun onDestroyView() {

        // Always call the superclass method first
        super.onDestroyView()

        // Find the WebView inside the fragment’s view (if it exists)
        view?.findViewById<WebView>(R.id.webView)?.apply {

            // Stop any ongoing page loading to free resources
            stopLoading()

            // Remove all child views from the WebView
            // Prevents memory leaks caused by attached views
            removeAllViews()

            // Destroy the WebView completely
            destroy()
        }
    }

    inner class WebAppInterface {

        @android.webkit.JavascriptInterface
        fun goToNextFragment() {

            requireActivity().runOnUiThread {

                // Show feedback dialog first
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("We'd Love Your Feedback")
                    .setMessage("Would you like to share your experience with us?")
                    .setPositiveButton("Give Feedback") { _, _ ->

                        // Navigate to feedback fragment
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, feedbackFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    .setNegativeButton("Maybe Later") { _, _ ->

                        // Navigate to Home Fragment
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                    .show()
            }
        }
    }
}
