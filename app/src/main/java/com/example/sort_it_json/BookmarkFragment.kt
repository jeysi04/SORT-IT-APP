package com.example.sort_it_json

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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

    // Variables for the empty states
    private var initialEmptyState: View? = null
    private var searchEmptyState: View? = null

    // Hold the full list of bookmarks and the current search term
    private var allBookmarkedGuides: List<GuideItem> = emptyList()
    private var currentSearchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        // Bind the empty state layouts from XML
        initialEmptyState = view.findViewById(R.id.initialEmptyStateLayout)
        searchEmptyState = view.findViewById(R.id.searchEmptyStateLayout)

        val searchEditText = view.findViewById<EditText>(R.id.searchEditText)
        val searchIcon = view.findViewById<ImageView>(R.id.searchIcon)

        // Load data initially
        loadBookmarks()

        // Setup Search Listener
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s?.toString()?.trim() ?: ""

                // "Lights up" the search icon when typing
                if (currentSearchQuery.isNotEmpty()) {
                    searchIcon.setColorFilter(Color.parseColor("#F0CD6E")) // Yellow
                } else {
                    searchIcon.setColorFilter(Color.parseColor("#919191")) // Grey
                }

                // Filter the list
                filterBookmarks()
            }
        })
    }

    // =========================
    // LOAD BOOKMARKS (From Storage)
    // =========================
    private fun loadBookmarks() {
        val allGuides = loadGuidesFromAssets()
        val prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        val bookmarkedTitles = prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        allGuides.forEach {
            it.isBookmarked = bookmarkedTitles.contains(it.title)
        }

        // Store the full list of currently bookmarked items
        allBookmarkedGuides = allGuides.filter { it.isBookmarked }

        // Apply any active search filter before updating UI
        filterBookmarks()
    }

    // =========================
    // FILTER AND UPDATE ADAPTER
    // =========================
    private fun filterBookmarks() {
        val filteredList = if (currentSearchQuery.isEmpty()) {
            allBookmarkedGuides
        } else {
            allBookmarkedGuides.filter {
                it.title.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // ==========================================
        // SMART EMPTY STATE LOGIC (UPDATED)
        // ==========================================
        if (currentSearchQuery.isNotEmpty() && filteredList.isEmpty()) {
            // Case 1: The user typed something, but there are no results.
            // We prioritize this! Even if they have 0 total bookmarks,
            // if they try to search, we show the search error.
            recyclerView?.visibility = View.GONE
            initialEmptyState?.visibility = View.GONE
            searchEmptyState?.visibility = View.VISIBLE

        } else if (allBookmarkedGuides.isEmpty()) {
            // Case 2: The search bar is empty, and they have 0 bookmarks saved.
            recyclerView?.visibility = View.GONE
            searchEmptyState?.visibility = View.GONE
            initialEmptyState?.visibility = View.VISIBLE

        } else {
            // Case 3: We have results! Show the list.
            recyclerView?.visibility = View.VISIBLE
            searchEmptyState?.visibility = View.GONE
            initialEmptyState?.visibility = View.GONE
        }

        // Update the adapter with the filtered list AND THE SEARCH QUERY
        recyclerView?.adapter = GuideAdapter(
            guides = filteredList,
            searchQuery = currentSearchQuery,
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
                    refreshBookmarks()

                    val snackbar = Snackbar.make(
                        requireView(),
                        "Removed from bookmarks",
                        Snackbar.LENGTH_LONG
                    )

                    val snackbarView = snackbar.view
                    snackbarView.background = ContextCompat.getDrawable(requireContext(), R.drawable.snackbar_bg)
                    snackbarView.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#F5F5F5"))

                    val textView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
                    textView.setTextColor(Color.parseColor("#000000"))
                    textView.typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)

                    val actionTextView = snackbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
                    actionTextView.typeface = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)
                    actionTextView.setAllCaps(false)

                    snackbar.setAction("UNDO") {
                        item.isBookmarked = true

                        val prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
                        val bookmarkedTitles = prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                        bookmarkedTitles.add(item.title)

                        prefs.edit()
                            .putStringSet("bookmark_titles", bookmarkedTitles)
                            .apply()

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
    private fun loadGuidesFromAssets(fileName: String = "guides.json"): List<GuideItem> {
        val jsonString = requireContext().assets.open(fileName).bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<GuideItem>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }

    private fun refreshBookmarks() {
        if (!isAdded) return
        loadBookmarks()
    }
}