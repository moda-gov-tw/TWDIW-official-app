//
//  VerifyCardView.swift
//  DigitalWallet
//

import UIKit

class VerifyCardView: UIView {
    
    /// 卡片名稱
    @IBOutlet weak var cardNamelabel: UILabel!
    /// 展開關閉按鈕
    @IBOutlet weak var expandButton: UIButton!
    /// 全選按鈕
    @IBOutlet weak var selectAllButton: UIButton!
    /// 組織View
    @IBOutlet weak var orgView: UIView!
    /// 組織名稱
    @IBOutlet weak var orgLabel: UILabel!
    /// 列表資訊
    @IBOutlet weak var infoLabel: UILabel!
    /// 其他授權資訊背景
    @IBOutlet weak var infoBackGroundView: UIView!
    /// 其他授權資訊
    @IBOutlet weak var infoStackView: UIStackView!
    
    /// 刷新畫面
    var refreshUI: (() -> Void)?
    
    /// 需移除卡片
    var needRemoveCard: ((CardInfoData?) -> Void)?
    
    var cardData: CardInfoData?
    
    var eyeOpen: Bool = true
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        loadNib(VerifyCardView.self)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        loadNib(VerifyCardView.self)
        initUI()
    }
    
    private func initUI() {
        selectAllButton.setTitle("全選", for: .normal)
        selectAllButton.setTitle("取消全選", for: .selected)
    }
    
    func setupUI(data:CardInfoData, eyeOpen: Bool, rule: VerifyCardRule?) {
        cardData = data
        cardNamelabel.text = data.card.cardName
        orgLabel.text = data.card.orgName
        orgView.isHidden = !data.isExpand
        
        infoLabel.text = eyeOpen ? data.infoContent : Config.hiddenText
        infoLabel.isHidden = data.isExpand

        expandButton.isSelected = data.isExpand
        infoBackGroundView.isHidden = !data.isExpand
        selectAllButton.isHidden = rule == .all ? true : false
        
        self.eyeOpen = eyeOpen
        if data.isExpand {
            addInfos(data: data.infos, rule: rule)
            let selectAll = data.infos.filter({!$0.isSelect}).isEmpty
            selectAllButton.isSelected = selectAll
            if rule == .all {
                selectAllButton.isEnabled = false
            }
        }
    }
    
    /// 加入授權資訊
    private func addInfos(data: [CardRequirement], rule: VerifyCardRule?) {
        data.enumerated().forEach { (index ,selectData) in
            if index != 0 {
                infoStackView.addArrangedSubview(DividerView())
            }
            let view = VerifySelectView()
            view.setupView(info: selectData, eyeOpen: eyeOpen, rule: rule)
            view.clickCheckBox = { [weak self] _ in
                guard let self else { return }
                checkRefreshUI()
            }
            infoStackView.addArrangedSubview(view)
        }
    }
    
    /// 變更授權選項時判斷是否刷新UI
    private func checkRefreshUI() {
        if cardData?.infos.filter({!$0.isSelect}).isEmpty ?? true ||
            cardData?.infos.filter({!$0.isSelect}).count == 1 ||
            cardData?.infos.filter({$0.isSelect}).isEmpty ?? true ||
            cardData?.infos.filter({$0.isSelect}).count == 1 {
            refreshUI?()
        }
    }
    
    /// 點擊展開
    @IBAction func onClickExpand(_ sender: UIButton) {
        if sender.isSelected && cardData?.infos.filter({$0.isSelect}).isEmpty ?? true {
            needRemoveCard?(cardData)
        } else {
            cardData?.isExpand = !sender.isSelected
            refreshUI?()
        }
    }
    
    /// 點擊選擇全部
    @IBAction func onClickSelectAll(_ sender: UIButton) {
        sender.isSelected.toggle()
        cardData?.infos.forEach({ $0.isSelect = sender.isSelected })
        refreshUI?()
    }
}
