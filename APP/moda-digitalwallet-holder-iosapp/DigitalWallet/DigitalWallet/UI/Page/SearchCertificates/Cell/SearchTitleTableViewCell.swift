//
//  SearchResultsTitleTableViewCell.swift
//  DigitalWallet
//

import UIKit

class SearchTitleTableViewCell: UITableViewCell {

    @IBOutlet weak var searchTitleLabel: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupView()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    private func setupView() {
        selectionStyle = .none
        searchTitleLabel.font = FontName.NotoSansTC_Bold.font.withSize(14)
    }
}

extension SearchTitleTableViewCell {
    func setupCell(str: String) {
        searchTitleLabel.text = str
    }
}
