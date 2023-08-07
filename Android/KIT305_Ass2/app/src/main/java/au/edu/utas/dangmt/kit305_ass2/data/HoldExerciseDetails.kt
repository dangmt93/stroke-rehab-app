package au.edu.utas.dangmt.kit305_ass2.data

import java.io.Serializable

data class HoldExerciseDetails(
    var exerciseMode : String? = null,
    var nReps : Int? = null,
    var nButtons : Int? = null,             // Possible values: 3, 4, 5
    var buttonSize : Float? = null
) : Serializable