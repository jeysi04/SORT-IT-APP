package com.example.sort_it_json

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class BookmarkFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            (activity as? MainActivity)?.setNav(R.id.nav_home)
        }

        loadBookmarks(view)

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.recyclerViewBookmarked)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        val allGuides = loadGuidesFromAssets()

        val prefs = requireContext().getSharedPreferences(
            "bookmarks",
            Context.MODE_PRIVATE
        )

        val bookmarkedTitles =
            prefs.getStringSet(
                "bookmark_titles",
                mutableSetOf()
            ) ?: mutableSetOf()

        allGuides.forEach {
            it.isBookmarked =
                bookmarkedTitles.contains(it.title)
        }

        val bookmarkedList =
            allGuides.filter { it.isBookmarked }

        recyclerView.adapter =
            GuideAdapter(bookmarkedList) { item ->

                refreshBookmarks()

                val fragment = WebViewFragment().apply {
                    arguments = Bundle().apply {
                        putString("html_file", item.html_file)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
    }

    private fun loadGuidesFromAssets(
        fileName: String = "guides.json"
    ): List<GuideItem> {

        val jsonString = requireContext()
            .assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }

        val listType =
            object : TypeToken<List<GuideItem>>() {}.type

        return Gson().fromJson(jsonString, listType)
    }

    private fun loadBookmarks(view: View) {

        val recyclerView =
            view.findViewById<RecyclerView>(R.id.recyclerViewBookmarked)

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        val allGuides = loadGuidesFromAssets()

        val prefs = requireContext().getSharedPreferences(
            "bookmarks",
            Context.MODE_PRIVATE
        )

        val bookmarkedTitles =
            prefs.getStringSet("bookmark_titles", mutableSetOf()) ?: mutableSetOf()

        allGuides.forEach {
            it.isBookmarked = bookmarkedTitles.contains(it.title)
        }

        val bookmarkedList =
            allGuides.filter { it.isBookmarked }

        recyclerView.adapter =
            GuideAdapter(
                bookmarkedList,
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
                }
            )
    }

    fun refreshBookmarks() {
        view?.let { loadBookmarks(it) }
    }
}