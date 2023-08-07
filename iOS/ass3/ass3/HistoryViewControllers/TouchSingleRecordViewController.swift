//
//  TouchSingleRecordViewController.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class TouchSingleRecordViewController: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    @IBOutlet var startTimeLabel: UILabel!
    @IBOutlet var endTimeLabel: UILabel!
    @IBOutlet var modeLabel: UILabel!
    @IBOutlet var statusLabel: UILabel!
    @IBOutlet var nRepsLabel: UILabel!
    @IBOutlet var nButtonsLabel: UILabel!
    @IBOutlet var randomOrderLabel: UILabel!
    @IBOutlet var buttonIndicationLabel: UILabel!
    
    @IBOutlet var imageView: UIImageView!
    
    var record: TouchExerciseRecord?
    var recordIndex: Int?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        if let displayRecord = record {
            var modeText: String
            if displayRecord.exerciseMode == "freeplay" {
                modeText = "Free-play"
            }
            else {
                if displayRecord.goalMode == "time" {
                    modeText = "Goal (Time)"
                }
                else {
                    modeText = "Goal (Repetition)"
                }
            }
            
            var statusText: String
            if displayRecord.exerciseMode == "goal" {
                if displayRecord.isCompleted! {
                    statusText = "Completed ✔"
                }
                else {
                    statusText = "Unfinished ✖"
                }
            }
            else {
                statusText = "━"
            }
            
            startTimeLabel.text = displayRecord.startTime
            endTimeLabel.text = displayRecord.endTime
            modeLabel.text = modeText
            statusLabel.text = statusText
            nRepsLabel.text = String(displayRecord.nReps!)
            nButtonsLabel.text = String(displayRecord.nButtons!)
            if displayRecord.randomOrder! {
                randomOrderLabel.text = "Yes"
            }
            else {
                randomOrderLabel.text = "No"
            }
            if displayRecord.nextButtonIndication! {
                buttonIndicationLabel.text = "Yes"
            }
            else {
                buttonIndicationLabel.text = "No"
            }
        }
    }
    
    
    @IBAction func onDelete(_ sender: UIBarButtonItem) {
        let deleteAlert = UIAlertController(title: "Delete this record?", message: "Do you really want to delete this record? Record will be permernantly deleted.", preferredStyle: UIAlertController.Style.alert)

        deleteAlert.addAction(UIAlertAction(title: "Delete", style: .default, handler: { (action: UIAlertAction!) in
              print("Handle Delete logic here")
            if let thisRecord = self.record {
                print(thisRecord.documentID ?? "")
                
                // Delete record from database
                let db = Firestore.firestore()
                do {
                    // Delete record from database
                    try db.collection("record").document(thisRecord.documentID!).delete() { err in
                        if let err = err {
                            print("Error updating document: \(err)")
                        }
                        else {
                            print("Document \(thisRecord.documentID!) successfully deleted")
                            
                            // Triggers the unwind segue manually
                            self.performSegue(withIdentifier: "deleteRecordSegue", sender: sender)
                        }
                    }
                }
                catch { print("Error deleting document \(error)") }
            }
        }))

        deleteAlert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: { (action: UIAlertAction!) in
              print("Handle Cancel Logic here")
        }))

        present(deleteAlert, animated: true, completion: nil)
        
    }
    
    
    @IBAction func onShare(_ sender: Any) {
        var gameSettingsText = ""
        var buttonPressedText = ""
        
        if let thisRecord = self.record {
            
            // Generate Game Settings details text
            var modeText: String
            if thisRecord.exerciseMode == "freeplay" {
                modeText = "Free-play"
            }
            else {
                if thisRecord.goalMode == "time" {
                    modeText = "Goal (Time)"
                }
                else {
                    modeText = "Goal (Repetition)"
                }
            }
            
            var statusText: String
            if thisRecord.exerciseMode == "goal" {
                if thisRecord.isCompleted! {
                    statusText = "Completed"
                }
                else {
                    statusText = "Unfinished"
                }
            }
            else {
                statusText = "None"
            }
            

            gameSettingsText += "\(thisRecord.startTime!), \(thisRecord.endTime!)"
            gameSettingsText += ", \(modeText), \(statusText), \(thisRecord.nReps!)"
            gameSettingsText += ", \(thisRecord.nButtons!)"
            if thisRecord.randomOrder! {
                gameSettingsText += ", Random Order"
            }
            if thisRecord.nextButtonIndication! {
                gameSettingsText += ", Button Indication"
            }
            
            // Generate Buttons Pressed details text
            for (index, buttonPressed) in thisRecord.buttonPressedList!.enumerated() {
                buttonPressedText += "[\(buttonPressed.time),\(buttonPressed.buttonPressed)]"
                if index != thisRecord.buttonPressedList!.endIndex - 1 {
                    buttonPressedText += ", "
                }
            }
        }
        print(gameSettingsText)
        print(buttonPressedText)
        
        let shareViewController = UIActivityViewController(
            activityItems: [gameSettingsText, buttonPressedText],
            applicationActivities: [])
            
        //add this line to prevent crash on ipad (assumes inside an IBAction)
        shareViewController.popoverPresentationController?.sourceView = sender as? UIView
        
        present(shareViewController, animated: true, completion: nil)
    }
    
    
    @IBAction func onCamera(_ sender: UIBarButtonItem) {
        if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
            print("Photo Library available")
            
            // Setup parameters for ImagePickerController
            let imagePicker = UIImagePickerController()
            imagePicker.delegate = self
            imagePicker.sourceType = .photoLibrary
            imagePicker.allowsEditing = true
            
            // Open Photo Library
            self.present(imagePicker, animated: true, completion: nil)
            
        }
        else {
            print("Photo Library UNavailable")
        }
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
        if let image = info[UIImagePickerController.InfoKey.originalImage] as? UIImage {
            imageView.image = image
            dismiss(animated: true, completion: nil)
        }
    }
    
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
        dismiss(animated: true, completion: nil)
    }
    

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        if segue.identifier == "showButtonPressList" {
            if let buttonPressScreen = segue.destination as? TouchButtonPressTableViewController {
                buttonPressScreen.buttonPressList = (record?.buttonPressedList)!
            }
        }
    }

}
