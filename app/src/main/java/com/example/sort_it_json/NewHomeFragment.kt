package com.example.sort_it_json

// Android context + UI imports
import android.content.Context
import android.content.Intent
import android.graphics.Color
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_new_home, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val captureButton = view.findViewById<Button>(R.id.CaptureButon)
        val aboutButton = view.findViewById<ImageButton>(R.id.btnAbout)
        val recentCard1 = view.findViewById<View>(R.id.recentCard1)
        val recentCard2 = view.findViewById<View>(R.id.recentCard2)
        val emptyText = view.findViewById<TextView>(R.id.emptyRecentText)

        val prefs = requireContext().getSharedPreferences("recent", Context.MODE_PRIVATE)
        val appPrefs = requireContext().getSharedPreferences("app_state", Context.MODE_PRIVATE)

        val isFirstLaunch = appPrefs.getBoolean("is_first_launch", true)

        if (isFirstLaunch) {
            prefs.edit().remove("recent_list").apply()
            appPrefs.edit().putBoolean("is_first_launch", false).apply()
        }

        val jsonString = prefs.getString("recent_list", "[]")
        val rawList = JSONArray(jsonString)

        val list = JSONArray()
        val seenTitles = mutableSetOf<String>()

        for (i in 0 until rawList.length()) {
            val item = rawList.getJSONObject(i)
            val titleText = item.optString("title", "").trim()

            if (!seenTitles.contains(titleText) && titleText.isNotEmpty()) {
                seenTitles.add(titleText)
                list.put(item)
            }

            if (list.length() == 2) break
        }

        prefs.edit().putString("recent_list", list.toString()).apply()

        // ----------------------------
        // HANDLE EMPTY STATE
        // ----------------------------
        if (list.length() == 0) {
            recentCard1.visibility = View.GONE
            recentCard2.visibility = View.GONE
            emptyText.visibility = View.VISIBLE

        } else {
            emptyText.visibility = View.GONE

            // LOAD FIRST ITEM
            val item1 = list.getJSONObject(0)
            bindRecent(view, item1, 1)
            recentCard1.visibility = View.VISIBLE

            // LOAD SECOND ITEM
            if (list.length() > 1) {
                val item2 = list.getJSONObject(1)
                bindRecent(view, item2, 2)
                recentCard2.visibility = View.VISIBLE
            } else {
                recentCard2.visibility = View.INVISIBLE
            }
        }

        aboutButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }

        captureButton.setOnClickListener {
            (activity as MainActivity).apply {
                val intent = Intent(requireContext(), CameraActivity::class.java)
                startCamera(intent)
            }
        }
    }

    private fun bindRecent(
        view: View,
        item: JSONObject,
        index: Int
    ) {
        val cardId = if (index == 1) R.id.recentCard1 else R.id.recentCard2
        val titleId = if (index == 1) R.id.title1 else R.id.title2
        val timeId = if (index == 1) R.id.time1 else R.id.time2
        val difficultyId = if (index == 1) R.id.difficulty1 else R.id.difficulty2
        val imageId = if (index == 1) R.id.image1 else R.id.image2
        val bookmarkId = if (index == 1) R.id.bookmark1 else R.id.bookmark2

        val card = view.findViewById<View>(cardId)
        val title = view.findViewById<TextView>(titleId)
        val time = view.findViewById<TextView>(timeId)
        val difficulty = view.findViewById<TextView>(difficultyId)
        val image = view.findViewById<ImageView>(imageId)
        val bookmark = view.findViewById<ImageButton>(bookmarkId)

        val titleText = item.optString("title", "")
        val timeText = item.optString("time", "")
        val difficultyText = item.optString("difficulty", "")
        val imageName = item.optString("image", "")
        val htmlFile = item.optString("html", "")

        title.text = titleText
        time.text = timeText
        difficulty.text = difficultyText

        when (difficultyText.lowercase()) {
            "easy" -> difficulty.setBackgroundResource(R.drawable.difficulty_easy_bg)
            "moderate" -> difficulty.setBackgroundResource(R.drawable.difficulty_moderate_bg)
            "advanced" -> difficulty.setBackgroundResource(R.drawable.difficulty_advanced_bg)
        }

        val resId = resources.getIdentifier(
            imageName,
            "drawable",
            requireContext().packageName
        )

        if (resId != 0) {
            image.setImageResource(resId)
        }

        val prefs = requireContext().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        val bookmarkedTitles = prefs.getStringSet("bookmark_titles", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        var isBookmarked = bookmarkedTitles.contains(titleText)
        updateBookmarkIcon(bookmark, isBookmarked)

        bookmark.setOnClickListener {
            isBookmarked = !isBookmarked
            if (isBookmarked) {
                bookmarkedTitles.add(titleText)
            } else {
                bookmarkedTitles.remove(titleText)
            }
            prefs.edit().putStringSet("bookmark_titles", bookmarkedTitles).apply()
            updateBookmarkIcon(bookmark, isBookmarked)
        }

        card.setOnClickListener {
            // FIX: Tell memory to bump this item to the top!
            bumpItemToTop(item)

            val fragment = WebViewFragment()
            val bundle = Bundle()
            bundle.putString("html_file", htmlFile)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun updateBookmarkIcon(button: ImageButton, isBookmarked: Boolean) {
        if (isBookmarked) {
            button.setImageResource(R.drawable.bookmarked)
        } else {
            button.setImageResource(R.drawable.notbookmark)
        }
    }

    // ==========================================
    // FIX: BUMP RECENT ITEM TO TOP
    // ==========================================
    private fun bumpItemToTop(clickedItem: JSONObject) {
        val prefs = requireContext().getSharedPreferences("recent", Context.MODE_PRIVATE)
        val oldJson = prefs.getString("recent_list", "[]")
        val oldList = JSONArray(oldJson)

        val newList = JSONArray()

        // 1. Put the clicked item at the very top
        newList.put(clickedItem)

        val clickedTitle = clickedItem.optString("title", "")

        // 2. Add the rest of the old items (skipping the one we just moved)
        for (i in 0 until oldList.length()) {
            if (newList.length() == 2) break // Keep max 2 items

            val oldItem = oldList.getJSONObject(i)
            if (oldItem.optString("title", "") != clickedTitle) {
                newList.put(oldItem)
            }
        }

        // 3. Save the rearranged list back to memory
        prefs.edit().putString("recent_list", newList.toString()).apply()
    }
}