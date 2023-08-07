//
//  TouchRecordTableViewController.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit
import Firebase
import FirebaseFirestoreSwift

class TouchRecordTableViewController: UITableViewController {

    var recordList = [TouchExerciseRecord]()
    let db = Firestore.firestore()
    override func viewDidLoad() {
        super.viewDidLoad()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem
        
        populateRecordTable()
        
    }
    
    
    func populateRecordTable() {
        // empty recordList first
        recordList = []
        
        // Get data from database to add to recordList
        let recordCollection = db.collection("record")
        recordCollection.getDocuments() { (result, err) in
            if let err = err {
                print("Error getting documents: \(err)")
            }
            else {
                for document in result!.documents {
                    let conversionResult = Result {
                        try document.data(as: TouchExerciseRecord.self)
                    }
                    switch conversionResult {
                        case .success(let record):
//                            print("Record: \(record)")
                                
                            // Add record to array of records
                            self.recordList.append(record)
                            
                        case .failure(let error):
                            // A `Record` value could not be initialized from the DocumentSnapshot.
                            print("Error decoding record: \(error)")
                    }
                }
                
                // Sort based on startTime + Load data to table
                self.recordList.sort(by: {$0.startTime! > $1.startTime!})
                self.tableView.reloadData()
            }
        }
    }

    
    
    // MARK: - Table view data source
    override func numberOfSections(in tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        return recordList.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "TouchRecordTableViewCell", for: indexPath)

        // Configure the cell...
        // get record for this row and fill data in the row
        let record = recordList[indexPath.row]
        
        if let recordCell = cell as? TouchRecordTableViewCell {
            var modeText = ""
            if record.exerciseMode == "freeplay" {
                modeText = "Free-play"
            }
            else {
                if record.goalMode == "time" {
                    modeText = "Goal (Time)"
                }
                else {
                    modeText = "Goal (Repetition)"
                }
            }
            
            var settingsText = "\(record.nButtons!) buttons"
            if record.randomOrder! {
                settingsText += ", Random Order"
            }
            if record.nextButtonIndication! {
                settingsText += ", Button Indication"
            }
            
            var statusText: String
            if record.exerciseMode == "goal" {
                if record.isCompleted! {
                    statusText = "Completed ✔"
                }
                else {
                    statusText = "Unfinished ✖"
                }
            }
            else {
                statusText = "━"
            }
            
            recordCell.startLabel.text = record.startTime
            recordCell.endLabel.text = record.endTime
            recordCell.modeLabel.text = modeText
            recordCell.settingsLabel.text = settingsText
            recordCell.nRepsLabel.text = "#Reps: \(record.nReps ?? -1)"
            recordCell.statusLabel.text = statusText
        }

        return cell
    }


    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    @IBAction func unwindToRecordList(sender: UIStoryboardSegue) {
        if let recordDetailScreen = sender.source as? TouchSingleRecordViewController {
            recordList.remove(at: recordDetailScreen.recordIndex!)
            tableView.reloadData()
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
        super.prepare(for: segue, sender: sender)
        // is this the segue to the details screen? (in more complex apps, there is more than one segue per screen)
        if segue.identifier == "showDetailTouchRecord" {
            //down-cast from UIViewController to DetailViewController (this could fail if we didn’t link things up properly)
            guard let recordDetailViewController = segue.destination as? TouchSingleRecordViewController else {
                fatalError("Unexpected destination: \(segue.destination)")
            }

            //down-cast from UITableViewCell to MovieUITableViewCell (this could fail if we didn’t link things up properly)
            guard let selectedRecordCell = sender as? TouchRecordTableViewCell else {
                fatalError("Unexpected sender: \( String(describing: sender))")
            }

            //get the number of the row that was pressed (this could fail if the cell wasn’t in the table but we know it is)
            guard let indexPath = tableView.indexPath(for: selectedRecordCell) else {
                fatalError("The selected cell is not being displayed by the table")
            }

            //work out which movie it is using the row number
            let selectedRecord = recordList[indexPath.row]

            //send it to the details screen
            recordDetailViewController.record = selectedRecord
            recordDetailViewController.recordIndex = indexPath.row
        }
        
    }


}
