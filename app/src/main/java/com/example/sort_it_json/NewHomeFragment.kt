package com.example.sort_it_json

// Android context + UI imports
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

// Fragment base class
import androidx.fragment.app.Fragment

// JSON handling
import org.json.JSONArray
import org.json.JSONObject

class NewHomeFragment : Fragment() {

    // Called when fragment UI is created
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate XML layout for this fragment
        return inflater.inflate(R.layout.fragment_new_home, container, false)
    }

    // Called after view is created and ready
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // ----------------------------
        // UI ELEMENT REFERENCES
        // ----------------------------

        // Capture button (opens camera)
        val captureButton =
            view.findViewById<Button>(R.id.CaptureButon)

        // About button (opens About screen)
        val aboutButton =
            view.findViewById<ImageButton>(R.id.btnAbout)

        // First recent card container
        val recentCard1 =
            view.findViewById<View>(R.id.recentCard1)

        // Second recent card container
        val recentCard2 =
            view.findViewById<View>(R.id.recentCard2)

        // Text shown when no recent items exist
        val emptyText =
            view.findViewById<TextView>(R.id.emptyRecentText)

        // ----------------------------
        // LOAD RECENT DATA
        // ----------------------------

        // Get SharedPreferences storage named "recent"
        val prefs = requireContext()
            .getSharedPreferences("recent", Context.MODE_PRIVATE)

        val appPrefs = requireContext()
            .getSharedPreferences(
                "app_state",
                Context.MODE_PRIVATE
            )

    // Check if this is first app launch
        val isFirstLaunch =
            appPrefs.getBoolean(
                "is_first_launch",
                true
            )

        if (isFirstLaunch) {

            // Clear recent guides
            prefs.edit()
                .remove("recent_list")
                .apply()

            // Mark app as no longer first launch
            appPrefs.edit()
                .putBoolean(
                    "is_first_launch",
                    false
                )
                .apply()
        }

        // Read stored JSON string (default empty array if none exists)
        val jsonString = prefs.getString("recent_list", "[]")

        // Convert JSON string into JSONArray
        val list = JSONArray(jsonString)

        // ----------------------------
        // HANDLE EMPTY STATE
        // ----------------------------

        if (list.length() == 0) {

            // No recent items → hide cards
            recentCard1.visibility = View.GONE
            recentCard2.visibility = View.GONE

            // Show "empty" message
            emptyText.visibility = View.VISIBLE

        } else {

            // We have at least 1 item → hide empty text
            emptyText.visibility = View.GONE

            // ----------------------------
            // LOAD FIRST ITEM (INDEX 0)
            // ----------------------------

                val item1 = list.getJSONObject(0)

                // Bind first recent card
                bindRecent(view, item1, 1)

                // Make sure card is visible
                recentCard1.visibility = View.VISIBLE

            // ----------------------------
            // LOAD SECOND ITEM (INDEX 1)
            // ----------------------------

            if (list.length() > 1) {

                val item2 = list.getJSONObject(1)

                // Bind second recent card
                bindRecent(view, item2, 2)

                // Make sure second card is visible
                recentCard2.visibility = View.VISIBLE

            } else {

                // Hide second card if not available
                recentCard2.visibility = View.GONE
            }
        }

        // ----------------------------
        // ABOUT BUTTON CLICK
        // ----------------------------

        aboutButton.setOnClickListener {

            // Navigate to AboutFragment
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    AboutFragment()
                )
                .addToBackStack(null)
                .commit()
        }

        // ----------------------------
        // CAMERA BUTTON CLICK
        // ----------------------------

        captureButton.setOnClickListener {

            // Open CameraActivity
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
    }

    // ----------------------------
    // FUNCTION: BIND RECENT ITEM TO CARD
    // ----------------------------
    private fun bindRecent(
        view: View,
        item: JSONObject,
        index: Int
    ) {

        // ----------------------------
        // SAFELY GET UI IDS BASED ON INDEX
        // ----------------------------

        val cardId =
            if (index == 1) R.id.recentCard1 else R.id.recentCard2

        val titleId =
            if (index == 1) R.id.title1 else R.id.title2

        val timeId =
            if (index == 1) R.id.time1 else R.id.time2

        val difficultyId =
            if (index == 1) R.id.difficulty1 else R.id.difficulty2

        val imageId =
            if (index == 1) R.id.image1 else R.id.image2

        val bookmarkId =
            if (index == 1) R.id.bookmark1 else R.id.bookmark2

        // ----------------------------
        // FIND VIEWS
        // ----------------------------

        val card = view.findViewById<View>(cardId)
        val title = view.findViewById<TextView>(titleId)
        val time = view.findViewById<TextView>(timeId)
        val difficulty = view.findViewById<TextView>(difficultyId)
        val image = view.findViewById<ImageView>(imageId)
        val bookmark = view.findViewById<ImageButton>(bookmarkId)

        // ----------------------------
        // SAFE JSON PARSING (IMPORTANT FIX)
        // ----------------------------

        // Using optString prevents crashes if key is missing
        val titleText = item.optString("title", "")
        val timeText = item.optString("time", "")
        val difficultyText = item.optString("difficulty", "")
        val imageName = item.optString("image", "")
        val htmlFile = item.optString("html", "")

        // ----------------------------
        // SET TEXT VALUES
        // ----------------------------

        title.text = titleText
        time.text = timeText
        difficulty.text = difficultyText

        // ----------------------------
        // DIFFICULTY STYLE HANDLING
        // ----------------------------

        when (difficultyText.lowercase()) {

            "easy" -> difficulty.setBackgroundResource(R.drawable.difficulty_easy_bg)

            "moderate" -> difficulty.setBackgroundResource(R.drawable.difficulty_moderate_bg)

            "advanced" -> difficulty.setBackgroundResource(R.drawable.difficulty_advanced_bg)
        }

        // ----------------------------
        // LOAD IMAGE FROM DRAWABLE
        // ----------------------------

        val resId = resources.getIdentifier(
            imageName,
            "drawable",
            requireContext().packageName
        )

        // Only set image if it exists
        if (resId != 0) {
            image.setImageResource(resId)
        }

        // ----------------------------
        // BOOKMARK LOGIC
        // ----------------------------

        val prefs = requireContext()
            .getSharedPreferences("bookmarks", Context.MODE_PRIVATE)

        // Get saved bookmark set safely
        val bookmarkedTitles =
            prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet()
                ?: mutableSetOf()

        // Check if current item is bookmarked
        var isBookmarked = bookmarkedTitles.contains(titleText)

        // Set correct bookmark icon
        updateBookmarkIcon(bookmark, isBookmarked)

        // Toggle bookmark on click
        bookmark.setOnClickListener {

            isBookmarked = !isBookmarked

            if (isBookmarked) {
                bookmarkedTitles.add(titleText)
            } else {
                bookmarkedTitles.remove(titleText)
            }

            // Save updated set
            prefs.edit()
                .putStringSet("bookmark_titles", bookmarkedTitles)
                .apply()

            // Update icon UI
            updateBookmarkIcon(bookmark, isBookmarked)
        }

        // ----------------------------
        // OPEN GUIDE ON CLICK
        // ----------------------------

        card.setOnClickListener {

            val fragment = WebViewFragment()

            val bundle = Bundle()

            // Pass HTML file to next fragment
            bundle.putString("html_file", htmlFile)

            fragment.arguments = bundle

            // Navigate to WebView
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    // ----------------------------
    // UPDATE BOOKMARK ICON
    // ----------------------------
    private fun updateBookmarkIcon(
        button: ImageButton,
        isBookmarked: Boolean
    ) {

        if (isBookmarked) {

            // Filled icon
            button.setImageResource(R.drawable.bookmarked)

        } else {

            // Empty icon
            button.setImageResource(R.drawable.notbookmark)
        }
    }
}