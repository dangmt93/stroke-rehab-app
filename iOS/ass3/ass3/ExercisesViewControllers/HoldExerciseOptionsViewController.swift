//
//  HoldExerciseOptionsViewController.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit

class HoldExerciseOptionsViewController: UIViewController {

    var holdExerciseOptions = ExerciseOptions()
    let goalModes = ["Repetition", "Time Limit"]
    let goalUnitValues = Array(1...100)
    
    @IBOutlet var exerciseModeSwitch: UISwitch!
    @IBOutlet var goalModeContainer: UIStackView!
    @IBOutlet var goalModePicker: UIPickerView!
    @IBOutlet var goalCountPicker: UIPickerView!
    @IBOutlet var goalTypeLabel: UILabel!
    @IBOutlet var goalUnitLabel: UILabel!
    @IBOutlet var nButtonsSlider: UISlider!
    @IBOutlet var nButtonsLabel: UILabel!
    @IBOutlet var randomOrderSwitch: UISwitch!
    @IBOutlet var nextButtonIndicationSwitch: UISwitch!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        // Set dataSource + Delegate for PickerViews
        goalModePicker.dataSource = self
        goalModePicker.delegate = self
        
        goalCountPicker.dataSource = self
        goalCountPicker.delegate = self
        
        // Set up view values
        nButtonsLabel.text = String(Int(nButtonsSlider.value))
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "goToHoldGameScreen" {
            if let gameScreen = segue.destination as? HoldExerciseGameViewController {
                gameScreen.holdExerciseOptions = holdExerciseOptions
            }
        }
    }
    
    @IBAction func startButtonClicked(_ sender: UIButton) {
//        print("Button Clicked")
//        self.performSegue(withIdentifier: "goToGameScreen", sender: nil)
    }
    
    // Exercise mode Switch functionality
    @IBAction func exerciseModeSwitched(_ sender: Any) {
//        print(exerciseModeSwitch.isOn)
        if exerciseModeSwitch.isOn {
            goalModeContainer.isHidden = true
            holdExerciseOptions.exerciseMode = "freeplay"
        }
        else {
            goalModeContainer.isHidden = false
            holdExerciseOptions.exerciseMode = "goal"
        }
    }
    
    // Action - nButtons slider value changed
    @IBAction func nButtonsSliderChanged(_ sender: Any) {
        let value = Int(nButtonsSlider.value)
        nButtonsLabel.text = String(value)
        holdExerciseOptions.nButtons = value
    }
    
    // Action - Random order switch
    @IBAction func randomOrderSwitched(_ sender: Any) {
        if randomOrderSwitch.isOn {
            holdExerciseOptions.randomOrder = true
        }
        else {
            holdExerciseOptions.randomOrder = false
        }
    }
    
    
    // Action - Next button indication switch
    @IBAction func nextButtonIndicationSwitched(_ sender: Any) {
        if nextButtonIndicationSwitch.isOn {
            holdExerciseOptions.nextButtonIndication = true
        }
        else {
            holdExerciseOptions.nextButtonIndication = false
        }
    }
}


// PickerView delegations -------------------------------
extension HoldExerciseOptionsViewController: UIPickerViewDelegate, UIPickerViewDataSource {
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        var countRows: Int = 0
        if pickerView == goalModePicker {
            countRows = goalModes.count
        }
        else if pickerView == goalCountPicker {
            countRows = goalUnitValues.count
        }
        return countRows
        
        
    }
    
//    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
//        return goalModes[row]
//    }
    
    func pickerView(_ pickerView: UIPickerView, viewForRow row: Int, forComponent component: Int, reusing view: UIView?) -> UIView {
        
        var pickerLabel: UILabel? = (view as? UILabel)
        if pickerLabel == nil {
            pickerLabel = UILabel()
            pickerLabel?.font = UIFont(name: "San Francisco", size: 10)
            pickerLabel?.textAlignment = .center
        }
        if pickerView == goalModePicker {
            pickerLabel?.text = goalModes[row]
        }
        else if pickerView == goalCountPicker {
            pickerLabel?.text = String(goalUnitValues[row])
        }
            
        pickerLabel?.textColor = UIColor.blue

        return pickerLabel!
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        if pickerView == goalModePicker {
            let goalModeSelected = goalModes[row] as String
            print(goalModeSelected)
            if(goalModeSelected == "Repetition") {
                goalTypeLabel.text = "Number of Reps"
                goalUnitLabel.text = "reps"
                holdExerciseOptions.goalMode = "repetition"
            } else {
                goalTypeLabel.text = "Time Limit"
                goalUnitLabel.text = "mins"
                holdExerciseOptions.goalMode = "time"
            }
        }
        else if pickerView == goalCountPicker {
            holdExerciseOptions.goalCount = goalUnitValues[row]
        }
    }
}
