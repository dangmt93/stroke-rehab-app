package au.edu.utas.dangmt.kit305_ass2.data

import android.os.Parcelable
import java.io.Serializable

data class TouchExerciseDetails(
    var exerciseMode : String? = null,
    var goalMode : String? = null,
    var nReps : Int? = null,
    var timeLimitMinutes : Int? = null,
    var nButtons : Int? = null,             // Possible values: 3, 4, 5
    var randomOrder : Boolean? = null,
    var nextButtonIndication : Boolean? = null,
    var buttonSize : Float? = null
) : Serializable