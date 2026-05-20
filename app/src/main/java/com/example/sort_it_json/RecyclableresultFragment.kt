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
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView

class RecyclableresultFragment : Fragment() {

    private var predictResponse: PredictResponse? = null

    // NEW: A flag to track when the camera is opening
    private var isNavigatingToCamera = false

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

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    returnToCamera()
                }
            })

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
                returnToCamera()
            }

            btnTopLeft.setOnClickListener {
                returnToCamera()
            }

            return view
        }

        val stage1Label = response.stage1.label
        val categoryLabel = response.stage2?.label ?: "Unknown"
        val subcategoryLabel = response.stage3?.label ?: "Unknown"

        Toast.makeText(requireContext(), "Classification: ${stage1Label}", Toast.LENGTH_SHORT)
            .show()
        Toast.makeText(requireContext(), "Category: ${categoryLabel}", Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), "Subcategory: ${subcategoryLabel}", Toast.LENGTH_SHORT)
            .show()

        if (stage1Label == "non-recyclable") {

            classText.text = "Your waste is non-recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#AA0000"))

            catText.text = "Please dispose this item in the general waste bin."
            typeText.visibility = View.GONE

            illustbg.setImageResource(R.drawable.nonrec_illus)
            illustclass.setImageResource(R.drawable.nonrecyclable)

            questbot.text = "Would you like to take a new picture?"


            btnYes.setOnClickListener {
                returnToCamera()
            }

        } else if (
            stage1Label == "recyclable" &&
            categoryLabel != "Unknown" &&
            categoryLabel != "uncertain" &&
            subcategoryLabel != "Unknown" &&
            subcategoryLabel != "uncertain" &&
            categoryLabel != "null" &&
            subcategoryLabel != "null"
        ) {

            classText.text = "Your waste is recyclable!"
            classText.setTextColor(android.graphics.Color.parseColor("#007700"))

            catText.text = "It's $categoryLabel!"
            typeText.visibility = View.VISIBLE
            typeText.text = "Type: ${mapSubcategoryToText(subcategoryLabel)}"

            illustclass.setImageResource(
                when (categoryLabel) {
                    "metal" -> R.drawable.metal_illus
                    "paper" -> R.drawable.paper_illus
                    "plastic" -> R.drawable.plastic_illus
                    "glass" -> R.drawable.glass_illus
                    "residual" -> R.drawable.residual_illus
                    else -> R.drawable.unknown
                }
            )

            illustbg.setImageResource(R.drawable.rec_illus)
            questbot.text = "Would you like to see recycling guides?"

            btnYes.setOnClickListener {
                val fragment = GuideListFragment().apply {
                    arguments = Bundle().apply {
                        putString("subcategory", subcategoryLabel)
                    }
                }

                if (isAdded) {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }

        } else {

            classText.text = "Your waste could not be identified!"
            classText.textSize = 27f
            classText.setTextColor(android.graphics.Color.parseColor("#D89B2B"))

            catText.text =
                "Please make sure the object is centered and clearly visible."

            typeText.visibility = View.GONE

            illustbg.setImageResource(R.drawable.unknown_illus)
            illustclass.setImageResource(R.drawable.unknown)

            questbot.text = "Would you like to take a new picture?"
            

            btnYes.setOnClickListener {
                returnToCamera()
            }
        }

        return view
    }

    // ==========================================
    // RETURN TO CAMERA HELPER (FIXED FOR FLASHING)
    // ==========================================
    private fun returnToCamera() {
        // Set the flag so we know we are leaving for the camera
        isNavigatingToCamera = true

        // Launch the Camera immediately (NO visual flash!)
        val intent = Intent(requireContext(), CameraActivity::class.java)
        startActivity(intent)
    }

    // ==========================================
    // SILENT BACKGROUND SWAP
    // ==========================================
    override fun onStop() {
        super.onStop()
        // Once the camera has fully covered the screen, THIS is called.
        // Now we can safely swap the background to Home while the user can't see it!
        if (isNavigatingToCamera) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NewHomeFragment())
                .commitAllowingStateLoss() // Allows this to happen in the background safely

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
            bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true

            isNavigatingToCamera = false
        }
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
                    (requireActivity() as MainActivity).setNav(R.id.nav_feedback)
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
            .setNegativeButton("Maybe Later") { _, _ -> }
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.darkgreen, null))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.black, null))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    override fun onResume() {
        super.onResume()
    }
}