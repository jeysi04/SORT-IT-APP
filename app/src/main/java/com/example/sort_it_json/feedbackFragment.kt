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
import androidx.activity.OnBackPressedCallback
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

    // Variable to track if we are actively sending data to Firebase
    private var isSending: Boolean = false

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

        // --- SYSTEM BACK BUTTON HANDLER ---
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFeedbackInProgress()) {
                    // SHOW EXIT POP-UP INSTEAD OF TOAST
                    view.findViewById<View>(R.id.exit_overlay)?.visibility = View.VISIBLE
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // Initialize RatingBar
        val ratingBar = view.findViewById<RatingBar>(R.id.rating_bar)
        ratingBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#F0CD6E"))
        ratingBar.progressBackgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))

        var lastRating = 0f
        ratingBar.setOnTouchListener { v, event ->
            if (isSending) return@setOnTouchListener true // BLOCKS interaction while sending

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

        // Initialize the Local Success Overlay Views
        val successOverlay = view.findViewById<View>(R.id.success_overlay)
        val btnCloseSuccess = view.findViewById<MaterialButton>(R.id.close_success_button)

        // --- NEW: Initialize the Exit Overlay Views ---
        val exitOverlay = view.findViewById<View>(R.id.exit_overlay)
        val btnCancelExit = view.findViewById<MaterialButton>(R.id.btn_cancel_exit)
        val btnConfirmExit = view.findViewById<MaterialButton>(R.id.btn_confirm_exit)

        updateDirtyState()

        // Send Feedback Button Logic
        sendButton.setOnClickListener {
            if (isSending) return@setOnClickListener // Double-tap protection

            val rating = ratingBar.rating
            val comment = feedbackEditText.text.toString().trim()

            if (rating == 0f && comment.isEmpty() && selectedTags.isEmpty()) {
                showCustomToast("Please provide some feedback before sending")
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                showCustomToast("Please check your internet connection and try again.")
                return@setOnClickListener
            }

            hideKeyboard(feedbackEditText)
            submitOverlay.visibility = View.VISIBLE
        }

        // Submit Overlay Logic
        btnCancelSubmit.setOnClickListener {
            submitOverlay.visibility = View.GONE
        }

        btnConfirmSubmit.setOnClickListener {
            if (!isNetworkAvailable()) {
                submitOverlay.visibility = View.GONE
                showCustomToast("Connection lost. Please check your internet and try again.")
                return@setOnClickListener
            }

            submitOverlay.visibility = View.GONE
            val rating = ratingBar.rating
            val comment = feedbackEditText.text.toString().trim()
            sendFeedbackToFirebase(rating, comment, selectedTags.toList(), sendButton, ratingBar, feedbackEditText)
        }

        // Success Overlay Logic
        btnCloseSuccess.setOnClickListener {
            successOverlay.visibility = View.GONE
            (activity as? MainActivity)?.setNav(R.id.nav_home)
        }

        // --- NEW: Exit Overlay Logic ---
        btnCancelExit.setOnClickListener {
            exitOverlay.visibility = View.GONE // Stay on the page
        }

        btnConfirmExit.setOnClickListener {
            // User confirmed they want to leave, so we reset the dirty state
            isFeedbackDirty = false
            exitOverlay.visibility = View.GONE
            (activity as? MainActivity)?.setNav(R.id.nav_home) // Go home
        }

        // Top Header Back Button Logic
        btnBack.setOnClickListener {
            if (isFeedbackInProgress()) {
                // SHOW EXIT POP-UP INSTEAD OF TOAST
                exitOverlay.visibility = View.VISIBLE
            } else {
                (activity as? MainActivity)?.setNav(R.id.nav_home)
            }
        }
    }

    // --- UTILITY FUNCTIONS ---

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

    fun showCustomToast(message: String) {
        val textView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.montserrat_regular)
            setTextColor(Color.parseColor("#000000"))

            val backgroundShape = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.parseColor("#EFEDED"))
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

    // Freezes or Unfreezes UI components
    private fun setInputsEnabled(enabled: Boolean) {
        val root = view ?: return
        val ratingBar = root.findViewById<RatingBar>(R.id.rating_bar)
        val feedbackEditText = root.findViewById<EditText>(R.id.feedback_edit_text)

        ratingBar.setIsIndicator(!enabled) // Freezes the rating bar strictly
        feedbackEditText.isEnabled = enabled // Disables typing
        tagButtons.forEach { it.isEnabled = enabled } // Disables tag clicking
    }

    private fun updateDirtyState() {
        // If we are actively sending, DO NOT re-enable the button!
        if (isSending) return

        val root = view ?: return
        val feedbackEditText = root.findViewById<EditText>(R.id.feedback_edit_text)

        val comment = feedbackEditText?.text?.toString()?.trim() ?: ""
        isFeedbackDirty = currentRating > 0f || comment.isNotEmpty() || selectedTags.isNotEmpty()

        val sendButton = root.findViewById<MaterialButton>(R.id.send_feedback_button)
        if (isFeedbackDirty) {
            sendButton?.isEnabled = true
            sendButton?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#89C07E"))
            sendButton?.setTextColor(Color.WHITE)
        } else {
            sendButton?.isEnabled = false
            sendButton?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D9D9D9"))
            sendButton?.setTextColor(Color.parseColor("#919191"))
        }
    }

    fun isFeedbackInProgress(): Boolean {
        // If we are actively sending, treat it as in-progress so they can't swipe back out
        return isFeedbackDirty || isSending
    }

    // --- NEW: Public function for MainActivity to trigger the exit overlay ---
    fun showExitOverlay() {
        view?.findViewById<View>(R.id.exit_overlay)?.visibility = View.VISIBLE
    }

    private fun hideKeyboard(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setupTagToggle(button: MaterialButton) {
        button.tag = false
        val tagName = button.text.toString()

        button.setOnClickListener {
            if (isSending) return@setOnClickListener // Blocks clicks while sending

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
        // --- PREPARE FOR SENDING (FREEZE UI) ---
        isSending = true
        setInputsEnabled(false)

        sendButton.isEnabled = false
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
                // --- SUCCESS (UNFREEZE AND RESET) ---
                isSending = false
                isFeedbackDirty = false

                val successOverlay = view?.findViewById<View>(R.id.success_overlay)
                successOverlay?.visibility = View.VISIBLE

                ratingBar.rating = 0f
                currentRating = 0f
                editText.text.clear()
                resetTags()

                sendButton.text = "Send Feedback"
                setInputsEnabled(true)
                updateDirtyState()
            }
            .addOnFailureListener { e ->
                // --- FAILED (UNFREEZE SO THEY CAN RETRY) ---
                isSending = false
                setInputsEnabled(true)

                showCustomToast("Error: ${e.message}")
                sendButton.text = "Send Feedback"
                updateDirtyState()
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