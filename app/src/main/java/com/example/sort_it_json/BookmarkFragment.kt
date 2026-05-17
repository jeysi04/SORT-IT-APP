package com.example.sort_it_json

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookmarkFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout for bookmark screen
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener { (activity as? MainActivity)?.setNav(R.id.nav_home) }

        recyclerView = view.findViewById(R.id.recyclerViewBookmarked)

        // Load all guides and show only bookmarked ones
        loadBookmarks()
    }

    // =========================
    // LOAD BOOKMARKS
    // =========================
    private fun loadBookmarks() {

        val rv = recyclerView ?: return

        rv.layoutManager = LinearLayoutManager(requireContext())

        val allGuides = loadGuidesFromAssets()

        val prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        val bookmarkedTitles =
            prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()

        allGuides.forEach {
            it.isBookmarked = bookmarkedTitles.contains(it.title)
        }

        val bookmarkedList = allGuides.filter { it.isBookmarked }

        rv.adapter = GuideAdapter(
            guides = bookmarkedList,
            onClick = { item ->

                val fragment = WebViewFragment().apply {
                    arguments = Bundle().apply {
                        putString("html_file", item.html_file)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            },

            onBookmarkChanged = { item, wasRemoved ->

                if (wasRemoved) {

                    // 1. UPDATE UI IMMEDIATELY (THIS IS THE IMPORTANT PART)
                    refreshBookmarks()

                    val snackbar = Snackbar.make(
                        recyclerView!!,
                        "Removed from bookmarks",
                        Snackbar.LENGTH_LONG
                    )

                    val snackbarView = snackbar.view

                    snackbarView.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.snackbar_bg)

                    val textView =
                        snackbarView.findViewById<TextView>(
                            com.google.android.material.R.id.snackbar_text
                        )

                    textView.setTextColor(Color.parseColor("#000000")) // Sets text to Black
                    val customFont = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular) // Sets to Montserrat Regular
                    textView.typeface = customFont

                    // Style the UNDO button to be BOLD and Montserrat
                    val actionTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                    val boldFont = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)
                    actionTextView.typeface = boldFont
                    actionTextView.setAllCaps(false)

                    snackbar.setAction("UNDO") {

                        item.isBookmarked = true

                        val prefs =
                            requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

                        val bookmarkedTitles =
                            prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet()
                                ?: mutableSetOf()

                        bookmarkedTitles.add(item.title)

                        prefs.edit()
                            .putStringSet("bookmark_titles", bookmarkedTitles)
                            .apply()

                        // 2. REFRESH AGAIN AFTER UNDO
                        refreshBookmarks()
                    }

                    snackbar.setActionTextColor(Color.parseColor("#467750"))

                    snackbar.show()
                }
            }
        )
    }

    // =========================
    // LOAD JSON DATA
    // =========================
    private fun loadGuidesFromAssets(
        fileName: String = "guides.json"
    ): List<GuideItem> {

        // Read JSON file from assets
        val jsonString =
            requireContext().assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }

        // Convert JSON to list
        val listType =
            object : TypeToken<List<GuideItem>>() {}.type

        return Gson().fromJson(jsonString, listType)
    }

    private fun refreshBookmarks() {
        if (!isAdded) return
        loadBookmarks()
    }
}
