package com.example.sort_it_json

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog

class RecyclableresultFragment : Fragment() {

    private var predictResponse: PredictResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        predictResponse = arguments?.getParcelable("predict_response")
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

        val response = predictResponse
        if (response == null) {
            classText.text = "Error: No prediction result available"
            return view
        }

        val classification = response.stage1.label
        val category = response.stage2?.label ?: "N/A"
        val subcategory = response.stage3?.label ?: "N/A"

        // Set category and illustration
        when (category) {
            "Metal" -> { catText.text = "It's metal!"; illustclass.setImageResource(R.drawable.metal_illus) }
            "Paper" -> { catText.text = "It's paper!"; illustclass.setImageResource(R.drawable.paper_illus) }
            "Plastic" -> { catText.text = "It's plastic!"; illustclass.setImageResource(R.drawable.plastic_illus) }
            "Glass" -> { catText.text = "It's glass!"; illustclass.setImageResource(R.drawable.glass_illus) }
            "Residual" -> { catText.text = "It's residual!"; illustclass.setImageResource(R.drawable.residual_illus) }
            else -> { catText.text = "Unknown category" }
        }

        if (classification == "non_recyclable") {
            classText.text = "Your waste is non-recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#AA0000"))
            catText.text = "Please dispose this item in the general waste bin."
            typeText.visibility = View.GONE
            illustbg.setImageResource(R.drawable.nonrec_illus)
            illustclass.setImageResource(R.drawable.nonrecyclable)
            questbot.text = "Would you like to leave feedback?"

            val dpHeight = 537
            val scale = resources.displayMetrics.density
            layoutwhitebg.layoutParams.height = (dpHeight * scale).toInt()
        } else {
            typeText.visibility = View.VISIBLE
            typeText.text = mapSubcategoryToText(subcategory)
        }

        btnYes.setOnClickListener {
            val fragment = if (classification == "non_recyclable") {
                feedbackFragment()
            } else {
                GuideListFragment().apply {
                    arguments = Bundle().apply { putString("subcategory", subcategory) }
                }
            }
            if (isAdded) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        btnNo.setOnClickListener {
            showFeedbackDialog()
            if (isAdded) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        return view
    }

    private fun mapSubcategoryToText(subcategory: String?): String {
        return when (subcategory) {
            "flatGlass" -> "Type: Flat Glass"
            "glassBottles" -> "Type: Glass Bottle"
            "cullets" -> "Type: Cullet"
            "aluminum_tin" -> "Type: Aluminum Tin"
            "copper" -> "Type: Copper"
            "steel" -> "Type: Steel"
            "ONP" -> "Type: Old Newspaper"
            "MP" -> "Type: Mixed Paper"
            "OCC" -> "Type: Old Corrugated Cartons (OCC)"
            "SWL" -> "Type: Selected White Ledger (SWL)"
            "UBC" -> "Type: Used Beverage Cartons (UBC)"
            "HDPE" -> "Type: High-Density Polyethylene (HDPE)"
            "LDPE" -> "Type: Low-Density Polyethylene (LDPE)"
            "others" -> "Type: Others"
            "PET" -> "Type: Polyethylene Terephthalate (PET)"
            "PP" -> "Type: Polypropylene (PP)"
            "PS" -> "Type: Polystyrene (PS)"
            "PVC" -> "Type: Polyvinyl Chloride (PVC)"
            "CDFP" -> "Type: Clean and Dry Flexible Plastics"
            "leather" -> "Type: Leather"
            "rubber" -> "Type: Rubber"
            "textiles" -> "Type: Textiles"
            else -> "Unknown type"
        }
    }

    private fun showFeedbackDialog() {
        val title = SpannableString("We'd Love Your Feedback").apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.black, null)), 0, length, 0)
        }
        val message = SpannableString("Would you like to share your experience with us?").apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.black, null)), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Give Feedback") { _, _ ->
                val fragment = feedbackFragment()
                if (isAdded) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            .setNegativeButton("Maybe Later") { _, _ -> }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.darkgreen, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black, null))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }
}