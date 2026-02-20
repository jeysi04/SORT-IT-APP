package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GuideListFragment : Fragment() {

    private var selectedSubcategory: String = "glassBottles"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get the selected subcategory from arguments
        selectedSubcategory = arguments?.getString("subcategory") ?: "glassBottles"

        // 'fragment_guide_list' should contain the RecyclerView
        val view = inflater.inflate(R.layout.fragment_guide_list, container, false)

        // Find the RecyclerView in the layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // Set the LayoutManager for the RecyclerView
        // LinearLayoutManager arranges items in a vertical scrolling list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load guides from JSON
        val allGuides = loadGuidesFromAssets()
        val filteredList = allGuides.filter { it.category == selectedSubcategory }

        // Set adapter
        recyclerView.adapter = GuideAdapter(filteredList) { guideItem ->
            openGuide(guideItem.html_file)
        }

        return view
    }

    // Function to open WebViewFragment
    private fun openGuide(htmlFile: String) {

        // Create a new instance of WebViewFragment
        // .apply { } lets us configure the fragment immediately after creating it
        val fragment = WebViewFragment().apply {
            // Create a Bundle object to pass data to the fragment
            arguments = Bundle().apply {

                // Store the HTML file name inside the Bundle
                // "html_file" is the key used to retrieve the value later
                // htmlFile is the actual string value passed to this function
                putString("html_file", htmlFile)
            }
        }

        // Start a fragment transaction using the parent FragmentManager
        parentFragmentManager.beginTransaction()
            // Replace the current fragment inside fragment_container
            // with the new WebViewFragment instance
            .replace(R.id.fragment_container, fragment)
            // This allows the user to press the back button
            .addToBackStack(null)
            .commit()
    }

    // Function that loads guide data from a JSON file inside the assets folder
// It returns a List of GuideItem objects
    private fun loadGuidesFromAssets(fileName: String = "guides.json"): List<GuideItem> {
        // Open the file from the app's assets folder
        // requireContext() gets the current Fragment's context
        // assets.open(fileName) opens the specified file
        val jsonString = requireContext().assets.open(fileName)
            .bufferedReader()

            // use { } automatically closes the reader after use (prevents memory leaks)
            // it.readText() reads the entire JSON file as a String
            .use { it.readText() }

        // Create a TypeToken to tell Gson the exact type we want to convert into
        // This is needed because List<GuideItem> is a generic type
        val listType = object : TypeToken<List<GuideItem>>() {}.type

        // Use Gson to convert (deserialize) the JSON string
        // into a List<GuideItem> object using the specified type
        return Gson().fromJson(jsonString, listType)
    }
}
