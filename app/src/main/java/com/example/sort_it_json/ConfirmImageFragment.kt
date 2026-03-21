package com.example.sort_it_json // replace with your package name

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import java.io.File

class ConfirmImageFragment : Fragment() {

    private lateinit var imageView: ImageView // ImageView to display the photo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val view = inflater.inflate(R.layout.fragment_confirm_image, container, false)

        // Bind the ImageView from the layout
        imageView = view.findViewById(R.id.imageView)

        // Retrieve the photo path passed from the previous fragment
        val photoPath = arguments?.getString("photo_path")
        if (photoPath != null) {
            // Load the photo from file into the ImageView
            imageView.setImageURI(Uri.fromFile(File(photoPath)))
        }

        return view
    }

    /**
     * Deletes the photo file at the given path.
     * Call this after you’re done with the photo to remove it.
     */
    fun deletePhoto(photoPath: String) {
        val file = File(photoPath)
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                // File deleted successfully
                println("Photo deleted: $photoPath")
            } else {
                // Failed to delete
                println("Failed to delete photo: $photoPath")
            }
        }
    }
}