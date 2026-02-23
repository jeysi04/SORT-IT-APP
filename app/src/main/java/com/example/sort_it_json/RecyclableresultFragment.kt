package com.example.sort_it_json

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment


class RecyclableresultFragment : Fragment() {

    private var subcategory: String? = null
    private var category: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ✅ Read arguments at the correct lifecycle stage
        subcategory = arguments?.getString("subcategory")
        category = arguments?.getString("category")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_recyclableresult, container, false)
        val btnYes = view.findViewById<Button>(R.id.buttonYes)
        val btnNo = view.findViewById<Button>(R.id.buttonNo)
        val typeText = view.findViewById<TextView>(R.id.typeText)
        val catText = view.findViewById<TextView>(R.id.category)
        val classText = view.findViewById<TextView>(R.id.classification)
        val layoutwhitebg = view.findViewById<LinearLayout>(R.id.whitebg)

        // Changes the subcategory text based on the analyzed subcategory
        when (category) {
            "Metal" -> catText.text = "It's metal!"
            "Paper" -> catText.text = "It's paper!"
            "Plastic" -> catText.text = "It's plastic!"
            "Glass" -> catText.text = "It's glass!"
            "Residual" -> catText.text = "It's residual!"
        }


        if (category == "NonRec"){
            classText.text = "Your waste is non-recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#AA0000"))
            catText.visibility = View.GONE
            typeText.visibility = View.GONE

            val dpHeight = 537 // height in dp
            val scale = resources.displayMetrics.density
            val heightInPx = (dpHeight * scale).toInt()

            val params = layoutwhitebg.layoutParams
            params.height = heightInPx
            layoutwhitebg.layoutParams = params
        }

        // Changes the subcategory text based on the analyzed subcategory
        when (subcategory) {
            //Glass
            "flatGlass" -> typeText.text = "Type: Flat Glass"
            "glassBottles" -> typeText.text = "Type: Glass Bottle"
            "cullets" -> typeText.text = "Type: Cullet"

            //Metal
            "aluminum_tin" -> typeText.text = "Type: Aluminum Tin"
            "copper" -> typeText.text = "Type: Copper"
            "steel" -> typeText.text = "Type: Steel"

            //Paper
            "ONP" -> typeText.text = "Type: Old Newspaper"
            "MP" -> typeText.text = "Type: Mixed Paper"
            "OCC" -> typeText.text = "Type: Old Corrugated Cartons (OCC)"
            "SWL" -> typeText.text = "Type: Selected White Ledger (SWL)"
            "UBC" -> typeText.text = "Type: Used Beverage Cartons (UBC)"

            //Plastic
            "HDPE" -> typeText.text = "Type: High-Density Polyethylene (HDPE)"
            "LDPE" -> typeText.text = "Type: Low-Density Polyethylene (HDPE)"
            "others" -> typeText.text = "Type: Other Plastics"
            "PET" -> typeText.text = "Type: Polyethylene Terephthalate (PET)"
            "PP" -> typeText.text = "Type: Polypropylene (PP)"
            "PS" -> typeText.text = "Type: Polystyrene (PS)"
            "PVC" -> typeText.text = "Type: Polyvinyl Chloride (PVC)"

            //Residuals
            "CDFP" -> typeText.text = "Type: Clean and Dry Flexible Plastics"
            "leather" -> typeText.text = "Type: Leather"
            "rubber" -> typeText.text = "Type: Rubber"
            "textiles" -> typeText.text = "Type: Textiles"

            else -> typeText.text = "Unknown glass type"
        }

        // ✅ Navigate directly to GuideListFragment on Yes
        btnYes.setOnClickListener {
            val fragment = GuideListFragment().apply {
                arguments = Bundle().apply {
                    putString("subcategory", subcategory)
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        //If button no is click, go back to home
        btnNo.setOnClickListener {
            val fragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}