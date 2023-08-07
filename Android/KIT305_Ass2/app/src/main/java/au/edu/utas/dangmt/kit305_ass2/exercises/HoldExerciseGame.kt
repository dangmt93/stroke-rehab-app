package au.edu.utas.dangmt.kit305_ass2.exercises

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.HoldExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.data.HoldExerciseEndGameScreen
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityHoldExerciseGameBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityHoldExerciseOptionsBinding
import java.io.Serializable

const val HOLD_DURATION = 3


class HoldExerciseGame : AppCompatActivity() {
    private lateinit var ui : ActivityHoldExerciseGameBinding
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityHoldExerciseGameBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Object consisting details of the exercise session
        val holdExerciseDetails = intent.getSerializableExtra(HOLD_EXERCISE_DETAILS_KEY) as HoldExerciseDetails
        Log.d("HOLD_EXERCISE_DETAILS",
            "exerciseMode: ${holdExerciseDetails.exerciseMode}, " +
                "nReps: ${holdExerciseDetails.nReps}, " +
                "nButtons: ${holdExerciseDetails.nButtons}, " +
                "buttonSize: ${holdExerciseDetails.buttonSize}")

        // Keep track of number of repetitions done
        var nRepsDone = 0
        var buttonNumber = 1


        /** ---------- UI functionalities and display ---------- **/
        // Menu button
        ui.menuButton.setOnClickListener {
            if(ui.restartButton.visibility == View.GONE) {
                ui.restartButton.visibility = View.VISIBLE
                ui.exitButton.visibility = View.VISIBLE
            }
            else {
                ui.restartButton.visibility = View.GONE
                ui.exitButton.visibility = View.GONE
            }
        }

        // Restart button
        ui.restartButton.setOnClickListener {
            finish()
            startActivity(intent)
        }

        // Exit button
        ui.exitButton.setOnClickListener {
            val intent = Intent(this, HoldExerciseOptions::class.java)
            startActivity(intent)
        }

        // Display exercise mode
        ui.exerciseModeTextView.text = when(holdExerciseDetails.exerciseMode) {
            getString(R.string.exercise_mode_goal_key) -> "Goal mode"
            else -> "Free-play mode"
        }
        ui.repCountTextView.text = if(holdExerciseDetails.exerciseMode == getString(R.string.exercise_mode_goal_key))
            "${nRepsDone}/${holdExerciseDetails.nReps}"
        else "$nRepsDone"




        /** ----------------- Game: Button Hold Functionalities ----------------- **/
        // Setup
        ui.holdButton.text = "1"
        setupHoldButtonOnScreen(ui)

        var second = HOLD_DURATION
        val timer = object: CountDownTimer((1000* HOLD_DURATION).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                ui.timerText.text = second.toString()
                second--
            }
            override fun onFinish() {
                Log.d("TIMER", "Timer finishes")
                // Display next button
                buttonNumber++
                // If finish 1 rep
                if(buttonNumber > holdExerciseDetails.nButtons!!) {
                    buttonNumber = 0
                    nRepsDone++
                    ui.repCountTextView.text = if(holdExerciseDetails.exerciseMode == getString(R.string.exercise_mode_goal_key))
                        "${nRepsDone}/${holdExerciseDetails.nReps}"
                    else "$nRepsDone"
                }
                ui.timerText.text = ""
                ui.holdButton.text = buttonNumber.toString()
                setupHoldButtonOnScreen(ui)

                // If reach goal
                if(nRepsDone == holdExerciseDetails.nReps) {
                    val intent = Intent(ui.root.context, HoldExerciseEndGameScreen::class.java)
                    intent.putExtra(HOLD_EXERCISE_DETAILS_KEY, holdExerciseDetails as Serializable)
                    startActivity(intent)
                }


            }
        }
        ui.holdButton.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                second = HOLD_DURATION
                timer.start()
            }
            if (event.action == MotionEvent.ACTION_UP) {
                timer.cancel()
                second = HOLD_DURATION
                ui.timerText.text = ""
            }
            true
        }

    }

    private fun setupHoldButtonOnScreen(ui : ActivityHoldExerciseGameBinding) {
        /** These constraints are only applicable to Pixel C API 30, Portrait orientation  **/
        val areaConstraints = mapOf(
            "h_min" to 0,
            "h_max" to 750,
            "w_min" to 10,
            "w_max" to 790)

        val randomHeightMargin = (areaConstraints["h_min"]!!..areaConstraints["h_max"]!!).random()
        val randomWidthMargin = (areaConstraints["w_min"]!!..areaConstraints["w_max"]!!).random()
        val buttonParams = ui.holdButton.layoutParams as RelativeLayout.LayoutParams

        // Need to convert dp to px
        // (above Constraints are in dp - but setting Params are in px)
        val randomHeightMarginPx : Int = (randomHeightMargin.times(Resources.getSystem().displayMetrics.density)).toInt()
        val randomWidthMarginPx : Int = (randomWidthMargin.times(Resources.getSystem().displayMetrics.density)).toInt()

        // Set Top and Start (left) constraints
        buttonParams.topMargin = randomHeightMarginPx
        buttonParams.marginStart = randomWidthMarginPx

        Log.d("BUTTON_POSITION", "Hold button - h: $randomHeightMargin, w: $randomWidthMargin")
        Log.d("BUTTON_POSITION", "Hold button in px - h: $randomHeightMarginPx, w: $randomWidthMarginPx")
        ui.holdButton.requestLayout()

        // TODO: Implement button size (scale)
    }
}