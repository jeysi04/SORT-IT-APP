package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

        // Changes the subcategory text based on the analyzed subcategory
        when (subcategory) {
            //Glass
            "flatGlass" -> guideTitle.text = "Ways to Recycle Flat Glass"
            "glassBottles" -> guideTitle.text = "Ways to Recycle Glass Bottles"
            "cullets" -> guideTitle.text = "Ways to Recycle Cullet Glass"

            //Metal
            "aluminum_tin" -> guideTitle.text = "Ways to Recycle Aluminum Tin"
            "copper" -> guideTitle.text = "Ways to Recycle Copper"
            "steel" -> guideTitle.text = "Ways to Recycle Steel"

            //Paper
            "ONP" -> guideTitle.text = "Ways to Recycle Old Newspaper"
            "MP" -> guideTitle.text = "Ways to Recycle Mixed Paper"
            "OCC" -> guideTitle.text = "Ways to Recycle Old Corrugated Cartons"
            "SWL" -> guideTitle.text = "Ways to Recycle Selected White Ledger"
            "UBC" -> guideTitle.text = "Ways to Recycle Used Beverage Cartons"

            //Plastic
            "HDPE" -> guideTitle.text = "Ways to Recycle High-Density Polyethylene"
            "LDPE" -> guideTitle.text = "Ways to Recycle  Low-Density Polyethylene"
            "others" -> guideTitle.text = "Ways to Recycle Other Plastics"
            "PET" -> guideTitle.text = "Ways to Recycle Polyethylene Terephthalate"
            "PP" -> guideTitle.text = "Ways to Recycle Polypropylene"
            "PS" -> guideTitle.text = "Ways to Recycle Polystyrene"
            "PVC" -> guideTitle.text = "Ways to Recycle Polyvinyl Chloride"

            //Residuals
            "CDFP" -> guideTitle.text = "Ways to Recycle Clean and Dry Flexible Plastics"
            "leather" -> guideTitle.text = "Ways to Recycle Leather"
            "rubber" -> guideTitle.text = "Ways to Recycle Rubber"
            "textiles" -> guideTitle.text = "Ways to Recycle Textile"

            else -> guideTitle.text = "Ways to Recycle Unknown Type"
        }

        // Load guides from JSON
        val allGuides = loadGuidesFromAssets()
        val filteredList = allGuides.filter { it.category == subcategory }

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
