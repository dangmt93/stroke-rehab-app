package au.edu.utas.dangmt.kit305_ass2.data

import java.io.Serializable

data class TouchExerciseRecord (
    var id : String? = null,
    var startTime : String? = null,
    var endTime : String? = null,
    var exerciseMode : String? = null,  // Possible values: "freeplay", "goal"
    var goalMode : String? = null,      // Possible values: "time", "rep"
    var nReps : Int? = null,
    @field:JvmField
    var isCompleted : Boolean? = null,
    var buttonPressedList : MutableList<ButtonPressedRecord>? = null,

    var nButtons : Int? = null,
    var randomOrder : Boolean? = null,
    var nextButtonIndication : Boolean? = null,
    var buttonSize : Float? = null
)

data class ButtonPressedRecord (
    var time : String? = null,
    var buttonPressed : Int? = null
)