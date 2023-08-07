//
//  TouchExerciseRecord.swift
//  ass3
//
//  Created by Thomas Dang on 23/5/2022.
//

import Foundation
import Firebase
import FirebaseFirestoreSwift

public struct TouchExerciseRecord : Codable {
    @DocumentID var documentID: String?
    var startTime: String?
    var endTime: String?
    var exerciseMode: String?
    var goalMode: String?
    var nReps: Int?
    var isCompleted: Bool?
    var buttonPressedList: [ButtonPressedRecord]?
    
    var nButtons: Int?
    var randomOrder: Bool?
    var nextButtonIndication: Bool?
}

struct ButtonPressedRecord : Codable {
    var time: String
    var buttonPressed: Int
}
