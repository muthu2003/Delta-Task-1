@file:Suppress("DEPRECATION")

package com.example.guesstheday

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Vibrator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.QuizActivity

class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration
    private val sharedPrefs: String = "sharedPrefs"
    private val highscore_key: String = "Highscore"
    private val requestScore: Int = 0
    private var highScore: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val vibe: Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        loadHighScore()

        val playButton : Button = findViewById(R.id.play_button)
        playButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivityForResult(intent, requestScore)
            vibe.vibrate(100)
        }
    }
    private fun loadHighScore(){
        val prefs : SharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE)
        highScore = prefs.getInt(highscore_key,0)
        val highScoreText: TextView = findViewById(R.id.highscore)
        highScoreText.text = ("HighScore : "+highScore)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestScore) {
            if (resultCode == RESULT_OK) {
                val score: Int = data!!.getIntExtra("Score_Passed", 0)
                if (score > highScore) {
                    highScore = score
                }
                val highScoreText: TextView = findViewById(R.id.highscore)
                highScoreText.text = ("HighScore : " + highScore)

                val prefs: SharedPreferences = getSharedPreferences(sharedPrefs, MODE_PRIVATE)
                val editor: SharedPreferences.Editor = prefs.edit()
                editor.putInt(highscore_key, highScore)
                editor.apply()
            }
        }

    }
}