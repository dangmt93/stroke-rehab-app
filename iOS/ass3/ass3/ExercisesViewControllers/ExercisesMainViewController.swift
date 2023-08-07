//
//  ExercisesMainViewController.swift
//  ass3
//
//  Created by Thomas Dang on 21/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class ExercisesMainViewController: UIViewController {

    let db = Firestore.firestore()
    @IBOutlet var welcomeText: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        setWelcomeMessage()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        setWelcomeMessage()
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    
    func setWelcomeMessage() {
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
//                            print("User \(user)")
                            self.welcomeText.text = "WELCOME BACK, \(user.username)"
                      
                        case .failure(let error):
                            print("Error decoding document: \(error)")
                    }
                }
            }
        }
    }
    
    // TODO
    func setMotivationalQuote() {
        
    }
    
    

}
