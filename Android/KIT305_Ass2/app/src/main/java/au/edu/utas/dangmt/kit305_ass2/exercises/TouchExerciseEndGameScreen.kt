package au.edu.utas.dangmt.kit305_ass2.exercises

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseEndGameScreenBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseGameBinding
import java.io.Serializable

class TouchExerciseEndGameScreen : AppCompatActivity() {
    private lateinit var ui : ActivityTouchExerciseEndGameScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTouchExerciseEndGameScreenBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val touchExerciseDetails = intent.getSerializableExtra(TOUCH_EXERCISE_DETAILS_KEY) as TouchExerciseDetails

        // Display message
        ui.msgText.text = when(touchExerciseDetails.goalMode) {
            getString(R.string.goal_mode_repetition_key) ->
                "You have completed ${touchExerciseDetails.nReps} repetition(s)"
            getString(R.string.goal_mode_time_limit_key) ->
                "You have completed ${touchExerciseDetails.timeLimitMinutes} minute(s) of the exercise"
            else ->
                "Error loading"
        }

        // Restart button
        ui.restartBtn.setOnClickListener {
            val intent = Intent(this, TouchExerciseGame::class.java)
            intent.putExtra(TOUCH_EXERCISE_DETAILS_KEY, touchExerciseDetails as Serializable)
            startActivity(intent)
        }

        ui.exitBtn.setOnClickListener {
            val intent = Intent(this, TouchExerciseOptions::class.java)
            startActivity(intent)
        }

    }
}