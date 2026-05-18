package com.example.sort_it_json

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
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
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
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

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var progressJob: Job? = null

    private var isPaused = false
    private var pendingResponse: PredictResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- FIX: SYSTEM BACK BUTTON HANDLER ---
        // Ensures swiping back on the phone triggers the pop-up properly
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                pauseProcessing()
                showExitDialog()
            }
        })

        progressBar = view.findViewById(R.id.progressBarCircular)
        progressText = view.findViewById(R.id.progressText)

        startFakeProgress()

        filePath = arguments?.getString("file_path")

        val cancelButton = view.findViewById<Button>(R.id.CancelButon)

        cancelButton.backgroundTintList = ColorStateList.valueOf(
            Color.parseColor("#467750")
        )

        cancelButton.setOnClickListener {
            pauseProcessing()
            showExitDialog()
        }

        filePath?.let { file ->
            lifecycleScope.launch {
                delay(300) // allow loading UI to render first
                analyzePhoto(File(file))
                startTimeoutWarning()
            }
        }
    }

    fun pauseProcessing() {
        isPaused = true
        timeoutJob?.cancel()
    }

    fun resumeProcessing() {
        isPaused = false
        startTimeoutWarning()

        pendingResponse?.let {
            navigateToResult(it)
            pendingResponse = null
        }
    }

    private fun analyzePhoto(file: File) {
        analysisJob = lifecycleScope.launch(Dispatchers.IO) {
            try {
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

                delay(1500)

                val response = ApiClient.service.predict(body)

                bitmap.recycle()
                resized.recycle()
                stream.close()

                withContext(Dispatchers.Main) {
                    if (isPaused) {
                        pendingResponse = response
                    } else {
                        navigateToResult(response)
                    }
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
            }
        }
    }

    private fun navigateToResult(response: PredictResponse) {
        progressJob?.cancel()
        progressBar.progress = 100
        progressText.text = "100%"

        val resultFragment = RecyclableresultFragment().apply {
            arguments = Bundle().apply {
                putParcelable("predict_response", response)
            }
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, resultFragment)
            .commit()
    }

    private fun startFakeProgress() {
        progressJob = lifecycleScope.launch {
            var progress = 0
            while (progress < 95) {
                delay(300)
                if (!isPaused) {
                    progress += (1..3).random()
                    if (progress > 95) progress = 95
                    progressBar.progress = progress
                    progressText.text = "$progress%"
                }
            }
        }
    }

    private fun startTimeoutWarning() {
        timeoutJob = lifecycleScope.launch {
            delay(30000)
            if (!isAdded) return@launch

            pauseProcessing()
            showTimeoutDialog()
        }
    }

    private fun showTimeoutDialog() {
        val title = SpannableString("This is taking longer than expected...").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#467750")), 0, length, 0)
        }

        val message = SpannableString("Would you like to take another picture?").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                analysisJob?.cancel()
                parentFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
                startActivity(Intent(requireContext(), CameraActivity::class.java))
            }
            .setNegativeButton("No") { _, _ ->
                resumeProcessing()
            }
            .setOnCancelListener {
                resumeProcessing()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }

    private fun showExitDialog() {
        val title = SpannableString("Cancel Process?").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#467750")), 0, length, 0)
        }

        val message = SpannableString("Are you sure you want to cancel the process? Your progress will be lost.").apply {
            setSpan(ForegroundColorSpan(Color.parseColor("#000000")), 0, length, 0)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ ->
                progressJob?.cancel()
                analysisJob?.cancel()

                // FIX: Swap directly to NewHomeFragment to bypass the MainActivity loop
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, NewHomeFragment())
                    .commit()

                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNav)
                bottomNav.menu.findItem(R.id.nav_home)?.isChecked = true
            }
            .setNegativeButton("No") { _, _ ->
                resumeProcessing()
            }
            .setOnCancelListener {
                resumeProcessing()
            }
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#000000"))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#000000"))
        dialog.window?.setBackgroundDrawableResource(R.color.white)
    }
}