package au.edu.utas.dangmt.kit305_ass2.exercises

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources.getSystem
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.ButtonPressedRecord
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseDetails
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseRecord
import au.edu.utas.dangmt.kit305_ass2.data.User
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseGameBinding
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

const val MAX_NO_BUTTONS = 5

class TouchExerciseGame : AppCompatActivity() {
    private lateinit var ui : ActivityTouchExerciseGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTouchExerciseGameBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Object consisting details of the exercise session
        val touchExerciseDetails = intent.getSerializableExtra(TOUCH_EXERCISE_DETAILS_KEY) as TouchExerciseDetails
        Log.d("EXERCISE_DETAILS", "exerciseMode: ${touchExerciseDetails.exerciseMode}, " +
                "goalMode: ${touchExerciseDetails.goalMode}, " +
                "nReps: ${touchExerciseDetails.nReps}, " +
                "timeLimit: ${touchExerciseDetails.timeLimitMinutes}, " +
                "nButtons: ${touchExerciseDetails.nButtons}, " +
                "randomOrder: ${touchExerciseDetails.randomOrder}, " +
                "next-button indication: ${touchExerciseDetails.nextButtonIndication}, " +
                "buttonSize: ${touchExerciseDetails.buttonSize}")

        /** ---------- Create History document on Firestore ---------- **/
        //get db connection
        val db = Firebase.firestore
        val recordsCollection = db.collection("history")

        // To update statistics - Since only 1 user --> to update: use userList[0]
        val userCollection = db.collection("user")
        val userList = mutableListOf<User>()
        userCollection
            .get()
            .addOnSuccessListener { result ->
                //this line clears the list,
                // and prevents a bug where items would be duplicated upon rotation of screen
                userList.clear()
                for(document in result) {
                    val user = document.toObject<User>() // create new "user" instance
                    user.id = document.id
                    userList.add(user)
                }
            }

        // Create history record on database
        val exerciseRecord = TouchExerciseRecord(
            startTime = getCurrentTime(),
            endTime = getCurrentTime(),
            exerciseMode = touchExerciseDetails.exerciseMode,
            goalMode = touchExerciseDetails.goalMode,
            isCompleted = if(touchExerciseDetails.exerciseMode == getString(R.string.exercise_mode_freeplay_key)) null else false,
            nReps = 0,
            buttonPressedList = mutableListOf(),

            nButtons = touchExerciseDetails.nButtons,
            randomOrder = touchExerciseDetails.randomOrder,
            nextButtonIndication = touchExerciseDetails.nextButtonIndication,
            buttonSize = touchExerciseDetails.buttonSize

        )

        Log.d("RECORD", exerciseRecord.toString())
        recordsCollection
            .add(exerciseRecord)
            .addOnSuccessListener {
                Log.d(FIREBASE_TAG, "Document created with id ${it.id}")
                exerciseRecord.id = it.id
            }
            .addOnFailureListener {
                Log.e(FIREBASE_TAG, "Error writing document", it)
            }

