package com.example.sort_it_json

import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog


class RecyclableresultFragment : Fragment() {

    private var subcategory: String? = null
    private var hasShownDialog = false
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
        val illustbg = view.findViewById<ImageView>(R.id.illustration)
        val illustclass = view.findViewById<ImageView>(R.id.illust_classification)
        val questbot = view.findViewById<TextView>(R.id.questionText)
        val buttonNo = view.findViewById<TextView>(R.id.buttonNo)

        // Changes the subcategory text based on the analyzed subcategory
        when (category) {
            "Metal" -> {
                catText.text = "It's metal!"
                illustclass.setImageResource(R.drawable.metal_illus)
            }
            "Paper" -> {
                catText.text = "It's paper!"
                illustclass.setImageResource(R.drawable.paper_illus)
            }
            "Plastic" -> {
                catText.text = "It's plastic!"
                illustclass.setImageResource(R.drawable.plastic_illus)
            }
            "Glass" -> {
                catText.text = "It's glass!"
                illustclass.setImageResource(R.drawable.glass_illus)
            }
            "Residual" -> {
                catText.text = "It's residual!"
                illustclass.setImageResource(R.drawable.residual_illus)
            }
        }


        if (category == "NonRec"){
            classText.text = "Your waste is non-recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#AA0000"))
            catText.visibility = View.GONE
            typeText.visibility = View.GONE
            illustbg.setImageResource(R.drawable.nonrec_illus)
            illustclass.setImageResource(R.drawable.nonrecyclable)
            questbot.text = "Would you like to leave a feedback?"

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
            "LDPE" -> typeText.text = "Type: Low-Density Polyethylene (LDPE)"
            "others" -> typeText.text = "Type: Others"
            "PET" -> typeText.text = "Type: Polyethylene Terephthalate (PET)"
            "PP" -> typeText.text = "Type: Polypropylene (PP)"
            "PS" -> typeText.text = "Type: Polystyrene (PS)"
            "PVC" -> typeText.text = "Type: Polyvinyl Chloride (PVC)"

            //Residuals
            "CDFP" -> typeText.text = "Type: Clean and Dry Flexible Plastics"
            "leather" -> typeText.text = "Type: Leather"
            "rubber" -> typeText.text = "Type: Rubber"
            "textiles" -> typeText.text = "Type: Textiles"

            else -> typeText.text = "Unknown type"
        }

        if(category == "NonRec"){
            // ✅ Navigate directly to FeedbackFragment on Yes
            btnYes.setOnClickListener {
                val fragment = feedbackFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
        else {
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
        }

        //If button no is click, go back to home
        btnNo.setOnClickListener {
            val fragment = HomeFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showFeedbackDialog {
                    isEnabled = false
                    hasShownDialog = true
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        buttonNo.setOnClickListener {
            showFeedbackDialog()
            hasShownDialog = true

            val fragment = SampleDecideFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun showFeedbackDialog(onLeave: (() -> Unit)? = null) {

        AlertDialog.Builder(requireContext())
            .setTitle("We'd Love Your Feedback")
            .setMessage("Would you like to share your experience with us?")
            .setPositiveButton("Give Feedback") { _, _ ->
                // Navigate to feedback screen here
            }
            .setNegativeButton("Maybe Later") { _, _ ->
                onLeave?.invoke()
            }
            .show()
    }
}