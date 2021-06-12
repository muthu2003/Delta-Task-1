package com

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.example.guesstheday.MainActivity
import com.example.guesstheday.R
import com.example.guesstheday.databinding.ActivityQuizBinding
import java.util.*

class QuizActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val fixedDayNames = listOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
    private var score : Int = 0
    private lateinit var binding: ActivityQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        generateOptions()
        val timer = object : CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {
                binding.countDown.text = ("00:"+(millisUntilFinished)/1000)
                if(millisUntilFinished<=10000){
                    binding.countDown.setTextColor(Color.parseColor("#F5F5DC"))
                    binding.countDown.text = ("00:0"+(millisUntilFinished)/1000)
                }
            }

            override fun onFinish() {
                finishQuiz()
                finish()
            }
        }
        timer.start()

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_quiz)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun finishQuiz(){
        val vibe2 : Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        Toast.makeText(this,"\tGame Over!!\nYour Score is $score",Toast.LENGTH_SHORT).show()
        vibe2.vibrate(600)
        val result : Intent = Intent(this, MainActivity::class.java)
        result.putExtra("Score_Passed", score)
        setResult(RESULT_OK, result)
    }

    private fun questions() : Int{
        val (weekNum, yearRandom) = generateRandomDate()
        val randomDate : TextView = findViewById(R.id.random_date)
        randomDate.text = yearRandom
        return weekNum
    }

    private fun generateRandomDate() : Pair<Int,String>{
        val finalDate : String
        val gc = GregorianCalendar()
        val year : Int = (1990..2050).random()
        gc.set(GregorianCalendar.YEAR, year)
        val dayOfYear : Int = (1..(gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR))).random()
        gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear)

        finalDate = (gc.get(GregorianCalendar.DAY_OF_MONTH)).toString() + "-" + (gc.get(GregorianCalendar.MONTH)+1) + "-" + gc.get(GregorianCalendar.YEAR)
        return Pair(gc.get(GregorianCalendar.DAY_OF_WEEK),finalDate)
    }

    private fun generateOptions(){
        val wN = questions()
        val dayNames = mutableListOf("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")
        dayNames.removeAt((wN-1))
        val newDays = dayNames.shuffled().take(3)
        val optionIds = listOf(R.id.option1,R.id.option2,R.id.option3,R.id.option4)
        val newIds = optionIds.shuffled()

        val Option1 : RadioButton = findViewById(newIds[0])
        val Option2 : RadioButton = findViewById(newIds[1])
        val Option3 : RadioButton = findViewById(newIds[2])
        val Option4 : RadioButton = findViewById(newIds[3])

        Option1.text = fixedDayNames[wN-1]
        Option2.text = newDays[0]
        Option3.text = newDays[1]
        Option4.text = newDays[2]

        val confirmButton : Button = findViewById(R.id.confirm_button)
        confirmButton.setOnClickListener {
            if(Option1.isChecked||Option2.isChecked ||Option3.isChecked ||Option4.isChecked){
                checkAnswer(wN)
            }
            else{
                val vibe2 : Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                Toast.makeText(this,"Please Select an Option!!",Toast.LENGTH_SHORT).show()
                binding.displayLayout.setBackgroundColor(Color.parseColor("#FF03DAC5"))
                vibe2.vibrate(200)
            }
        }
    }
    private fun checkAnswer(ansNum : Int){
        val vibe2 : Vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        val selectedButton : RadioButton = findViewById(binding.radioOptions.checkedRadioButtonId)
        binding.radioOptions.clearCheck()
        if(selectedButton.text == fixedDayNames[ansNum-1]){
            score += 2
            vibe2.vibrate(100)
            Toast.makeText(this,"Nice Guess :)",Toast.LENGTH_SHORT).show()
            binding.displayLayout.setBackgroundColor(Color.GREEN)
            binding.confirmButton.setBackgroundColor(Color.parseColor("#FF005000"))
            generateOptions()
        }else{
            score -= 1
            Toast.makeText(this,"Wrong Choice :{",Toast.LENGTH_SHORT).show()
            binding.displayLayout.setBackgroundColor(Color.RED)
            binding.confirmButton.setBackgroundColor(Color.BLACK)
            generateOptions()
        }
    }
}

