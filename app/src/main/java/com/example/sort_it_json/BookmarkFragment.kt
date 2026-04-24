package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewBookmarked)

        //val allGuides = DataStore.guides  // your data source

        // FILTER BOOKMARKED ITEMS
        //val bookmarkedList = allGuides.filter { it.isBookmarked }

        //recyclerView.layoutManager = LinearLayoutManager(requireContext())
        //recyclerView.adapter = GuideAdapter(bookmarkedList) { item ->
            // handle click if needed
        }
}