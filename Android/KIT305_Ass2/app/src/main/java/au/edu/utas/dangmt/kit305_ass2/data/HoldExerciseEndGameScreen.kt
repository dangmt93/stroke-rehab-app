package au.edu.utas.dangmt.kit305_ass2.data

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityHoldExerciseEndGameScreenBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseEndGameScreenBinding
import au.edu.utas.dangmt.kit305_ass2.exercises.*
import java.io.Serializable

class HoldExerciseEndGameScreen : AppCompatActivity() {
    private lateinit var ui : ActivityHoldExerciseEndGameScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityHoldExerciseEndGameScreenBinding.inflate(layoutInflater)
        setContentView(ui.root)

        val holdExerciseDetails = intent.getSerializableExtra(HOLD_EXERCISE_DETAILS_KEY) as HoldExerciseDetails

        // Display message
        ui.msgText.text = "You have completed ${holdExerciseDetails.nReps} repetitions"

        // Restart button
        ui.restartBtn.setOnClickListener {
            val intent = Intent(this, HoldExerciseGame::class.java)
            intent.putExtra(HOLD_EXERCISE_DETAILS_KEY, holdExerciseDetails as Serializable)
            startActivity(intent)
        }

        ui.exitBtn.setOnClickListener {
            val intent = Intent(this, HoldExerciseOptions::class.java)
            startActivity(intent)
        }
    }
}