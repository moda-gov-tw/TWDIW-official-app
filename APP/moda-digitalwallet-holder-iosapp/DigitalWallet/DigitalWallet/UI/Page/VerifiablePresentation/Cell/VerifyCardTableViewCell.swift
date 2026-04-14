//
//  VerifyCardTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifyCardTableViewCell: UITableViewCell {
    
    @IBOutlet weak var groupBackgroundView: UIView!
    @IBOutlet weak var groupTitleLabel: UILabel!
    @IBOutlet weak var cardInfoStackView: UIStackView!
    @IBOutlet weak var noAuthorizedStackView: UIStackView!
    @IBOutlet weak var noAuthorizedWarnView: UIView!
    @IBOutlet weak var noAuthorizedLabel: UILabel!
    @IBOutlet weak var noAuthorizedButton: UIButton!
    @IBOutlet weak var noAuthorizedImageView: UIImageView!
    @IBOutlet weak var dividerView: UIView!
    @IBOutlet weak var selectCardDividerView: UIView!
    @IBOutlet weak var selectCardStackView: UIStackView!
    @IBOutlet weak var selectCardTitleLabel: UILabel!
    @IBOutlet weak var selectCardImageView: UIImageView!
    @IBOutlet weak var changeCardButton: UIButton!
    
    /// 刷新UI
    var refreshUI: (()->Void)?
    /// 更換卡片/選擇卡片
    var changeCard: (()->Void)?
    /// 需移除卡片
    var needRemoveCard: ((CardInfoData?)->Void)?
    /// 查看授權卡片清單
    var noAuthorized: (()->Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        setupUI()
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        cardInfoStackView.arrangedSubviews.forEach({ $0.removeFromSuperview() })
    }
    
    private func setupUI() {
        groupBackgroundView.layer.borderWidth = 1.0
        groupBackgroundView.layer.cornerRadius = 12.0
        groupBackgroundView.clipsToBounds = true
        
        noAuthorizedLabel.text = NSLocalizedString("VerifyCardNoAuthorizedHint", comment: "目前缺少可授權的憑證")
        
        let buttonTitle = NSLocalizedString("VerifyCardNoAuthorizedButtonTitle", comment: "查看可授權的憑證清單")
        noAuthorizedButton.setTitle(buttonTitle, for: .normal)
        
        selectCardTitleLabel.text = NSLocalizedString("UnselectVerifyCardHint", comment: "尚未選擇憑證 請點選更改憑證")
        
        let changeButtonTitle: String? = NSLocalizedString("ChangeVerifyCard", comment: "更改憑證")
        changeCardButton.setTitle(changeButtonTitle, for: .normal)
    }
    
    /// 更換卡片/選擇卡片
    @IBAction func onClickChangeCard(_ sender: Any) {
        changeCard?()
    }
    
    /// 查看授權卡片清單
    @IBAction func onClickNoAuthorizedButton(_ sender: Any) {
        noAuthorized?()
    }
    
    func setupCell(_ data: VerifyGroupData, vpTitle: String?, eyeOpen: Bool) {
        /// 有選擇的卡片
        let hasSelectCard: Bool = data.cards.filter({$0.isSelect}).count > 0
        /// 是否顯示選擇憑證
        let canSelectCard: Bool = data.limitCount > data.cards.filter({$0.isSelect}).count
        
        let isPick: Bool = data.rule == .pick
        
        /// 選擇卡片畫面
        selectCardStackView.isHidden = !canSelectCard
        selectCardDividerView.isHidden = !canSelectCard || !hasSelectCard
        
        switch data.status {
        case .unselect: break
        case .noAuthorized:
            addCardsViews(data.cards.filter({$0.isSelect}), eyeOpen: eyeOpen, rule: data.rule).enumerated().forEach({
                if $0.offset > 0 {
                    cardInfoStackView.addArrangedSubview(DividerView())
                }
                cardInfoStackView.addArrangedSubview($0.element)
            })
        case .authorized:
            addCardsViews(data.cards.filter({$0.isSelect}), eyeOpen: eyeOpen, rule: data.rule).enumerated().forEach({
                if $0.offset > 0 {
                    cardInfoStackView.addArrangedSubview(DividerView())
                }
                cardInfoStackView.addArrangedSubview($0.element)
            })
        }
        groupBackgroundView.backgroundColor = hasSelectCard ? .F_6_F_7_FA : .white
        groupBackgroundView.layer.borderColor = UIColor.D_4_D_9_E_9.cgColor
        
        var hasEnoughVerifiedCards: Bool
        var selectCardTitleFont: UIFont = isPick ? FontName.NotoSansTC_Regular.font : FontName.NotoSansTC_Bold.font
        var selectCardTitleColor: UIColor = isPick ? ._767676 : .C_94_D_70
        selectCardTitleLabel.font = selectCardTitleFont
        selectCardTitleLabel.textColor = selectCardTitleColor
        selectCardImageView.isHidden = isPick
        
        if data.rule == .pick {
            /// 判斷使用者持有的卡片，是否滿足驗證group所要求的卡片數量`count`
            hasEnoughVerifiedCards = data.count ?? 1 > data.limitCount
        } else {
            /// 判斷使用者持有的卡片，是否滿足驗證group所要求的總卡片數量`requestCards.count`
            hasEnoughVerifiedCards = data.requestCards.count > data.limitCount
        }
        /// 分隔線出現的條件為 -> 未滿足group要求卡片數量，且使用者持有可驗證的卡片數量不為0
        dividerView.isHidden = !(hasEnoughVerifiedCards && data.limitCount != 0)
        /// 顯示缺少可授權憑證提示區塊的條件為->使用者持有的卡片，不滿足group所要求的最低數量
        noAuthorizedStackView.isHidden = !hasEnoughVerifiedCards
        noAuthorizedLabel.textColor =  isPick ? ._767676 : .C_94_D_70
        noAuthorizedImageView.isHidden = isPick
        
        let hasGroupName = !(data.groupName?.isEmpty ?? true)
        let groupTitleName = hasGroupName ? data.groupName : vpTitle
        groupTitleLabel.text = groupTitleName
        
    }
    
    /// 加入驗證卡片列表
    private func addCardsViews(_ data: [CardInfoData], eyeOpen: Bool, rule: VerifyCardRule?) -> [VerifyCardView] {
        return data.map { infoData -> VerifyCardView in
            let resultView = VerifyCardView()
            resultView.setupUI(data: infoData, eyeOpen: eyeOpen, rule: rule)
            resultView.refreshUI = { [weak self] in
                self?.refreshUI?()
            }
            
            resultView.needRemoveCard = { [weak self] cardInfo in
                self?.needRemoveCard?(cardInfo)
            }
            return resultView
        }
    }
}
