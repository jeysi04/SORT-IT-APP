package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog



class RecyclableresultFragment : Fragment() {

    private var predictResponse: PredictResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Safe retrieval of Parcelable
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
        val btnTopLeft = view.findViewById<ImageButton>(R.id.btnTopLeft)

        val response = predictResponse
        if (response == null) {
            classText.text = "Error: No prediction result available"
            return view
        }


        val stage1Label = response.stage1.label
        val categoryLabel = response.stage2?.label ?: "Unknown"
        val subcategoryLabel = response.stage3?.label ?: "Unknown"

        //TEST
        //Toast.makeText(requireContext(), "Classification: ${stage1Label}", Toast.LENGTH_SHORT).show()

        if (stage1Label == "non_recyclable") {
            // Non-recyclable UI
            classText.text = "Your waste is non-recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#AA0000"))
            catText.text = "Please dispose this item in the general waste bin."
            typeText.visibility = View.GONE
            illustbg.setImageResource(R.drawable.nonrec_illus)
            illustclass.setImageResource(R.drawable.nonrecyclable)
            questbot.text = "Would you like to take a new picture?"

            val dpHeight = 537
            val scale = resources.displayMetrics.density
            layoutwhitebg.layoutParams.height = (dpHeight * scale).toInt()

            btnYes.setOnClickListener {

                parentFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                val intent = Intent(requireContext(), CameraActivity::class.java)
                startActivity(intent)

            }

        }

        if (stage1Label == "recyclable" && categoryLabel != "Unknown" && categoryLabel != "uncertain" && subcategoryLabel != "Unknown" && subcategoryLabel != "uncertain") {
            // Recyclable UI
            classText.text = "Your waste is recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#007700"))

            //TEST
            //Toast.makeText(requireContext(), "Category: ${categoryLabel}", Toast.LENGTH_SHORT).show()
            //Toast.makeText(requireContext(), "Subcategory: ${subcategoryLabel}", Toast.LENGTH_SHORT).show()

            catText.text = "It's $categoryLabel!"
            typeText.visibility = View.VISIBLE
            typeText.text = "Type: ${mapSubcategoryToText(subcategoryLabel)}"

            // Illustrations
            illustclass.setImageResource(
                when (categoryLabel) {

                    "metal" -> {
                        R.drawable.metal_illus
                    }

                    "paper" -> {
                        R.drawable.paper_illus
                    }

                    "plastic" -> {
                            R.drawable.plastic_illus
                    }

                    "glass" -> {
                        R.drawable.glass_illus
                    }

                    "residual" -> {
                        R.drawable.residual_illus
                    }

                    else -> {
                        R.drawable.unknown
                    }
                }
            )

            illustbg.setImageResource(R.drawable.rec_illus)
            questbot.text = "Would you like to see recycling guides?"

            btnYes.setOnClickListener {

                val fragment = GuideListFragment().apply {
                    arguments = Bundle().apply { putString("subcategory", subcategoryLabel) }
                }
                if (isAdded) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        if (categoryLabel == "Unknown" || categoryLabel == "uncertain" || subcategoryLabel == "Unknown")
        {
            // Unknown UI
            classText.text = "Your waste could not be identified!"
            classText.textSize = 27f
            classText.setTextColor(android.graphics.Color.parseColor("#D89B2B"))
            catText.text = "Please make sure the object is centered and clearly visible."
            typeText.visibility = View.GONE
            illustbg.setImageResource(R.drawable.unknown_illus)
            illustclass.setImageResource(R.drawable.unknown)
            questbot.text = "Would you like to take a new picture?"

            val dpHeight = 537
            val scale = resources.displayMetrics.density
            layoutwhitebg.layoutParams.height = (dpHeight * scale).toInt()

            btnYes.setOnClickListener {

                parentFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )

                val intent = Intent(requireContext(), CameraActivity::class.java)
                startActivity(intent)

            }
        }

        btnNo.setOnClickListener {
            (activity as? MainActivity)?.setNav(R.id.nav_home)
        }

        btnTopLeft.setOnClickListener {
            showExitDialog()
        }

        return view
    }

    private fun mapSubcategoryToText(subcategory: String): String {
        return when (subcategory) {
            "Flat Glass" -> "Flat Glass"
            "Glass Bottle" -> "Glass Bottle"
            "Glass Cullet" -> "Cullet"
            "Aluminum_Tin" -> "Aluminum Tin"
            "Copper" -> "Copper"
            "Steel" -> "Steel"
            "Old Newspaper" -> "Old Newspaper"
            "Mixed Paper" -> "Mixed Paper"
            "Old Corrugated Cartons" -> "Old Corrugated Cartons (OCC)"
            "Selected White Ledger" -> "Selected White Ledger (SWL)"
            "Used Beverage Cartons" -> "Used Beverage Cartons (UBC)"
            "HDPE" -> "High-Density Polyethylene (HDPE)"
            "LDPE" -> "Low-Density Polyethylene (LDPE)"
            "Other Plastic" -> "Others"
            "PET" -> "Polyethylene Terephthalate (PET)"
            "PP" -> "Polypropylene (PP)"
            "PS" -> "Polystyrene (PS)"
            "PVC" -> "Polyvinyl Chloride (PVC)"
            "Clean and Dry Flexible Plastic" -> "Clean and Dry Flexible Plastics"
            "Leather" -> "Leather"
            "Rubber" -> "Rubber"
            "Textiles" -> "Textiles"
            else -> "Unknown"
        }
    }

    private fun showFeedbackDialog() {
        val title = SpannableString("We'd Love Your Feedback").apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.darkgreen, null)), 0, length, 0)
        }
        val message = SpannableString("Would you like to share your experience with us?").apply {
            setSpan(ForegroundColorSpan(resources.getColor(R.color.black, null)), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Give Feedback") { _, _ ->

                view?.post {
                    (requireActivity() as MainActivity)
                        .setNav(R.id.nav_feedback)
                }

                if (isAdded) {

                    val fragment = feedbackFragment()

                    parentFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .hide(this@RecyclableresultFragment)
                        .addToBackStack("guide")
                        .commit()
                }
            }
            .setNegativeButton("Maybe Later") { _, _ ->
            }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.darkgreen, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black, null))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    private fun showExitDialog() {

        val title = SpannableString("Exit Result Page").apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.darkgreen, null)),
                0,
                length,
                0
            )
        }

        val message = SpannableString(
            "Are you sure you want to exit? Your progress will be lost."
        ).apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black, null)),
                0,
                length,
                0
            )
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                parentFragmentManager.popBackStack(
                    null,
                    androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                startActivity(Intent(requireContext(), CameraActivity::class.java))
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    override fun onResume() {
        super.onResume()
    }

}