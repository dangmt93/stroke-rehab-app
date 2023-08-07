//
//  HoldExerciseOptions.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import Foundation

public struct HoldExerciseOptions {
    var exerciseMode: String    // freeplay or goal
    var goalMode: String?       // null or repetition or time
    var goalCount: Int?         // count of reps / minutes
    var nButtons: Int           // values: 3, 4, 5
    var randomOrder: Bool
    var nextButtonIndication: Bool
    
    // Initial selected options
    init() {
        exerciseMode = "goal"
        goalMode = "repetition"
        goalCount = 1
        nButtons = 3
        randomOrder = true
        nextButtonIndication = true
    }
}
