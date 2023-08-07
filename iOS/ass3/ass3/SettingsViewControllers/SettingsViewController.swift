//
//  SettingsViewController.swift
//  ass3
//
//  Created by Thomas Dang on 21/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class SettingsViewController: UIViewController {
    let db = Firestore.firestore()
    var userObject = User(username: "",
                          minutesSpentTouchExercise: 0.0,
                          repsDoneTouchExercise: 0)
    
    @IBOutlet var usernameInput: UITextField!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        fillUsername()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        fillUsername()
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    func fillUsername() {
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
//                            print("User \(user)")
                            self.userObject = user  // Fill data in userObject
                            self.usernameInput.text = user.username
                      
                        case .failure(let error):
                            print("Error decoding document: \(error)")
                    }
                }
            }
        }
    }
    

    @IBAction func usernameEntered(_ sender: Any) {
        if(usernameInput.text! != "") {
            usernameInput.resignFirstResponder()
            print("Entered \(usernameInput.text!)")
            
            // Update username
            let userCollection = db.collection("user")
            userCollection.document(userObject.documentID!).updateData([
                "username": usernameInput.text!
            ]) { (err) in
                    if let err = err {
                        print("Error updating documents: \(err)")
                    } else {
                        print("Document updated!")
                    }
                }

            }

            
    }
        
}
    
    
