//
//  TouchRecordTableViewCell.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit

class TouchRecordTableViewCell: UITableViewCell {

    @IBOutlet var startLabel: UILabel!
    @IBOutlet var endLabel: UILabel!
    @IBOutlet var modeLabel: UILabel!
    @IBOutlet var settingsLabel: UILabel!
    @IBOutlet var nRepsLabel: UILabel!
    @IBOutlet var statusLabel: UILabel!
    
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
