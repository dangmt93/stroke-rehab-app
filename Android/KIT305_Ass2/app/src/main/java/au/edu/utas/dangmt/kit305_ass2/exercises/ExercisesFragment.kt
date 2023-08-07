package au.edu.utas.dangmt.kit305_ass2.exercises

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import au.edu.utas.dangmt.kit305_ass2.data.MotivationalQuote
import au.edu.utas.dangmt.kit305_ass2.data.User
import au.edu.utas.dangmt.kit305_ass2.databinding.FragmentExercisesBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

/* CONSTANTS */
const val FIREBASE_TAG = "FirebaseLogging"

/* VARIABLES (global) */
val quoteList = mutableListOf<MotivationalQuote>()

class ExercisesFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflatedView = FragmentExercisesBinding.inflate(layoutInflater, container, false)

        // "Touch" button onClick
        inflatedView.touchButton.setOnClickListener {
            val intent = Intent(this.context, TouchExerciseOptions::class.java)
            startActivity(intent)
        }

        // "Hold" button onClick
        inflatedView.holdButton.setOnClickListener {
            val intent = Intent(this.context, HoldExerciseOptions::class.java)
            startActivity(intent)
        }

        // Connect to Firestore database
        val db = Firebase.firestore

        // Get and display username
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
                var username = userList[0].username?.uppercase()
                Log.d(FIREBASE_TAG, "(From ExercisesFragment) Username: ${username.toString()}")
                inflatedView.welcomeMsg.text = "WELCOME BACK, $username"
            }

        // Motivational quote display
        val quoteCollection = db.collection("motivationalQuotes")
        quoteCollection
            .get()
            .addOnSuccessListener { result ->
                quoteList.clear()

                for(document in result) {
                    val quote = document.toObject<MotivationalQuote>()
                    quoteList.add(quote)
                }

//                Log.d("QUOTE", "Number of quotes found: ${result.size()}")
//                Log.d("QUOTE", "Number of quotes added to local: ${quoteList.size}")

                val randomIndex = Random.nextInt(0, quoteList.size)
                inflatedView.txtMotivationalQuote.text = quoteList[randomIndex].quote
                inflatedView.txtAuthor.text = "- " + quoteList[randomIndex].author
            }

        return inflatedView.root
    }
}