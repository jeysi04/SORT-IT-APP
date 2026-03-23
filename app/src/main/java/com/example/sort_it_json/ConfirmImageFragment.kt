package com.example.sort_it_json

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// CODE TO DISPLAY PICTURE TAKEN

class ConfirmImageFragment : Fragment() {

    private lateinit var imageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_confirm_image, container, false)

        imageView = view.findViewById(R.id.imageView)

        val photoPath = arguments?.getString("photo_path")
        if (photoPath != null) {
            // Load bitmap from file
            val bitmap = BitmapFactory.decodeFile(photoPath)

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap) // Display the image
            }
        }

        return view
    }
}