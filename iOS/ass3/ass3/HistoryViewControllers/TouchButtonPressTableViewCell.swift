//
//  TouchButtonPressTableViewCell.swift
//  ass3
//
//  Created by Thomas Dang on 24/5/2022.
//

import UIKit

class TouchButtonPressTableViewCell: UITableViewCell {

    @IBOutlet var timeLabel: UILabel!
    @IBOutlet var buttonLabel: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
