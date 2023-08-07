package au.edu.utas.dangmt.kit305_ass2.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import au.edu.utas.dangmt.kit305_ass2.R
import au.edu.utas.dangmt.kit305_ass2.data.TouchExerciseRecord
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseGameBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.ActivityTouchExerciseHistoryBinding
import au.edu.utas.dangmt.kit305_ass2.databinding.TouchExerciseHistoryItemBinding
import au.edu.utas.dangmt.kit305_ass2.exercises.FIREBASE_TAG
import au.edu.utas.dangmt.kit305_ass2.main.MainActivity
import au.edu.utas.dangmt.kit305_ass2.main.TAB_POSITION_KEY
import au.edu.utas.dangmt.kit305_ass2.main.TAB_TITLES
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

const val RECORD_INDEX = "RECORD_INDEX"

val recordList = mutableListOf<TouchExerciseRecord>()

class TouchExerciseHistory : AppCompatActivity() {
    private lateinit var ui : ActivityTouchExerciseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityTouchExerciseHistoryBinding.inflate(layoutInflater)
        setContentView(ui.root)

        // Back button
        ui.backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(TAB_POSITION_KEY, TAB_TITLES.indexOf(R.string.tab_text_history))
            startActivity(intent)
        }

        // Setup adapter and layout manager for RecyclerView
        ui.touchRecordList.adapter = RecordAdapter(records = recordList)
        ui.touchRecordList.layoutManager = LinearLayoutManager(this)    // Vertical list

        //get db connection
        val db = Firebase.firestore

        var recordsCollection = db.collection("history")
        recordsCollection
            .get()
            .addOnSuccessListener { result ->
                //this line clears the list,
                // and prevents a bug where items would be duplicated upon rotation of screen
                recordList.clear()

                Log.d(FIREBASE_TAG, "History list")
                for(document in result) {
                    val record = document.toObject<TouchExerciseRecord>()
                    record.id = document.id

                    //Log.d("BUTTON_PRESSED_LIST", record.buttonPressedList.toString())

                    recordList.add(record)
                }

                // Sort the list to show most recent record first
                recordList.sortByDescending { it.startTime }

                (ui.touchRecordList.adapter as RecordAdapter).notifyDataSetChanged()
                ui.recordCountTextView.text = "${recordList.size} Record(s)"
            }

    }

    // onResume() is called when activity is brought back to the foreground
    //      When a screen is displayed, other screens are brought to the background
    override fun onResume() {
        super.onResume()
        ui.touchRecordList.adapter?.notifyDataSetChanged()
        ui.recordCountTextView.text = "${recordList.size} Record(s)"    // also update text
    }

    inner class RecordHolder(var ui : TouchExerciseHistoryItemBinding) : RecyclerView.ViewHolder(ui.root) {}

    inner class RecordAdapter(private val records : MutableList<TouchExerciseRecord>) : RecyclerView.Adapter<RecordHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
            val ui = TouchExerciseHistoryItemBinding.inflate(layoutInflater, parent, false)
            return RecordHolder(ui)
        }

        override fun onBindViewHolder(holder: RecordHolder, position: Int) {
            val record = records[position]
            holder.ui.startTimeText.text = record.startTime
            holder.ui.endTimeText.text = if(record.endTime == null) "-" else record.endTime
            holder.ui.exerciseModeText.text = when(record.exerciseMode) {
                getString(R.string.exercise_mode_freeplay_key) -> "Free-play"
                getString(R.string.exercise_mode_goal_key) -> {
                    when(record.goalMode) {
                        getString(R.string.goal_mode_repetition_key) -> "Goal (repetition)"
                        getString(R.string.goal_mode_time_limit_key) -> "Goal (time limit)"
                        else -> "ERROR_2"
                    }
                }
                else -> "ERROR_1"
            }
            holder.ui.statusText.text = when(record.isCompleted) {
                true -> "Completed"
                false -> "Incomplete"
                else -> "-"
            }
            holder.ui.nRepsText.text = record.nReps.toString()

            holder.ui.root.setOnClickListener {
                Log.d("RECORD", "A record is clicked: $position")
                val intent = Intent(holder.ui.root.context, TouchExerciseSingleRecord::class.java)
                intent.putExtra(RECORD_INDEX, position)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return records.size
        }

    }
}