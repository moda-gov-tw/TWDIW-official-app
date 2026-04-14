//
//  ChangeCardViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class ChangeCardViewController: BaseViewController {

    @IBOutlet weak var cardTitleLabel: UILabel!
    @IBOutlet weak var expandButton: UIButton!
    @IBOutlet weak var confirmButton: UIButton!
    @IBOutlet weak var eyeButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var buttonBackGroundView: UIView!
    
    typealias Completion = () -> Void
    let viewModel: ChangeCardViewModel
    var finishChangeCard: Completion?
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: ChangeCardViewModel, finishChangeCard: Completion?) {
        self.viewModel = viewModel
        self.finishChangeCard = finishChangeCard
        super.init(nibName: "ChangeCardViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupData()
        binding()
        tableView.reloadData()
    }
    
    /// 基礎設定
    private func setupData() {
        
        title = NSLocalizedString("SelectVerifyCardTitle", comment: "更改憑證")
        
        expandButton.layer.cornerRadius = expandButton.frame.height * 0.5
        expandButton.layer.masksToBounds = true
        expandButton.layer.borderWidth = 1
        expandButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        expandButton.semanticContentAttribute = .forceRightToLeft
        
        updateCount()
        
        // 眼睛按鈕設定
        let openEyesImage = UIImage(named: ImageName.CreateWallet.OpenEyes.rawValue)
        let closeEyesImage = UIImage(named: ImageName.CreateWallet.CloseEyes.rawValue)
        eyeButton.setImage(closeEyesImage, for: .normal)
        eyeButton.setImage(openEyesImage, for: .selected)
        eyeButton.isSelected = viewModel.eyeOpen
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(VerifyCardChangeHintTableViewCell.self)
        tableView.registerWithNib(VerifyCardChangeTableViewCell.self)
        tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 76, right: 0)
        
        buttonBackGroundView.clipsToBounds = false
        buttonBackGroundView.layer.cornerRadius = 28.0
        buttonBackGroundView.layer.maskedCorners = [.layerMinXMinYCorner, .layerMaxXMinYCorner]
        buttonBackGroundView.layer.shadowOpacity = 0.25
        buttonBackGroundView.layer.shadowColor = UIColor._868686.cgColor
        
        confirmButton.titleLabel?.text = NSLocalizedString("Confirm", comment: "確認")
        confirmButton.layer.cornerRadius = confirmButton.bounds.height * 0.5
        confirmButton.clipsToBounds = true
    }
    
    private func updateCount() {
        cardTitleLabel.text = viewModel.getCount()
    }
    
    private func binding() {
        viewModel.refreshUISubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                tableView.reloadData()
                updateCount()
            }.store(in: &cancelSet)
    }
    
    @IBAction func onClickEyeButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        viewModel.eyeOpen = sender.isSelected
        tableView.reloadData()
    }
    
    /// 點擊確認按鈕
    @IBAction func onClickConfirmButton(_ sender: Any) {
        viewModel.finishChangeCardUpdate()
        navigationController?.popViewController(animated: true)
        finishChangeCard?()
    }
    
    /// 點擊全部展開
    @IBAction func onClickExpandButton(_ sender: UIButton) {
        sender.isSelected.toggle()
        viewModel.setExpand(sender.isSelected)
        tableView.reloadData()
    }
}

extension ChangeCardViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.changeCardItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch viewModel.changeCardItems[indexPath.row] {
        case .hintMessage(let message):
            let cell = tableView.dequeueReusableCell(VerifyCardChangeHintTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(message)
            return cell
        case .cardGroup(let cards):
            let cell = tableView.dequeueReusableCell(VerifyCardChangeTableViewCell.self, forIndexPath: indexPath)
            let isEnabled = viewModel.checkEnable(cards)
            cell.setupCell(cards, isEnable: isEnabled, eyeOpen: viewModel.eyeOpen)
            
            cell.refreshUI = { [weak self] in
                guard let self else { return }
                expandButton.isSelected = viewModel.updateExpandButtonSelect()
                tableView.reloadData()
            }
            
            cell.selectCard = { [weak self] selectCard in
                guard let self else { return }
                viewModel.checkSelect(cardInfo: selectCard)
            }
            
            return cell
        }
    }
}
