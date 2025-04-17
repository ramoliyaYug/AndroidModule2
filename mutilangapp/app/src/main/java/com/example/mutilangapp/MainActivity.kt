package com.example.mutilangapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object{
        var selectedLanguage = "en"
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(applyLanguage(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val englishButton = findViewById<Button>(R.id.englishbtn)
        val hindiButton = findViewById<Button>(R.id.hindibtn)
        val gujaratiButton = findViewById<Button>(R.id.gujaratibtn)

        englishButton.setOnClickListener {
            changeLanguage("en")
        }
        hindiButton.setOnClickListener {
            changeLanguage("hi")
        }
        gujaratiButton.setOnClickListener {
            changeLanguage("gu")
        }

    }

    private fun changeLanguage(languageCode: String) {
        selectedLanguage = languageCode
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun applyLanguage(context: Context): Context {
        val locale = Locale(selectedLanguage)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }
}