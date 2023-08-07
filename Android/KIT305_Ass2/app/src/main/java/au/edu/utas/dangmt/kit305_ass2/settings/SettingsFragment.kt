package au.edu.utas.dangmt.kit305_ass2.settings

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import au.edu.utas.dangmt.kit305_ass2.data.User
import au.edu.utas.dangmt.kit305_ass2.databinding.FragmentSettingsBinding
import au.edu.utas.dangmt.kit305_ass2.exercises.FIREBASE_TAG
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var inflatedView = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        // Connect to Firestore database
        val db = Firebase.firestore

        // Display current username
        val userCollection = db.collection("user")
        userCollection.document("user1")
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject<User>() // create new "user" instance
                if (user != null) {

                }
            }
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

                // Since there is only 1 user
                Log.d(FIREBASE_TAG, "(From SettingsFragment) Username: ${userList[0].username.toString()}")
                inflatedView.editTextUsername.setText(userList[0].username)
            }

        // Update username
        var username : String? = null;
        inflatedView.editTextUsername.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                //Perform Code
                username = inflatedView.editTextUsername.text.toString()
                Log.d("EDIT_TEXT", "User enters $username")

                if(username != null) {
                    Log.d("USERNAME", "Username is NOT null")
                    userList[0].id?.let {
                        userCollection.document(it)
                            .update("username", username)
                            .addOnSuccessListener {
                                Log.d(FIREBASE_TAG, "Successfully update username $username")
                            }
                            .addOnFailureListener { e -> Log.w(FIREBASE_TAG, "Error updating document", e)
                            }
                    }
                }
                else {
                    Log.d("USERNAME", "Username is null")
                }
                return@OnKeyListener true
            }
            false
        })

        return inflatedView.root
    }
}