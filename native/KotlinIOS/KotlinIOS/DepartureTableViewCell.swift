//
//  DepartureTableViewCell.swift
//  KotlinIOS
//
//  Created by Harry Best on 28/06/2022.
//  Copyright © 2022 Evgeny Petrenko. All rights reserved.
//

import UIKit

class DepartureTableViewCell: UITableViewCell {
    @IBOutlet weak var departureTime: UILabel!
    @IBOutlet weak var arrivalTime: UILabel!
    @IBOutlet weak var price: UILabel!
    @IBOutlet weak var travelTime: UILabel!
    @IBOutlet weak var primaryOperator: UILabel!
    @IBOutlet weak var purchase: UIButton!
    
    var purchaseURL: String?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    @IBAction func purchaseButtonPressed(_ sender: Any) {
        if let url = purchaseURL {
            UIApplication.shared.open(URL(string: url)!)
        }
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
}
