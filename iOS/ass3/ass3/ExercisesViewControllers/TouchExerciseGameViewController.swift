//
//  TouchExerciseGameViewController.swift
//  ass3
//
//  Created by Thomas Dang on 22/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class TouchExerciseGameViewController: UIViewController {
    
    let db = Firestore.firestore()
    
    var touchExerciseOptions = ExerciseOptions()
    var buttonList: [UIButton] = [] // Keep track of buttons
    var isClicked: [Bool] = []      // Keep track of clicked buttons
    var repsCount = 0           // Keep track of nReps done
    var record = TouchExerciseRecord()
    var user = User(username: "", minutesSpentTouchExercise: 0.0, repsDoneTouchExercise: 0)
       
    @IBOutlet var headingLabel: UILabel!
    @IBOutlet var guideMessage: UILabel!
    
    @IBOutlet var endGameContainer: UIStackView!
    @IBOutlet var endGameMessage: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print(touchExerciseOptions)
    
        isClicked = Array(repeating: false, count: self.touchExerciseOptions.nButtons)
        
        // Create and add record to database
        addRecord()
        
        // Setup labels
        if touchExerciseOptions.exerciseMode == "freeplay" {
            headingLabel.text = "Free-play"
        }
        else {
            if touchExerciseOptions.goalMode == "repetition" {
                headingLabel.text = "Reps: 0/\(touchExerciseOptions.goalCount!)"
            }
            else {
                // Timer for time limit goal mode
                headingLabel.text = ""
                var remainingSeconds = touchExerciseOptions.goalCount! * 60
//                remainingSeconds = 5  // FOR TESTING
                Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { (Timer) in
                    if remainingSeconds > 0 {
                        let min = (remainingSeconds % 3600) / 60
                        let sec = (remainingSeconds % 3600) % 60
                        let minStr, secStr: String
                        if min < 10 { minStr = "0\(min)" } else { minStr = String(min) }
                        if sec < 10 { secStr = "0\(sec)" } else { secStr = String(sec) }
                        self.headingLabel.text = "Time: \(minStr):\(secStr)"
                        remainingSeconds -= 1
                    }
                    else {
                        Timer.invalidate()
                        print("Timer done")
                        // Hide buttons
                        for button in self.buttonList {
                            button.isHidden = true
                        }
                        // Show End game message
                        self.endGameContainer.isHidden = false
                        self.endGameMessage.text = "You have completed \(self.touchExerciseOptions.goalCount!) minute(s)"
                        self.headingLabel.text = ""
                        self.guideMessage.text = ""
                        
                        // Update database
                        // Check if game is current view (in case user presses back during game)
                        if self.isViewLoaded && (self.view.window != nil) {
                            print("current screen")
                            self.record.endTime = self.getCurrentTime()
                            self.record.isCompleted = true
                            self.updateRecord()
                        }
                    }
                }
            }
        }
        guideMessage.text = "Touch the buttons in order"
    
        // Hide End-game container
        endGameContainer.isHidden = true
        
        // Setup initial buttons
        setupButtons()
        
        // Retrieve User document (for updating "total reps done")
        let userCollection = db.collection("user")
        userCollection.getDocuments() { (result, err) in
            //check for server error
            if let err = err {
                print("Error getting documents: \(err)")
            }
            else {
                //loop through the results
                for document in result!.documents {
                    //attempt to convert to Movie object
                    let conversionResult = Result {
                        try document.data(as: User.self)
                    }

                    // check if conversionResult is success or failure
                    // (i.e. was an exception/error thrown?
                    switch conversionResult {
                        //no problems (but could still be nil)
                        case .success(let user):
                            print("User \(user)")
                            self.user = user
                      
                        case .failure(let error):
                            print("Error decoding document: \(error)")
                    }
                }
            }
        }
    }
        
    func addRecord() {
        var goalMode: String?
        if touchExerciseOptions.exerciseMode == "freeplay" {
            goalMode = nil
        }
        else {
            if touchExerciseOptions.goalMode == "repetition" {
                goalMode = "rep"
            }
            else {
                goalMode = "time"
            }
        }
        
        let recordCollection = db.collection("record")
        record = TouchExerciseRecord(
            startTime: getCurrentTime(),
            endTime: getCurrentTime(),
            exerciseMode: touchExerciseOptions.exerciseMode,
            goalMode: goalMode,
            nReps: 0,
            isCompleted: false,
            buttonPressedList: [],
            nButtons: touchExerciseOptions.nButtons,
            randomOrder: touchExerciseOptions.randomOrder,
            nextButtonIndication: touchExerciseOptions.nextButtonIndication
        )
        
        do {
            let ref = recordCollection.document()
            let id = ref.documentID
            
            try ref.setData(from: record, completion: { [self] (err) in
                if let err = err {
                    print("Error adding document: \(err)")
                }
                else {
                    print("Successfully created record id: \(id)")
                    self.record.documentID = id
                }
            })
        }
        catch let error {
            print("Error writing city to Firestore: \(error)")
        }
    }
    
    func updateRecord() {
        do {
            try self.db.collection("record").document(self.record.documentID!).setData(from: self.record){ err in
                if let err = err {
                    print("Error updating document: \(err)")
                }
                else {
                    print("Record Document \(self.record.documentID!) successfully updated ")
                }
            }
        }
        catch { print("Error updating document \(error)") }
    }
    
    
    func setupButtons() {
        let screenWidth  = UIScreen.main.bounds.width
        let screenHeight = UIScreen.main.bounds.height
//        print("W x H = \(screenWidth) x \(screenHeight)")
        let navBarHeight = 100.0
        let gameAreaHeight = screenHeight - navBarHeight
        
        let btnDiameter = 50.0
                
        let minX1 = 0,
            maxX1 = (screenWidth / 2) - btnDiameter,
            minX2 = screenWidth / 2,
            maxX2 = screenWidth - btnDiameter
        let minY1 = navBarHeight,
            maxY1 = navBarHeight + gameAreaHeight / 3 - btnDiameter,
            minY2 = navBarHeight + gameAreaHeight / 3,
            maxY2 = navBarHeight + 2  * gameAreaHeight / 3 - btnDiameter,
            minY3 = navBarHeight + 2  * gameAreaHeight / 3,
            maxY3 = screenHeight - btnDiameter
        
//        print("X1: \(minX1)...\(maxX1)")
//        print("X2: \(minX2)...\(maxX2)")
//        print("Y1: \(minY1)...\(maxY1)")
//        print("Y2: \(minY2)...\(maxY2)")
//        print("Y3: \(minY3)...\(maxY3)")
        
        // Divide the screen into 6 equal areas and place buttons each in a separate area
        var areas = Array(1...6)
        if touchExerciseOptions.randomOrder {
            areas = areas.shuffled()
        }
        
        buttonList = []    // Empty buttons array
        let buttonLabels = Array(1...touchExerciseOptions.nButtons)
        
        for (index, buttonLabel) in buttonLabels.enumerated() {
//            print("Button \(buttonLabel) is in area \(areas[index])")
            var randomX: Float = 0, randomY: Float = 0
            switch areas[index] {
            case 1:
                randomX = Float.random(in: Float(minX1)...Float(maxX1))
                randomY = Float.random(in: Float(minY1)...Float(maxY1))
            case 2:
                randomX = Float.random(in: Float(minX2)...Float(maxX2))
                randomY = Float.random(in: Float(minY1)...Float(maxY1))
            case 3:
                randomX = Float.random(in: Float(minX1)...Float(maxX1))
                randomY = Float.random(in: Float(minY2)...Float(maxY2))
            case 4:
                randomX = Float.random(in: Float(minX2)...Float(maxX2))
                randomY = Float.random(in: Float(minY2)...Float(maxY2))
            case 5:
                randomX = Float.random(in: Float(minX1)...Float(maxX1))
                randomY = Float.random(in: Float(minY3)...Float(maxY3))
            case 6:
                randomX = Float.random(in: Float(minX2)...Float(maxX2))
                randomY = Float.random(in: Float(minY3)...Float(maxY3))
            default:
                print("Error display button")
            }
            let button = createButton(x_position: randomX, y_position: randomY, diameter: Int(btnDiameter), label: String(buttonLabel))
            buttonList.append(button)
//            print(buttons)
        }
        
        
        
    }
    
    func createButton(x_position: Float, y_position: Float, diameter: Int, label: String) -> UIButton {
        let button = UIButton(frame: CGRect(x: Int(x_position), y: Int(y_position), width: diameter, height: diameter))
        button.backgroundColor = UIColor.systemIndigo
        // Highlight button 1 (if next button indication is ON)
        if touchExerciseOptions.nextButtonIndication && label == "1" {
            button.backgroundColor = UIColor.blue
        }
        
        button.titleLabel?.textColor = UIColor.white
        button.setTitle(label, for: .normal)
        button.layer.cornerRadius = button.frame.width / 2
        button.layer.masksToBounds = true
        button.addTarget(self, action: #selector(self.onClick), for: .touchUpInside)
        view.addSubview(button)
        
        return button
    }
    
    @objc func onClick(button: UIButton) {
        if let buttonTitle = button.title(for: .normal) {
            // If first button || middle button that is supposed to be clicked
            if buttonTitle == "1" || isClicked[Int(buttonTitle)! - 2] {
                isClicked[Int(buttonTitle)! - 1] = true     // set it to clicked
                button.isHidden = true                      // hide button
                guideMessage.text = ""              // remove guiding message (because correct button clicked)
                // Highlight next button if indication is ON
                if touchExerciseOptions.nextButtonIndication && buttonTitle != String(touchExerciseOptions.nButtons) {
                    buttonList[Int(buttonTitle)!].backgroundColor = UIColor.blue
                }
            }
            else {
                let nextButtonLabel = isClicked.firstIndex(where: {$0 == false})! + 1
                guideMessage.text = "Click button \(nextButtonLabel)"
            }
            
            // If last button is clicked when it is supposed to be
            if buttonTitle == String(touchExerciseOptions.nButtons) && isClicked[Int(buttonTitle)! - 2] {
                                
                // Add to rep count
                repsCount += 1
                // Update reps count for record
                record.nReps! += 1
                updateRecord()
                
                // Update user statistics (total reps done)
                if let userId = self.user.documentID {
                    user.repsDoneTouchExercise += 1
                    print("Update total reps done of user \(userId)")
                    do {
                        try self.db.collection("user").document(userId).setData(from: self.user){ err in
                            if let err = err {
                                print("Error updating document: \(err)")
                            }
                            else {
                                print("User Document \(userId) successfully updated ")
                            }
                        }
                    }
                    catch { print("Error updating document \(error)") }
                }
                
                // Update label for "Repetition" goal mode
                if touchExerciseOptions.exerciseMode == "goal" && touchExerciseOptions.goalMode == "repetition" {
                    headingLabel.text = "Reps: \(repsCount)/\(touchExerciseOptions.goalCount!)"
                }
                
                // If finish Reps goal (in "Repetition" goal mode)
                if touchExerciseOptions.exerciseMode == "goal" && touchExerciseOptions.goalMode == "repetition" && repsCount == touchExerciseOptions.goalCount {
                    // Update database (isCompleted)
                    record.isCompleted = true
                    updateRecord()
                    
                    // Show End game message
                    endGameContainer.isHidden = false
                    endGameMessage.text = "You have completed \(touchExerciseOptions.goalCount!) repetition(s)"
                    headingLabel.text = ""
                    guideMessage.text = ""
                }
                else {
                    isClicked = Array(repeating: false, count: self.touchExerciseOptions.nButtons)  // Reset isClicked array
                    setupButtons()
                }
                
            }
            
            // Add button press to database
            let buttonPressed = ButtonPressedRecord(time: getCurrentTime(), buttonPressed: Int(buttonTitle)!)
            record.buttonPressedList?.append(buttonPressed)
            record.endTime = getCurrentTime()
            updateRecord()
        }
                
    }
    

    func getCurrentTime() -> String {
        let date = Date(),
            calendar = Calendar.current,
            day = calendar.component(.day, from: date),
            month = calendar.component(.month, from: date),
            year = calendar.component(.year, from: date),
            hour = calendar.component(.hour, from: date),
            minute = calendar.component(.minute, from: date),
            second = calendar.component(.second, from: date)
    
        let d = addZeroForTime(value: day),
            m = addZeroForTime(value: month),
            y = addZeroForTime(value: year),
            h = addZeroForTime(value: hour),
            min = addZeroForTime(value: minute),
            s = addZeroForTime(value: second)
        
        return "\(y)/\(m)/\(d) \(h):\(min):\(s)"
    }
    
    func addZeroForTime(value: Int) -> String {
        if value < 10 {
            return "0\(value)"
        }
        return String(value)
    }

}
