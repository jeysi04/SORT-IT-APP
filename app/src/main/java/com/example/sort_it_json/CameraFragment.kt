package com.example.sort_it_json

// Import classes needed for camera, UI, storage, and permissions
import android.app.Activity                      // Used for RESULT_OK (camera success result)
import android.content.Intent                  // Used to launch the camera app
import android.graphics.Bitmap                 // Represents the captured image
import android.os.Bundle                       // Used to pass data between fragments
import android.provider.MediaStore             // Contains ACTION_IMAGE_CAPTURE (camera intent)
import android.view.LayoutInflater             // Used to inflate XML layout
import android.view.View                       // Base view class
import android.view.ViewGroup                  // Layout container
import android.widget.Button                   // Button UI element
import android.widget.ImageView                // ImageView to display photo
import android.widget.Toast                    // For showing short messages
import androidx.fragment.app.Fragment          // Base Fragment class
import java.io.File                            // Used to create file for saving image
import java.io.FileOutputStream                // Used to write image to file
import androidx.core.content.ContextCompat     // Used to check permissions

// Define the fragment class
class CameraFragment : Fragment() {

    // Declare UI components (will be initialized later)
    private lateinit var captureButton: Button

    // Request code for camera intent (used to identify result later)
    private val CAMERA_REQUEST_CODE = 101

    // Request code for permission request (used to identify permission result)
    private val CAMERA_PERMISSION_CODE = 100

    // Called when the fragment UI is being created
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout XML into a View object
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        // Find UI elements from the layout using their IDs
        captureButton = view.findViewById(R.id.capture_button)

        return view // Return the created view
    }

    // Called after the view is created (safe to interact with UI here)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if camera permission is already granted
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            enableCameraButton() // If granted → enable camera button
        } else {
            // If not granted → request permission from user
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    // Function to enable button and set click behavior
    private fun enableCameraButton() {

        // Enable the button so user can click it
        captureButton.isEnabled = true

        // Set click listener
        captureButton.setOnClickListener {

            // Create intent to open built-in camera app
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            // Launch camera app and wait for result
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    // Called after user responds to permission request
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Check if this result is for camera permission
        if (requestCode == CAMERA_PERMISSION_CODE) {

            // Check if permission was granted
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED)
            ) {
                enableCameraButton() // Permission granted → enable camera
            } else {
                // Permission denied → show message
                Toast.makeText(requireContext(), "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Called automatically after camera activity finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if this result is from camera and successful
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Get the captured image as a Bitmap (thumbnail)
            val photo = data?.extras?.get("data") as? Bitmap

            // Check if photo is not null
            if (photo != null) {

                // 2️⃣ Save the image to a file
                val file = File(
                    requireContext().getExternalFilesDir(null), // App-specific storage
                    "photo.jpg" // File name
                )

                // Create output stream to write data into file
                val out = FileOutputStream(file)

                // Compress and save bitmap as JPEG
                photo.compress(Bitmap.CompressFormat.JPEG, 90, out)

                // Flush and close stream to complete writing
                out.flush()
                out.close()

                // 3️⃣ Send file path to another fragment
                val bundle = Bundle().apply {
                    putString("photo_path", file.absolutePath) // Store file path
                }

                // Create the next fragment
                val fragment = ConfirmImageFragment()

                // Attach data to fragment
                fragment.arguments = bundle

                // Replace current fragment with ConfirmImageFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Allows back navigation
                    .commit()

            } else {
                // If photo is null → show error message
                Toast.makeText(requireContext(), "Failed to capture photo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}