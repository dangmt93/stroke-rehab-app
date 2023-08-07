//
//  StatisticsViewController.swift
//  ass3
//
//  Created by Thomas Dang on 21/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class StatisticsViewController: UIViewController {
    let db = Firestore.firestore()
    @IBOutlet var nRepsLabel: UILabel!
    @IBOutlet var nHoursLabel: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        setDataLabel()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        setDataLabel()
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    func setDataLabel() {
        let userCollection = db.collection("user")
        userCollection.getDocuments() { (result, err) in
            //check for server error
            if let err = err {
                print("Error getting documents: \(err)")
            }
            else {
                //loop through the results
                for document in result!.documents {
                    //attempt to convert to User object
                    let conversionResult = Result {
                        try document.data(as: User.self)
                    }

                    // check if conversionResult is success or failure
                    // (i.e. was an exception/error thrown?
                    switch conversionResult {
                        //no problems (but could still be nil)
                        case .success(let user):
                            self.nRepsLabel.text = String(user.repsDoneTouchExercise)
                            
                            let nHours = user.minutesSpentTouchExercise / 60
                            self.nHoursLabel.text = String(format: "%.2f", nHours) + " hours";
                            
                        case .failure(let error):
                            print("Error decoding document: \(error)")
                    }
                }
            }
        }
    }

}
