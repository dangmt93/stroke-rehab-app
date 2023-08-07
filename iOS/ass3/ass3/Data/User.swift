//
//  User.swift
//  ass3
//
//  Created by Thomas Dang on 22/5/2022.
//

import Foundation
import Firebase
import FirebaseFirestoreSwift

public struct User : Codable {
    @DocumentID var documentID: String?
    var username: String
    var minutesSpentTouchExercise: Float
    var repsDoneTouchExercise: Int
}
