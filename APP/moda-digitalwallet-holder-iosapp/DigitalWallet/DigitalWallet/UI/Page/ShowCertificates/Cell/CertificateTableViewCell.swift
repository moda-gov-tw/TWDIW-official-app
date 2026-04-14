//
//  StoreTableViewCell.swift
//  DigitalWallet
//

import UIKit

class CertificateTableViewCell: UITableViewCell {
    enum StarStatus {
        /// 已經新增
        case added(MyFavoriteList)
        /// 尚未新增
        case notAdded(VPItems)
    }
    
    @IBOutlet weak var certificateLabel: UILabel!
    @IBOutlet weak var favoriteStarButton: UIButton!
    @IBOutlet weak var contentBackgroundView: UIView!
    
    /// 狀態
    var starStatus: StarStatus?
    /// 點擊加入或刪除
    var onClickStar: ((StarStatus) -> Void)?
    /// 我得最愛清單資料
    var myfavoriteItem: MyFavoriteList?
    /// 快速授權清單資料
    var vpItem: VPItems?
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        setupView()
    }
    
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    private func setupView() {
        selectionStyle = .none
        contentView.layer.masksToBounds = false
        contentView.layer.masksToBounds = false
        contentView.layer.shadowColor = UIColor.A_8_A_8_A_8.cgColor
        contentView.layer.shadowOffset = CGSize(width: 0, height: 0)
        contentView.layer.shadowRadius = 20
        contentView.layer.shadowOpacity = 1
        contentView.layer.shadowPath = UIBezierPath(rect: contentView.frame).cgPath
        contentBackgroundView.layer.cornerRadius = 16.0
        contentBackgroundView.layer.masksToBounds = true
        certificateLabel.font = FontName.NotoSansTC_Medium.font.withSize(16)
    }
    
    @IBAction func clickStar(_ sender: Any) {
        switch starStatus {
        case .added(let myFavoriteList):
            onClickStar?(.added(myFavoriteList))
            break
        case .notAdded(let vpItems):
            onClickStar?(.notAdded(vpItems))
            break
        case nil:
            break
        }
    }
}

extension CertificateTableViewCell {
    func setupMyFavoriteCell(data: MyFavoriteList) {
        starStatus = .added(data)
        myfavoriteItem = data
        certificateLabel.text = data.name
        favoriteStarButton.setImage(UIImage.fullFavoriteStar32, for: .normal)
    }
    
    func setupAuthCell(data: VPItems) {
        starStatus = .notAdded(data)
        vpItem = data
        certificateLabel.text = data.name
        favoriteStarButton.setImage(UIImage.favoriteStar32, for: .normal)
    }
}
