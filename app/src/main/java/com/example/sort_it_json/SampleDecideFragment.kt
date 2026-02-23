package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment


class SampleDecideFragment : Fragment() {
    private var selectedCategory: String? = null
    private var selectedSubcategory: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Reference to the subcategory fragment (The partnered xml file)
        val view = inflater.inflate(R.layout.fragment_sample_decide, container, false)

        // CATEGORY buttons
        listOf(
            view.findViewById<Button>(R.id.Plastic),
            view.findViewById<Button>(R.id.Glass),
            view.findViewById<Button>(R.id.Paper),
            view.findViewById<Button>(R.id.Metal),
            view.findViewById<Button>(R.id.Residual),
            view.findViewById<Button>(R.id.NonRec),
        ).forEach { button ->
            button.setOnClickListener {
                selectedCategory = button.tag.toString()
            }
        }

        // SUBCATEGORY buttons
        listOf(
            view.findViewById<Button>(R.id.glassBottles),
            view.findViewById<Button>(R.id.flatGlass),
            view.findViewById<Button>(R.id.cullets),
            view.findViewById<Button>(R.id.aluminum_tin),
            view.findViewById<Button>(R.id.copper),
            view.findViewById<Button>(R.id.steel),
            view.findViewById<Button>(R.id.ONP),
            view.findViewById<Button>(R.id.MP),
            view.findViewById<Button>(R.id.OCC),
            view.findViewById<Button>(R.id.SWL),
            view.findViewById<Button>(R.id.UBC),
            view.findViewById<Button>(R.id.HDPE),
            view.findViewById<Button>(R.id.LDPE),
            view.findViewById<Button>(R.id.others),
            view.findViewById<Button>(R.id.PET),
            view.findViewById<Button>(R.id.PP),
            view.findViewById<Button>(R.id.PS),
            view.findViewById<Button>(R.id.PVC),
            view.findViewById<Button>(R.id.CDFP),
            view.findViewById<Button>(R.id.leather),
            view.findViewById<Button>(R.id.rubber),
            view.findViewById<Button>(R.id.textiles)
        ).forEach { button ->
            button.setOnClickListener {
                selectedSubcategory = button.tag.toString()
            }
        }

        // NEXT button
        view.findViewById<Button>(R.id.btnNext)?.setOnClickListener {

            // Declare the bundle variable
            val bundle: Bundle

            // Assign it conditionally
            bundle = if (selectedCategory == "NonRec") {
                Bundle().apply {
                    putString("category", selectedCategory)
                }
            } else {
                Bundle().apply {
                    putString("category", selectedCategory)
                    putString("subcategory", selectedSubcategory)
                }
            }


            val fragment = RecyclableresultFragment()
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun openGuideList(category: String) {
        val fragment = GuideListFragment().apply {
            arguments = Bundle().apply {
                putString("category", selectedCategory)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}