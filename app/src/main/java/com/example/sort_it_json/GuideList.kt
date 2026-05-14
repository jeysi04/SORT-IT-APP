package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Context

class GuideListFragment : Fragment() {

    private var subcategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Get the selected subcategory from arguments
        subcategory = arguments?.getString("subcategory")

        // 'fragment_guide_list' should contain the RecyclerView
        val view = inflater.inflate(R.layout.fragment_guide_list, container, false)

        // Find the RecyclerView in the layout
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        // Set the LayoutManager for the RecyclerView
        // LinearLayoutManager arranges items in a vertical scrolling list
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Declare a variable to control Title of the page
        val guideTitle = view.findViewById<TextView>(R.id.titleText)

        //Declare back button
        val btnTopLeft = view.findViewById<ImageButton>(R.id.btnTopLeft)

        // Changes the subcategory text based on the analyzed subcategory
        when (subcategory) {
            //Glass
            "Flat Glass" -> guideTitle.text = "Ways to Recycle Flat Glass"
            "Glass Bottle" -> guideTitle.text = "Ways to Recycle Glass Bottles"
            "Glass Cullet" -> guideTitle.text = "Ways to Recycle Cullet Glass"

            //Metal
            "Aluminum_Tin" -> guideTitle.text = "Ways to Recycle Aluminum Tin"
            "Copper" -> guideTitle.text = "Ways to Recycle Copper"
            "Steel" -> guideTitle.text = "Ways to Recycle Steel"

            //Paper
            "Old Newspaper" -> guideTitle.text = "Ways to Recycle Old Newspaper"
            "Mixed Paper" -> guideTitle.text = "Ways to Recycle Mixed Paper"
            "Old Corrugated Cartons" -> guideTitle.text = "Ways to Recycle Old Corrugated Cartons"
            "Selected White Ledger" -> guideTitle.text = "Ways to Recycle Selected White Ledger"
            "Used Beverage Cartons" -> guideTitle.text = "Ways to Recycle Used Beverage Cartons"

            //Plastic
            "HDPE" -> guideTitle.text = "Ways to Recycle High-Density Polyethylene"
            "LDPE" -> guideTitle.text = "Ways to Recycle  Low-Density Polyethylene"
            "Other Plastic" -> guideTitle.text = "Ways to Recycle Other Plastics"
            "PET" -> guideTitle.text = "Ways to Recycle Polyethylene Terephthalate"
            "PP" -> guideTitle.text = "Ways to Recycle Polypropylene"
            "PS" -> guideTitle.text = "Ways to Recycle Polystyrene"
            "PVC" -> guideTitle.text = "Ways to Recycle Polyvinyl Chloride"

            //Residuals
            "Clean and Dry Flexible Plastic" -> guideTitle.text = "Ways to Recycle Clean and Dry Flexible Plastics"
            "Leather" -> guideTitle.text = "Ways to Recycle Leather"
            "Rubber" -> guideTitle.text = "Ways to Recycle Rubber"
            "Textiles" -> guideTitle.text = "Ways to Recycle Textile"

            else -> guideTitle.text = "Ways to Recycle Unknown Type"
        }

        // Load guides from JSON
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

        val filteredList =
            allGuides.filter {
                it.category == subcategory
            }

        // Set adapter
        recyclerView.adapter = GuideAdapter(filteredList) { guideItem ->
            openGuide(guideItem.html_file)
        }

        btnTopLeft.setOnClickListener {
            if (isAdded) {
                parentFragmentManager.popBackStack()
            }
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
        (activity as MainActivity).updateFab(fragment)
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
