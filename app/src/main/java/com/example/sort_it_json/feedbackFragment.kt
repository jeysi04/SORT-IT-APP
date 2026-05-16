package com.example.sort_it_json

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.ceil

class feedbackFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var btnBack: ImageButton
    private val selectedTags = mutableSetOf<String>()
    private var tagButtons: List<MaterialButton> = listOf()

    // To track if the user has started providing feedback
    private var isFeedbackDirty: Boolean = false
    private var currentRating: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize back button
        btnBack = view.findViewById(R.id.btnBack)

        // Initialize Firebase
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Sign in anonymously
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }

        // Initialize RatingBar
        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#F0CD6E"))
        ratingBar.progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))

        var lastRating = 0f
        ratingBar.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastRating = ratingBar.rating
                }
                MotionEvent.ACTION_UP -> {
                    val starWidth = v.width.toFloat() / ratingBar.numStars
                    val clickedRating = ceil((event.x / starWidth).toDouble()).toFloat()
                    val finalRating = clickedRating.coerceIn(1f, ratingBar.numStars.toFloat())

                    if (finalRating == lastRating) {
                        ratingBar.rating = 0f
                        currentRating = 0f
                        updateDirtyState()
                        v.performClick()
                        return@setOnTouchListener true
                    } else {
                        currentRating = finalRating
                        updateDirtyState()
                    }
                }
            }
            false
        }

        // Initialize EditText and Counter
        val feedbackEditText = view.findViewById<EditText>(R.id.feedback_edit_text)
        val feedbackCounter = view.findViewById<TextView>(R.id.feedback_counter)

        feedbackEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    feedbackEditText.setTypeface(feedbackEditText.typeface, Typeface.ITALIC)
                } else {
                    feedbackEditText.setTypeface(feedbackEditText.typeface, Typeface.NORMAL)
                }
                val currentLength = s?.length ?: 0
                feedbackCounter.text = "$currentLength / 200"
                if (currentLength >= 200) {
                    feedbackCounter.setTextColor(Color.parseColor("#E33939"))
                } else {
                    feedbackCounter.setTextColor(Color.parseColor("#919191"))
                }
                updateDirtyState()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Initialize Tag Buttons
        tagButtons = listOf(
            view.findViewById(R.id.tag_accuracy),
            view.findViewById(R.id.tag_sorting),
            view.findViewById(R.id.tag_design),
            view.findViewById(R.id.tag_reliability),
            view.findViewById(R.id.tag_response),
            view.findViewById(R.id.tag_ease)
        )

        tagButtons.forEach { button ->
            setupTagToggle(button)
        }

        // Initialize Send Button and Submit Overlay Views
        val sendButton = view.findViewById<MaterialButton>(R.id.send_feedback_button)
        val submitOverlay = view.findViewById<View>(R.id.submit_overlay)
        val btnCancelSubmit = view.findViewById<MaterialButton>(R.id.btn_cancel_submit)
        val btnConfirmSubmit = view.findViewById<MaterialButton>(R.id.btn_confirm_submit)

        updateDirtyState() // Set initial state (gray/disabled) upon loading

        sendButton.setOnClickListener {
            val rating = ratingBar.rating
            val comment = feedbackEditText.text.toString().trim()

            if (rating == 0f && comment.isEmpty() && selectedTags.isEmpty()) {
                showCustomToast("Please provide some feedback before sending")
                return@setOnClickListener
            }

            hideKeyboard(feedbackEditText)
            // SHOW THE POP-UP OVERLAY INSTEAD OF SENDING DIRECTLY
            submitOverlay.visibility = View.VISIBLE
        }

        // Action when "Cancel" is clicked in the overlay
        btnCancelSubmit.setOnClickListener {
            submitOverlay.visibility = View.GONE
        }

        // Action when "Submit" is clicked in the overlay
        btnConfirmSubmit.setOnClickListener {
            // Check for internet connection first
            if (!isNetworkAvailable()) {
                submitOverlay.visibility = View.GONE // Hide overlay
                showCustomToast("Please check your internet connection and try again.")
                return@setOnClickListener
            }

            // If there is internet, proceed to send
            submitOverlay.visibility = View.GONE
            val rating = ratingBar.rating
            val comment = feedbackEditText.text.toString().trim()
            sendFeedbackToFirebase(rating, comment, selectedTags.toList(), sendButton, ratingBar, feedbackEditText)
        }

        // Goes to home when back button is clicked
        btnBack.setOnClickListener {
            if (isFeedbackInProgress()) {
                showCustomToast("Please finish or send your feedback before leaving.")
                return@setOnClickListener
            }

            (activity as? MainActivity)?.setNav(R.id.nav_home)
        }
    }

    // --- UTILITY FUNCTIONS ---

    // Function to check Internet Connection
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    // Function to show Custom Toast with a message
    fun showCustomToast(message: String) {
        val textView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular) // Changed to Regular
            setTextColor(Color.parseColor("#000000")) // Changed to Black

            // Gumawa ng Light Background para mabasa ang Black Text
            val backgroundShape = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#EFEDED")) // Light Gray/White Background
                cornerRadius = 200f
            }
            background = backgroundShape

            setPadding(50, 20, 50, 20)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
        }

        val toast = Toast(requireContext())
        toast.duration = Toast.LENGTH_SHORT
        toast.view = textView
        toast.show()
    }

    private fun updateDirtyState() {
        val root = view ?: return
        val feedbackEditText = root.findViewById<EditText>(R.id.feedback_edit_text)

        val comment = feedbackEditText?.text?.toString()?.trim() ?: ""
        isFeedbackDirty = currentRating > 0f || comment.isNotEmpty() || selectedTags.isNotEmpty()

        val sendButton = root.findViewById<MaterialButton>(R.id.send_feedback_button)
        if (isFeedbackDirty) {
            sendButton?.isEnabled = true
            sendButton?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#89C07E")) // Green
            sendButton?.setTextColor(Color.WHITE)
        } else {
            sendButton?.isEnabled = false
            sendButton?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9")) // Gray
            sendButton?.setTextColor(Color.parseColor("#919191"))
        }
    }

    // This function is checked by MainActivity before navigating
    fun isFeedbackInProgress(): Boolean {
        return isFeedbackDirty
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupTagToggle(button: MaterialButton) {
        button.tag = false // false means not selected
        val tagName = button.text.toString()

        button.setOnClickListener {
            val isSelected = !(button.tag as Boolean)
            button.tag = isSelected

            if (isSelected) {
                button.setBackgroundResource(R.drawable.tag_selected)
                button.setTextColor(Color.WHITE)
                button.elevation = 4f
                selectedTags.add(tagName)
            } else {
                button.setBackgroundResource(R.drawable.tag_hard_shadow)
                button.setTextColor(Color.parseColor("#919191"))
                button.elevation = 0f
                selectedTags.remove(tagName)
            }
            updateDirtyState()
        }
    }

    private fun sendFeedbackToFirebase(
        rating: Float,
        comment: String,
        tags: List<String>,
        sendButton: MaterialButton,
        ratingBar: RatingBar,
        editText: EditText
    ) {
        sendButton.isEnabled = false
        // KEEP IT GREEN AND WHITE WHILE SENDING
        sendButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#89C07E"))
        sendButton.setTextColor(Color.WHITE)
        sendButton.text = "Sending..."

        val feedbackData = hashMapOf(
            "rating" to rating,
            "comment" to comment,
            "tags" to tags,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "userId" to (auth.currentUser?.uid ?: "anonymous")
        )

        db.collection("feedbacks")
            .add(feedbackData)
            .addOnSuccessListener {
                // Feedback is sent, so it's no longer "dirty"
                isFeedbackDirty = false

                // Show Global Success Overlay
                (activity as? MainActivity)?.showSuccessOverlay()

                // Reset UI
                ratingBar.rating = 0f
                currentRating = 0f
                editText.text.clear()
                resetTags()
                sendButton.text = "Send Feedback"
                updateDirtyState() // Updates button back to gray and disabled after success
            }
            .addOnFailureListener { e ->
                showCustomToast("Error: ${e.message}")
                sendButton.isEnabled = true
                sendButton.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#89C07E"))
                sendButton.setTextColor(Color.WHITE)
                sendButton.text = "Send Feedback"
            }
    }

    private fun resetTags() {
        selectedTags.clear()
        tagButtons.forEach { button ->
            button.tag = false
            button.setBackgroundResource(R.drawable.tag_hard_shadow)
            button.setTextColor(Color.parseColor("#919191"))
            button.elevation = 0f
        }
    }
}