package au.edu.utas.dangmt.kit305_ass2.exercises

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.HoldExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityHoldExerciseOptionsBinding
import au.edu.utas.dangmt.kit305_ass2.main.MainActivity
import au.edu.utas.dangmt.kit305_ass2.main.TAB_POSITION_KEY
import au.edu.utas.dangmt.kit305_ass2.main.TAB_TITLES
import java.io.Serializable

/* Intent KEYS */
const val HOLD_EXERCISE_DETAILS_KEY : String ="HOLD_EXERCISE_DETAILS"

// This object keeps track of exercise options
val holdExerciseDetails = HoldExerciseDetails()

class HoldExerciseOptions : AppCompatActivity() {
    private lateinit var ui : ActivityHoldExerciseOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityHoldExerciseOptionsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /** Populate options based on previous options **/
        Log.d("HOLD_GAME_OPTIONS", "Options object: $touchExerciseDetails")
        if(holdExerciseDetails.exerciseMode != null) {
            // Mode
            if(holdExerciseDetails.exerciseMode == getString(R.string.exercise_mode_freeplay_key)) {
                ui.freeplayModeSwitchHold.isChecked = true
                ui.constraintLayoutGoalMode.visibility = View.GONE
            }
            // Customisation
            ui.nButtonsSliderHold.value = holdExerciseDetails.nButtons!!.toFloat()
            ui.buttonSizeSliderHold.value = holdExerciseDetails.buttonSize!!
        }

        /** --------------------------------- Functionalities --------------------------------- **/
        // backButton
        ui.backButtonHold.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(TAB_POSITION_KEY, TAB_TITLES.indexOf(R.string.tab_text_exercises))
            startActivity(intent)
        }

        // free-play mode switch
        ui.freeplayModeSwitchHold.setOnClickListener {
            if(ui.freeplayModeSwitchHold.isChecked) {
                ui.constraintLayoutGoalMode.visibility = View.GONE
                holdExerciseDetails.exerciseMode = getString(R.string.exercise_mode_freeplay_key)
            }
            else {
                ui.constraintLayoutGoalMode.visibility = View.VISIBLE
                holdExerciseDetails.exerciseMode = getString(R.string.exercise_mode_goal_key)
            }
        }

        // Number of repetitions NumberPicker
        ui.nRepsNumberPickerHold.maxValue = 10
        ui.nRepsNumberPickerHold.minValue = 1
        ui.nRepsNumberPickerHold.setOnValueChangedListener { picker, oldVal, newVal ->
            val text = "Changed from $oldVal to $newVal"
            Log.d("NUMBER_PICKER", text)
            holdExerciseDetails.nReps = newVal
        }

        // Number of buttons Slider
        ui.nButtonsSliderHold.addOnChangeListener {slider, value, fromUser ->
            holdExerciseDetails.nButtons = value.toInt()
        }

        // Button Size Slider
        ui.buttonSizeSliderHold.addOnChangeListener {slider, value, fromUser ->
            holdExerciseDetails.buttonSize = value
        }

        // START button
        ui.startButtonHold.setOnClickListener {
            /** Retrieve values **/
            // exercise mode
            holdExerciseDetails.exerciseMode = when(ui.freeplayModeSwitchHold.isChecked) {
                true -> getString(R.string.exercise_mode_freeplay_key)
                else -> getString(R.string.exercise_mode_goal_key)
            }

            // Number of reps
            holdExerciseDetails.nReps = if(holdExerciseDetails.exerciseMode == getString(R.string.exercise_mode_goal_key))
                ui.nRepsNumberPickerHold.value
            else null

            // Number of buttons
            holdExerciseDetails.nButtons = ui.nButtonsSliderHold.value.toInt()

            // Button size
            holdExerciseDetails.buttonSize = ui.buttonSizeSliderHold.value

            // Send details to Hold Game screen
            Log.d("INTENT_HOLD", holdExerciseDetails.toString())
            val intent = Intent(this, HoldExerciseGame::class.java)
            intent.putExtra(HOLD_EXERCISE_DETAILS_KEY, holdExerciseDetails as Serializable)
            startActivity(intent)

        }
    }
}