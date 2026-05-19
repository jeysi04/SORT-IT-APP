package com.example.sort_it_json

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class PrivacyPolicyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privacy_policy)

        val checkBox = findViewById<CheckBox>(R.id.checkbox_privacy)
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnReject = findViewById<Button>(R.id.btnReject)

        btnAccept.isEnabled = false
        btnAccept.alpha = 0.5f

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            btnAccept.isEnabled = isChecked
            btnAccept.alpha = if (isChecked) 1f else 0.5f
        }

        btnAccept.setOnClickListener {

            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("privacy_accepted", true).apply()

            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("open_home", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        btnReject.setOnClickListener {
            finishAffinity()
        }
    }
}