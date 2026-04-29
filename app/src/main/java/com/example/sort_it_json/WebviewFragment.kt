package com.example.sort_it_json

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

class WebViewFragment : Fragment() {

    private var htmlFile: String? = null
    private var webView: WebView? = null

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

        webView = view.findViewById(R.id.webView)

        setupWebView()
        loadHtml()

        return view
    }

    private fun setupWebView() {
        webView?.apply {

            visibility = View.INVISIBLE

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
            }

            // Disable dark mode override
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(
                    settings,
                    WebSettingsCompat.FORCE_DARK_OFF
                )
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    visibility = View.VISIBLE
                }
            }

            addJavascriptInterface(WebAppInterface(), "Android")
        }
    }

    private fun loadHtml() {
        htmlFile?.let {
            webView?.loadUrl("file:///android_asset/$it")
        }
    }

    override fun onDestroyView() {
        webView?.apply {
            stopLoading()
            removeAllViews()
            destroy()
        }
        webView = null
        super.onDestroyView()
    }

    inner class WebAppInterface {

        @android.webkit.JavascriptInterface
        fun goToNextFragment() {

            requireActivity().runOnUiThread {

                val title = SpannableString("We'd Love Your Feedback").apply {
                    setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.black, null)),
                        0, length, 0
                    )
                }

                val message = SpannableString("Would you like to share your experience with us?").apply {
                    setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.black, null)),
                        0, length, 0
                    )
                }

                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Give Feedback") { _, _ ->

                        view?.post {
                            (requireActivity() as MainActivity)
                                .setNav(R.id.nav_feedback)
                        }

                        if (isAdded) {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, feedbackFragment())
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    .setNegativeButton("Maybe Later", null)
                    .create()

                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.darkgreen, null))

                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.black, null))

                dialog.window?.setBackgroundDrawableResource(R.color.white)
            }
        }
    }

}