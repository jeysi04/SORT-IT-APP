package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.Job
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File

class LoadingFragment : Fragment() {

    private var filePath: String? = null
    private var timeoutJob: Job? = null
    private var analysisJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filePath = arguments?.getString("file_path")

        val cancelButton = view.findViewById<Button>(R.id.CancelButon)

        cancelButton.setOnClickListener {
            showExitDialog()
        }

        // START ANALYSIS AUTOMATICALLY
        filePath?.let { file ->
            analyzePhoto(File(file))
            startTimeoutWarning()
        }
    }

    private fun analyzePhoto(file: File) {

        analysisJob = lifecycleScope.launch(Dispatchers.IO) {

            try {
                // Decode image
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

                val stream = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 70, stream)

                val requestFile = RequestBody.create(
                    "image/jpeg".toMediaTypeOrNull(),
                    stream.toByteArray()
                )

                val body = MultipartBody.Part.createFormData(
                    "file",
                    "compressed.jpg",
                    requestFile
                )

                // Optional loading delay (UX)
                delay(1500)

                // API CALL
                val response = ApiClient.service.predict(body)

                bitmap.recycle()
                resized.recycle()
                stream.close()

                Log.d("LoadingFragment", "Response: $response")

                withContext(Dispatchers.Main) {

                    val resultFragment = RecyclableresultFragment().apply {
                        arguments = Bundle().apply {
                            putParcelable("predict_response", response)
                        }
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, resultFragment)
                        .commit()
                }

                if (file.exists()) file.delete()

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Analysis failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                Log.e("LoadingFragment", "Error", e)
            }
        }
    }

    private fun startTimeoutWarning() {

        timeoutJob = lifecycleScope.launch {

            delay(5000) // 5 seconds

            if (!isAdded) return@launch

            showTimeoutDialog()
        }
    }

    private fun showTimeoutDialog() {
        val title = SpannableString("This is taking longer than expected...").apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.darkgreen, null)),
                0,
                length,
                0
            )
        }

        val message = SpannableString("Would you like to take another picture?").apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black, null)),
                0,
                length,
                0
            )
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                analysisJob?.cancel()
                parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                startActivity(Intent(requireContext(), CameraActivity::class.java))
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    private fun showExitDialog() {

        val title = SpannableString("Cancel Process").apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.darkgreen, null)),
                0,
                length,
                0
            )
        }

        val message = SpannableString(
            "Are you sure you want to cancel the process? Your progress will be lost."
        ).apply {
            setSpan(
                ForegroundColorSpan(resources.getColor(R.color.black, null)),
                0,
                length,
                0
            )
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                parentFragmentManager.popBackStack()
                startActivity(Intent(requireContext(), CameraActivity::class.java))
            }
            .setNegativeButton("No", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(resources.getColor(R.color.black, null))

        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }
}