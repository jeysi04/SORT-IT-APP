package com.example.sort_it_json

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import java.io.File

class ConfirmImageFragment : Fragment(R.layout.fragment_confirm_image) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.imageView)

        val photoPath = arguments?.getString("photo_path")

        photoPath?.let {
            val file = File(it)

            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}