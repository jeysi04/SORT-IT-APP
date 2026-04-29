package com.example.sort_it_json

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

class NewHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_new_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val captureButton = view.findViewById<Button>(R.id.CaptureButon)
        val aboutButton = view.findViewById<ImageButton>(R.id.btnAbout)

        // Recent views
        val recentCard1 = view.findViewById<View>(R.id.recentCard1)
        val recentCard2 = view.findViewById<View>(R.id.recentCard2)
        val emptyText = view.findViewById<TextView>(R.id.emptyRecentText)

        val prefs = requireContext().getSharedPreferences("recent", Context.MODE_PRIVATE)
        val json = prefs.getString("recent_list", "[]")
        val list = org.json.JSONArray(json)

        if (list.length() == 0) {
            recentCard1.visibility = View.GONE
            recentCard2.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            emptyText.visibility = View.GONE

            // FIRST ITEM
            val item1 = list.getJSONObject(0)
            bindRecent(view, item1, 1)
            recentCard1.visibility = View.VISIBLE

            // SECOND ITEM
            if (list.length() > 1) {
                val item2 = list.getJSONObject(1)
                bindRecent(view, item2, 2)
                recentCard2.visibility = View.VISIBLE
            } else {
                recentCard2.visibility = View.GONE
            }
        }


        // Buttons
        aboutButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }

        captureButton.setOnClickListener {
            val intent = Intent(requireContext(), CameraActivity::class.java)
            startActivity(intent)
        }
    }

    private fun bindRecent(view: View, item: org.json.JSONObject, index: Int) {

        val cardId = if (index == 1) R.id.recentCard1 else R.id.recentCard2
        val titleId = if (index == 1) R.id.title1 else R.id.title2
        val timeId = if (index == 1) R.id.time1 else R.id.time2
        val difficultyId = if (index == 1) R.id.difficulty1 else R.id.difficulty2
        val imageId = if (index == 1) R.id.image1 else R.id.image2

        val card = view.findViewById<View>(cardId)
        val title = view.findViewById<TextView>(titleId)
        val time = view.findViewById<TextView>(timeId)
        val difficulty = view.findViewById<TextView>(difficultyId)
        val image = view.findViewById<ImageView>(imageId)

        val titleText = item.getString("title")
        val timeText = item.getString("time")
        val difficultyText = item.getString("difficulty")
        val imageName = item.getString("image")
        val htmlFile = item.getString("html") // 🔥 IMPORTANT

        title.text = titleText
        time.text = timeText
        difficulty.text = difficultyText

        // difficulty background
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
        if (resId != 0) image.setImageResource(resId)

        // 🔥 CLICK → OPEN HTML
        card.setOnClickListener {
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

}