package au.edu.utas.dangmt.kit305_ass2.exercises

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView

import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseOptionsBinding
import au.edu.utas.dangmt.kit305_ass2.main.MainActivity
import au.edu.utas.dangmt.kit305_ass2.main.TAB_POSITION_KEY
import au.edu.utas.dangmt.kit305_ass2.main.TAB_TITLES
import java.io.Serializable

/* Intent KEYS */
const val TOUCH_EXERCISE_DETAILS_KEY : String ="TOUCH_EXERCISE_DETAILS"

// This object keeps track of exercise options
val touchExerciseDetails = TouchExerciseDetails()

class TouchExerciseOptions : AppCompatActivity() {
    private lateinit var ui : ActivityTouchExerciseOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTouchExerciseOptionsBinding.inflate(layoutInflater)
        setContentView(ui.root)

        /** Populate options based on previous options **/
        Log.d("TOUCH_GAME_OPTIONS", "Options object: $touchExerciseDetails")
        // If exerciseMode != null, then START button has been pressed
        if(touchExerciseDetails.exerciseMode != null) {
            // Mode
            if(touchExerciseDetails.exerciseMode == getString(R.string.exercise_mode_freeplay_key)) {
                ui.freeplayModeSwitch.isChecked = true
                ui.constraintLayoutGoalMode.visibility = View.GONE
            }
            else {
                if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_repetition_key)) {
                    ui.goalModeSpinner.setSelection(0)
                    ui.nRepsNumberPicker.value = touchExerciseDetails.nReps!!
                    // TODO: Set NumberPicker value does not work. Find a way
                }
                else {
                    ui.goalModeSpinner.setSelection(1)
                    ui.minuteNumberPicker.value = touchExerciseDetails.timeLimitMinutes!!
                }
            }

            // Customisation
            ui.nButtonsSlider.value = touchExerciseDetails.nButtons!!.toFloat()
            ui.randomOrderSwitch.isChecked = touchExerciseDetails.randomOrder!!
            ui.nextButtonIndicationSwitch.isChecked = touchExerciseDetails.nextButtonIndication!!
            ui.buttonSizeSlider.value = touchExerciseDetails.buttonSize!!
        }


        /** --------------------------------- Functionalities --------------------------------- **/
        // backButton
        ui.backButtonTouch.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(TAB_POSITION_KEY, TAB_TITLES.indexOf(R.string.tab_text_exercises))
            startActivity(intent)
        }

        // free-play mode switch
        ui.freeplayModeSwitch.setOnClickListener {
            if(ui.freeplayModeSwitch.isChecked) {
                ui.constraintLayoutGoalMode.visibility = View.GONE
                touchExerciseDetails.exerciseMode = getString(R.string.exercise_mode_freeplay_key)
            }
            else {
                ui.constraintLayoutGoalMode.visibility = View.VISIBLE
                touchExerciseDetails.exerciseMode = getString(R.string.exercise_mode_goal_key)
            }
        }

        // goal mode spinner
        ui.goalModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                //Log.d("SPINNER", selectedItem)

                // display mode accordingly
                if(selectedItem == "Repetition") {
                    ui.noRepConstraintLayout.visibility = View.VISIBLE
                    ui.timeLimitConstraintLayout.visibility = View.GONE
                    touchExerciseDetails.goalMode = "rep"
                }
                else {
                    ui.noRepConstraintLayout.visibility = View.GONE
                    ui.timeLimitConstraintLayout.visibility = View.VISIBLE
                    touchExerciseDetails.goalMode = "time"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // Number of repetitions NumberPicker
        ui.nRepsNumberPicker.maxValue = 10
        ui.nRepsNumberPicker.minValue = 1
        ui.nRepsNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val text = "Changed from $oldVal to $newVal"
            Log.d("NUMBER_PICKER", text)
            touchExerciseDetails.nReps = newVal
        }

        // Time limit NumberPickers
        ui.minuteNumberPicker.maxValue = 60
        ui.minuteNumberPicker.minValue = 1
        ui.minuteNumberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            val text = "Changed from $oldVal to $newVal"
            Log.d("MINUTE_PICKER", text)
            touchExerciseDetails.timeLimitMinutes = newVal
        }

        // Number of buttons Slider
        ui.nButtonsSlider.addOnChangeListener {slider, value, fromUser ->
            touchExerciseDetails.nButtons = value.toInt()
        }

        // Random order Switch
        ui.randomOrderSwitch.setOnClickListener {
            touchExerciseDetails.randomOrder = ui.randomOrderSwitch.isChecked
        }

        // Next-button indication Switch
        ui.nextButtonIndicationSwitch.setOnClickListener {
            touchExerciseDetails.nextButtonIndication = ui.nextButtonIndicationSwitch.isChecked
        }

        // Button Size Slider
        ui.buttonSizeSlider.addOnChangeListener {slider, value, fromUser ->
            touchExerciseDetails.buttonSize = value
        }

        // START button
        ui.startButtonTouch.setOnClickListener {
            /** Retrieve values **/
            // exerciseMode
            touchExerciseDetails.exerciseMode = when(ui.freeplayModeSwitch.isChecked) {
                true -> getString(R.string.exercise_mode_freeplay_key)
                else -> getString(R.string.exercise_mode_goal_key)
            }
            // goalMode
            if(touchExerciseDetails.exerciseMode == "goal") {
                touchExerciseDetails.goalMode = when(ui.goalModeSpinner.getSelectedItem().toString()) {
                    "Repetition" -> getString(R.string.goal_mode_repetition_key)
                    "Time limit" -> getString(R.string.goal_mode_time_limit_key)
                    else -> null
                }
            } else {
                touchExerciseDetails.goalMode = null
            }

            // Goal (nReps / time limit)
            touchExerciseDetails.nReps = if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_repetition_key))
                ui.nRepsNumberPicker.value
            else null

            touchExerciseDetails.timeLimitMinutes = if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_time_limit_key))
                ui.minuteNumberPicker.value
            else null

            // Customisation details
            touchExerciseDetails.nButtons = ui.nButtonsSlider.value.toInt()
            touchExerciseDetails.randomOrder = when(ui.randomOrderSwitch.isChecked) {
                true -> true
                else -> false
            }
            touchExerciseDetails.nextButtonIndication = when(ui.nextButtonIndicationSwitch.isChecked) {
                true -> true
                else -> false
            }
            touchExerciseDetails.buttonSize = ui.buttonSizeSlider.value

            // Send details to Touch Game screen
            val intent = Intent(this, TouchExerciseGame::class.java)
            intent.putExtra(TOUCH_EXERCISE_DETAILS_KEY, touchExerciseDetails as Serializable)
            startActivity(intent)
        }
    }
}
