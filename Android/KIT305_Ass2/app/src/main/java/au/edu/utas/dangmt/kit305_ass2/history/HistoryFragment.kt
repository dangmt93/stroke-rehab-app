package au.edu.utas.dangmt.kit305_ass2.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import au.edu.utas.dangmt.kit305_ass2.databinding.FragmentHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var inflatedView = FragmentHistoryBinding.inflate(layoutInflater, container, false)

//        val now = Calendar.getInstance().time
//        val formatter = SimpleDateFormat.getDateTimeInstance()
//        val formatted = formatter.format(now)
//        Log.d("TIME", formatted.toString())

        // Button functionalities
        inflatedView.touchButton2.setOnClickListener {
            val intent = Intent(this.context, TouchExerciseHistory::class.java)
            startActivity(intent)
        }

        return inflatedView.root
    }
}