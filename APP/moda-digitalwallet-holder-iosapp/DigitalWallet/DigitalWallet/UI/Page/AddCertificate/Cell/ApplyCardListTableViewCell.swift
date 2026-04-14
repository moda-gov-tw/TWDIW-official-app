//
//  ApplyCardListTableViewCell.swift
//  DigitalWallet
//

import UIKit

class ApplyCardListTableViewCell: UITableViewCell {
    @IBOutlet weak var contentBackgroundView: UIView!
    @IBOutlet weak var certificateImageView: UIImageView!
    @IBOutlet weak var certificateName: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupLabel()
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        certificateImageView.image = nil
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        setupView()
    }
    
    
    private func setupView() {
        contentView.layer.masksToBounds = false
        contentView.layer.shadowColor = UIColor.A_8_A_8_A_8.cgColor
        contentView.layer.shadowOffset = CGSize(width: 0, height: 0)
        contentView.layer.shadowRadius = 20
        contentView.layer.shadowOpacity = 1
        contentView.layer.shadowPath = UIBezierPath(rect: contentView.bounds).cgPath
        contentBackgroundView.layer.cornerRadius = 16.0
        contentBackgroundView.layer.masksToBounds = true
        
    }
    
    private func setupLabel() {
        certificateName.font = FontName.NotoSansTC_Medium.font.withSize(16)
    }
}

extension ApplyCardListTableViewCell {
    func setupCell(_ data: VCItems, imageLoadManager: ImageLoadProtocol) {
        certificateName.text = data.name
        
        Task {
            if let logoUrlString = data.logoUrl,
               let logoUrl = URL(string: logoUrlString) {
                do {
                    let image = try await imageLoadManager.loadImage(from: logoUrl)
                    certificateImageView.image = image
                } catch {
                    certificateImageView.image = .appIcon
                }
            } else {
                certificateImageView.image = .appIcon
            }
        }
    }
}