        // Timer object for time limit mode
        val timeLimitInMilliseconds = if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_time_limit_key))
            1000 * 60 * touchExerciseDetails.timeLimitMinutes!!
        else 0
        val timer = object: CountDownTimer(timeLimitInMilliseconds.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minute = (millisUntilFinished / 1000) / 60
                val second = (millisUntilFinished / 1000) % 60
                ui.timerTextView.text = "$minute:$second"
            }

            override fun onFinish() {
                Log.d("TIMER", "Timer finishes")
                // Update endTime and isCompleted on database
                exerciseRecord.endTime = getCurrentTime()
                exerciseRecord.isCompleted = true
                recordsCollection.document(exerciseRecord.id!!)
                    .set(exerciseRecord)
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Document updated with id ${exerciseRecord.id}")
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating document", it)
                    }

                // Go to end screen
                val intent = Intent(ui.root.context, TouchExerciseEndGameScreen::class.java)
                intent.putExtra(TOUCH_EXERCISE_DETAILS_KEY, touchExerciseDetails as Serializable)
                startActivity(intent)
            }
        }


        /** ---------- UI functionalities and display ---------- **/
        ui.msgTextView.text = "Touch the buttons in order"

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
            timer.cancel()
            val intent = Intent(this, TouchExerciseOptions::class.java)
            startActivity(intent)
        }

        // Display exercise mode
        ui.exerciseModeTextView.text = when(touchExerciseDetails.exerciseMode) {
            getString(R.string.exercise_mode_goal_key) -> {
                when(touchExerciseDetails.goalMode) {
                    getString(R.string.goal_mode_time_limit_key) -> "Goal mode (time limit)"
                    getString(R.string.goal_mode_repetition_key) -> "Goal mode (repetitions)"
                    else -> ""
                }
            }
            else -> "Free-play mode"
        }
        ui.repCountTextView.text = if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_repetition_key))
            "${exerciseRecord.nReps}/${touchExerciseDetails.nReps}"
        else "${exerciseRecord.nReps}"

        // Display timer for time limit goal mode
        if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_time_limit_key)) {
            ui.timerSection.visibility = View.VISIBLE
            timer.start()
        }


        // Display touch buttons
        setupButtonsOnScreen(ui, touchExerciseDetails)


        /** ----------------- Game: Button Clicked Functionalities ----------------- **/
        // Array of "visible" buttons on screen
        var buttonList = mutableListOf(ui.button1, ui.button2, ui.button3, ui.button4, ui.button5)
        buttonList = buttonList.dropLast(MAX_NO_BUTTONS - touchExerciseDetails.nButtons!!) as MutableList<MaterialButton>

        // Array to keep track of buttons clicked
        var isClicked = Array(touchExerciseDetails.nButtons!!) { false }

        buttonList.forEachIndexed { index, button ->
            button.setOnClickListener {
                /** If first button or middle button that is supposed to be clicked **/
                if(button == buttonList.first() || isClicked[index-1]) {
                    isClicked[index] = true         // set it to clicked
                    button.visibility = View.GONE   // "hide" button
                    ui.msgTextView.text = ""        // remove msg because correct button click
                    // Highlight next button if indication is turned on
                    if(button != buttonList.last() && touchExerciseDetails.nextButtonIndication == true) {
                        buttonList[index + 1].backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.purple_500))
                    }
                }
                else {
                    // Find index of the first un-clicked button (the name of the button will be +1) (i.e. next-button)
                    val nextButton = isClicked.indexOfFirst { x -> !x } + 1
                    ui.msgTextView.text = getString(R.string.touch_guide_msg, nextButton)

                }

                /** If the last button is clicked when it is supposed to be **/
                if(button == buttonList.last() && isClicked[index-1]) {
                    isClicked = Array(touchExerciseDetails.nButtons!!) { false }
                    setupButtonsOnScreen(ui, touchExerciseDetails)

                    // Add to rep count and update view
                    exerciseRecord.nReps = exerciseRecord.nReps?.plus(1)
                    ui.repCountTextView.text =
                        if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_repetition_key))
                            "${exerciseRecord.nReps}/${touchExerciseDetails.nReps}"
                        else "${exerciseRecord.nReps}"

                    // Also update statistics on database
                    userList[0].repsDoneTouchExercise = userList[0].repsDoneTouchExercise?.plus(1)
                    userCollection.document(userList[0].id!!)
                        .set(userList[0])
                        .addOnSuccessListener {
                            Log.d(FIREBASE_TAG, "Stats (user) updated with id ${userList[0].id}")
                        }
                        .addOnFailureListener {
                            Log.e(FIREBASE_TAG, "Error updating document", it)
                        }

                    /** Implement Repetition Goal mode **/
                    if(touchExerciseDetails.goalMode == getString(R.string.goal_mode_repetition_key) && exerciseRecord.nReps == touchExerciseDetails.nReps) {
                        // update isCompleted on Firebase
                        exerciseRecord.isCompleted = true
                        recordsCollection.document(exerciseRecord.id!!)
                            .set(exerciseRecord)
                            .addOnSuccessListener {
                                Log.d(FIREBASE_TAG, "Exercise record updated with id ${exerciseRecord.id}")
                            }
                            .addOnFailureListener {
                                Log.e(FIREBASE_TAG, "Error updating document", it)
                            }

                        // Go to end game screen
                        val intent = Intent(this, TouchExerciseEndGameScreen::class.java)
                        intent.putExtra(TOUCH_EXERCISE_DETAILS_KEY, touchExerciseDetails as Serializable)
                        startActivity(intent)
                    }
                }

                /** Record button press and add to database **/
                exerciseRecord.buttonPressedList?.add(ButtonPressedRecord(
                    time = getCurrentTime(),
                    buttonPressed = index + 1))
                exerciseRecord.endTime = getCurrentTime()   // Also update endTime
                recordsCollection.document(exerciseRecord.id!!)
                    .set(exerciseRecord)
                    .addOnSuccessListener {
                        Log.d(FIREBASE_TAG, "Document updated with id ${exerciseRecord.id}")
                    }
                    .addOnFailureListener {
                        Log.e(FIREBASE_TAG, "Error updating document", it)
                    }
            }
        }

        // TODO: Implement adding total time spent to statistics (Not part of requirement)
    }


    private fun setupButtonsOnScreen(ui : ActivityTouchExerciseGameBinding, exerciseDetails : TouchExerciseDetails) {
        // Array of "visible" buttons on screen
        var buttonList = mutableListOf(ui.button1, ui.button2, ui.button3, ui.button4, ui.button5)
        buttonList = buttonList.dropLast(MAX_NO_BUTTONS - exerciseDetails.nButtons!!) as MutableList<MaterialButton>

        // Display all the buttons that should be displayed
        for(button in buttonList) {
            button.visibility = View.VISIBLE
            button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.purple_200))
            if(button == buttonList.first() && exerciseDetails.nextButtonIndication == true) {
                button.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.purple_500))
            }
        }

        /** Hide buttons based on number of buttons **/
        if(exerciseDetails.nButtons == 3) {
            ui.button4.visibility = View.GONE
            ui.button5.visibility = View.GONE
        }
        else if(exerciseDetails.nButtons == 4) {
            ui.button5.visibility = View.GONE
        }

        // IDEA: Divide the screen into 6 equal areas
        //          1. Put 5 buttons in 5/6 of those areas (no 2 buttons in the same area)
        //          2. "Hide" buttons according to noButtons
        /** These constraints are only applicable to Pixel C API 30, Portrait orientation  **/
        val areaMargins = arrayOf(
            mapOf("height_min" to 0, "height_max" to 170, "width_min" to 10, "width_max" to 340),
            mapOf("height_min" to 0, "height_max" to 170, "width_min" to 460, "width_max" to 790),
            mapOf("height_min" to 290, "height_max" to 450, "width_min" to 10, "width_max" to 340),
            mapOf("height_min" to 290, "height_max" to 450, "width_min" to 460, "width_max" to 790),
            mapOf("height_min" to 570, "height_max" to 750, "width_min" to 10, "width_max" to 340),
            mapOf("height_min" to 570, "height_max" to 750, "width_min" to 460, "width_max" to 790)
        )

        // Array to track if an area (6 areas in total) is empty
        val emptyAreas = Array(6) { true }

        /* For each button
        *   1. Select random area that is empty (empty = true)
        *   2. Place button into that empty area
        *   3. Set that empty area to false
        */
        /** Random Order **/
        if(exerciseDetails.randomOrder == true) {
            for(button in buttonList) {
                // Select a random empty area
                val filteredEmpty = emptyAreas.withIndex().filter { it.value }.map { it.index }
                val randomEmptyAreaIndex = filteredEmpty.random()

                // Place button in the selected area
                val hMin = areaMargins[randomEmptyAreaIndex]["height_min"]
                val hMax = areaMargins[randomEmptyAreaIndex]["height_max"]
                val wMin = areaMargins[randomEmptyAreaIndex]["width_min"]
                val wMax = areaMargins[randomEmptyAreaIndex]["width_max"]
                val randomHeightMargin = (hMax?.let { hMin?.rangeTo(it) })?.random()
                val randomWidthMargin = (wMax?.let { wMin?.rangeTo(it) })?.random()
                Log.d("BUTTON_POSITION", "$button in area $randomEmptyAreaIndex: h=$randomHeightMargin, w=$randomWidthMargin")

                val buttonParams = button.layoutParams as ConstraintLayout.LayoutParams

                // Need to convert dp to px
                // (above Constraints are in dp - but setting Params are in px)
                val randomHeightMarginPx : Int = (randomHeightMargin?.times(getSystem().displayMetrics.density))!!.toInt()
                val randomWidthMarginPx : Int = (randomWidthMargin?.times(getSystem().displayMetrics.density))!!.toInt()

                // Set Top and Start (left) constraints
                buttonParams.topMargin = randomHeightMarginPx
                buttonParams.marginStart = randomWidthMarginPx
                button.requestLayout()

                // Set area to occupied
                emptyAreas[randomEmptyAreaIndex] = false
            }
        }
        /** NOT Random Order **/
        else {
            buttonList.forEachIndexed { index, button ->
                val hMin = areaMargins[index]["height_min"]
                val hMax = areaMargins[index]["height_max"]
                val wMin = areaMargins[index]["width_min"]
                val wMax = areaMargins[index]["width_max"]
                val randomHeightMargin = (hMax?.let { hMin?.rangeTo(it) })?.random()
                val randomWidthMargin = (wMax?.let { wMin?.rangeTo(it) })?.random()

                val buttonParams = button.layoutParams as ConstraintLayout.LayoutParams

                // Need to convert dp to px
                // (above Constraints are in dp - but setting Params are in px)
                val randomHeightMarginPx : Int = (randomHeightMargin?.times(getSystem().displayMetrics.density))!!.toInt()
                val randomWidthMarginPx : Int = (randomWidthMargin?.times(getSystem().displayMetrics.density))!!.toInt()

                // Set Top and Start (left) constraints
                buttonParams.topMargin = randomHeightMarginPx
                buttonParams.marginStart = randomWidthMarginPx
                button.requestLayout()
            }
        }


        // TODO: Implement button size (scale)


    }

    private fun getCurrentTime() : String {
        val timeNow = Calendar.getInstance().time
//        val formatter = SimpleDateFormat.getDateTimeInstance()
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm:ss")
        return formatter.format(timeNow)
    }

}