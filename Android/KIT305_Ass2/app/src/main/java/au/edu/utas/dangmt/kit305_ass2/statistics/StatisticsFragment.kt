package au.edu.utas.dangmt.kit305_ass2.statistics

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import au.edu.utas.dangmt.kit305_ass2.data.User
import au.edu.utas.dangmt.kit305_ass2.databinding.FragmentStatisticsBinding
import au.edu.utas.dangmt.kit305_ass2.exercises.FIREBASE_TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.math.roundToInt

class StatisticsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var inflatedView = FragmentStatisticsBinding.inflate(layoutInflater, container, false)

        // Connect to Firestore database
        val db = Firebase.firestore

        // Display values
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

                // Since there is only 1 user --> userList[0]
                // Total time spent on Touch exercise (converted to hours)
                Log.d(FIREBASE_TAG, "(From StatisticsFragment) Minute Spent on Touch Exercise:${userList[0].minutesSpentTouchExercise.toString()}")
                val hoursSpentTouchExercise : Double = userList[0].minutesSpentTouchExercise!! / 60.0
                val twoDecimalsResult = (hoursSpentTouchExercise * 100.0).roundToInt() / 100.0
                inflatedView.timeSpentTouchTextView.text = twoDecimalsResult.toString()

                // Total reps done in Touch exercise
                inflatedView.repsDoneTouchTextView.text = userList[0].repsDoneTouchExercise.toString()
            }

        // Share stats button
        inflatedView.touchShareButton.setOnClickListener {
            val textToShare =   "${userList[0].minutesSpentTouchExercise}," +
                                "${userList[0].repsDoneTouchExercise}"
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, textToShare)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Share via..."))
        }

        return inflatedView.root
    }
}