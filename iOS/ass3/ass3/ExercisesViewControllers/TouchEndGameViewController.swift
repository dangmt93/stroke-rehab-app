//
//  TouchEndGameViewController.swift
//  ass3
//
//  Created by Thomas Dang on 23/5/2022.
//

import UIKit

class TouchEndGameViewController: UIViewController {

    @IBOutlet var messageLabel: UILabel!
    var goalMode: String?
    var goalCount: Int?
    override func viewDidLoad() {
        super.viewDidLoad()
        var message = "You have completed \(goalCount!) "
        if goalMode == "repetition" {
            message += "repetitions"
        }
        else {
            message += "minutes"
        }
        messageLabel.text = message
        
        // Do any additional setup after loading the view.
        
    }
    
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
