//
//  HoldExerciseGameViewController.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit

class HoldExerciseGameViewController: UIViewController {

    var holdExerciseOptions = ExerciseOptions()
    var buttonList: [UIButton] = [] // Keep track of buttons
    var isClicked: [Bool] = []      // Keep track of clicked buttons
    var repsCount = 0           // Keep track of nReps done
    
    @IBOutlet var headingLabel: UILabel!
    @IBOutlet var guideMessage: UILabel!
    
    @IBOutlet var endGameContainer: UIStackView!
    @IBOutlet var endGameMessage: UILabel!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        print(holdExerciseOptions)
    
        isClicked = Array(repeating: false, count: self.holdExerciseOptions.nButtons)
        
        // Setup labels
        if holdExerciseOptions.exerciseMode == "freeplay" {
            headingLabel.text = "Free-play"
        }
        else {
            if holdExerciseOptions.goalMode == "repetition" {
                headingLabel.text = "Reps: 0/\(holdExerciseOptions.goalCount!)"
            }
            else {
                // Timer for time limit goal mode
                headingLabel.text = ""
                var remainingSeconds = holdExerciseOptions.goalCount! * 60
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
                        self.endGameMessage.text = "You have completed \(self.holdExerciseOptions.goalCount!) minute(s)"
                        self.headingLabel.text = ""
                        self.guideMessage.text = ""
                        
                        // Update database

                    }
                }
            }
        }
        guideMessage.text = "Hold buttons for 3s in order"
    
        // Hide End-game container
        endGameContainer.isHidden = true
        
        // Setup initial buttons
        setupButtons()
    
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
        if holdExerciseOptions.randomOrder {
            areas = areas.shuffled()
        }
        
        buttonList = []    // Empty buttons array
        let buttonLabels = Array(1...holdExerciseOptions.nButtons)
        
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
        if holdExerciseOptions.nextButtonIndication && label == "1" {
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
                if holdExerciseOptions.nextButtonIndication && buttonTitle != String(holdExerciseOptions.nButtons) {
                    buttonList[Int(buttonTitle)!].backgroundColor = UIColor.blue
                }
            }
            else {
                let nextButtonLabel = isClicked.firstIndex(where: {$0 == false})! + 1
                guideMessage.text = "Click button \(nextButtonLabel)"
            }
            
            // If last button is clicked when it is supposed to be
            if buttonTitle == String(holdExerciseOptions.nButtons) && isClicked[Int(buttonTitle)! - 2] {
                                
                // Add to rep count
                repsCount += 1
                
                // Update label for "Repetition" goal mode
                if holdExerciseOptions.exerciseMode == "goal" && holdExerciseOptions.goalMode == "repetition" {
                    headingLabel.text = "Reps: \(repsCount)/\(holdExerciseOptions.goalCount!)"
                }
                
                // If finish Reps goal (in "Repetition" goal mode)
                if holdExerciseOptions.exerciseMode == "goal" && holdExerciseOptions.goalMode == "repetition" && repsCount == holdExerciseOptions.goalCount {
                    
                    // Show End game message
                    endGameContainer.isHidden = false
                    endGameMessage.text = "You have completed \(holdExerciseOptions.goalCount!) repetition(s)"
                    headingLabel.text = ""
                    guideMessage.text = ""
                }
                else {
                    isClicked = Array(repeating: false, count: self.holdExerciseOptions.nButtons)  // Reset isClicked array
                    setupButtons()
                }
                
            }
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
