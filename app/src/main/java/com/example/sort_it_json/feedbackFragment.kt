package com.example.sort_it_json

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlin.math.ceil

class feedbackFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RatingBar and set filled stars color to #F0CD6E
        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#F0CD6E"))
        // Optional: Set background stars color if needed
        ratingBar.progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))

        // Add toggle behavior to RatingBar: clicking the same rating twice resets it to 0
        var lastRating = 0f
        ratingBar.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Store the rating before the current touch interaction begins
                    lastRating = ratingBar.rating
                }
                MotionEvent.ACTION_UP -> {
                    // Calculate which star was clicked based on touch X position
                    val starWidth = v.width.toFloat() / ratingBar.numStars
                    val clickedRating = ceil((event.x / starWidth).toDouble()).toFloat()
                    val finalRating = clickedRating.coerceIn(1f, ratingBar.numStars.toFloat())

                    // If the user clicked the star that was already selected, reset to 0
                    if (finalRating == lastRating) {
                        ratingBar.rating = 0f
                        v.performClick()
                        return@setOnTouchListener true
                    }
                }
            }
            // Return false for other actions to let RatingBar handle its standard behavior
            false
        }

        // Initialize Feedback EditText and character counter
        val feedbackEditText = view.findViewById<EditText>(R.id.feedback_edit_text)
        val feedbackCounter = view.findViewById<TextView>(R.id.feedback_counter)

        // Listener to change style from italic (hint state) to normal when user types
        // and enforce 100 character limit with color change #E33939
        feedbackEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    feedbackEditText.setTypeface(feedbackEditText.typeface, Typeface.ITALIC)
                } else {
                    feedbackEditText.setTypeface(feedbackEditText.typeface, Typeface.NORMAL)
                }

                val currentLength = s?.length ?: 0
                feedbackCounter.text = "$currentLength / 100"

                if (currentLength >= 100) {
                    feedbackCounter.setTextColor(Color.parseColor("#E33939"))
                } else {
                    feedbackCounter.setTextColor(Color.parseColor("#919191"))
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Initialize buttons using the inflated view
        val tags = listOf(
            view.findViewById<MaterialButton>(R.id.tag_accuracy),
            view.findViewById<MaterialButton>(R.id.tag_sorting),
            view.findViewById<MaterialButton>(R.id.tag_design),
            view.findViewById<MaterialButton>(R.id.tag_reliability),
            view.findViewById<MaterialButton>(R.id.tag_response),
            view.findViewById<MaterialButton>(R.id.tag_ease)
        )

        tags.forEach { button ->
            setupTagToggle(button)
        }
    }

    private fun setupTagToggle(button: MaterialButton) {
        var isSelected = false

        button.setOnClickListener {
            isSelected = !isSelected

            if (isSelected) {
                // Selected state: Solid Yellow Pill (#F0CD6E)
                button.setBackgroundResource(R.drawable.tag_selected)
                button.setTextColor(Color.WHITE)
                button.elevation = 4f
            } else {
                // Default state: Light Grey with Hard Shadow
                button.setBackgroundResource(R.drawable.tag_hard_shadow)
                button.setTextColor(Color.parseColor("#919191"))
                button.elevation = 0f
            }
        }
    }
}