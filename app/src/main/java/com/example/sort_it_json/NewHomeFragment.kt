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
        val recentTitle = view.findViewById<TextView>(R.id.title)
        val recentTime = view.findViewById<TextView>(R.id.time)
        val recentDifficulty = view.findViewById<TextView>(R.id.difficulty)
        val recentImage = view.findViewById<ImageView>(R.id.image)

        // 🔥 LOAD RECENT GUIDE
        val prefs = requireContext().getSharedPreferences("recent", Context.MODE_PRIVATE)

        val title = prefs.getString("title", null)
        val time = prefs.getString("time", null)
        val difficulty = prefs.getString("difficulty", null)
        val imageName = prefs.getString("image", null)

        if (title != null) {
            recentTitle.text = title
            recentTime.text = time
            recentDifficulty.text = difficulty

            //Change the background of the difficulty component
            difficulty?.lowercase()?.let {
                when (it) {
                    "easy" -> recentDifficulty.setBackgroundResource(R.drawable.difficulty_easy_bg)
                    "moderate" -> recentDifficulty.setBackgroundResource(R.drawable.difficulty_moderate_bg)
                    "advanced" -> recentDifficulty.setBackgroundResource(R.drawable.difficulty_advanced_bg)
                }
            }

            if (imageName != null) {
                val resId = resources.getIdentifier(
                    imageName,
                    "drawable",
                    requireContext().packageName
                )
                if (resId != 0) {
                    recentImage.setImageResource(resId)
                }
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
}