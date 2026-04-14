//
//  CardRecordViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class CardRecordViewController: BaseViewController {
    @IBOutlet weak var sortButton: UIButton!
    @IBOutlet weak var cardImageView: UIImageView!
    @IBOutlet weak var cardUnitLabel: UILabel!
    @IBOutlet weak var cardRecordLabel: UILabel!
    @IBOutlet weak var cardNameLabel: UILabel!
    @IBOutlet weak var logTableView: UITableView!
    @IBOutlet weak var shareView: UIView!
    @IBOutlet weak var updateDateLabel: UILabel!
    
    let pickerView = CustomPickerViewController()
    
    var selectSortType: CardOverViewViewModel.SortType = .newToOld//使用者選擇的排序順序
    
    private let viewModel: CardRecordViewModel
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: CardRecordViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "CardRecordViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
        setupData()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        refreshUI()
    }
    
    private func initView() {
        backButtonType = .normal
        
        shareView.isGone = true
        
        cardNameLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        cardRecordLabel.font = FontName.NotoSansTC_Bold.font.withSize(14)
        sortButton.layer.cornerRadius = sortButton.frame.height * 0.5
        sortButton.layer.masksToBounds = true
        sortButton.layer.borderWidth = 1
        sortButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        sortButton.semanticContentAttribute = .forceRightToLeft
        
        logTableView.registerWithNib(CardLogTableViewCell.self)
        logTableView.separatorStyle = UITableViewCell.SeparatorStyle.none//隱藏分隔線
        
        // 排序picker設定
        let sortItems = [
            CustomPickerItem(name: NSLocalizedString("NewToOld", comment: "")),
            CustomPickerItem(name: NSLocalizedString("OldToNew", comment: ""))
        ]
        
        pickerView.setupPicker(list: sortItems,
                               selectName: NSLocalizedString("NewToOld", comment: ""),
                               targetView: sortButton)
        pickerView.modalTransitionStyle = .crossDissolve
        pickerView.modalPresentationStyle = .overFullScreen
        
        let updateDate = viewModel.userVerifiableCredentialData.updateDate ?? Date()
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy/MM/dd HH:mm"
        let dateString = formatter.string(from: updateDate)
        updateDateLabel.text = String(format: NSLocalizedString("CardRecordUpdateDate", comment: ""), dateString)
    }
    
    private func setupData() {
        title = NSLocalizedString("CardRecord", comment: "")
        cardUnitLabel.text = viewModel.userVerifiableCredentialData.orgName ?? ""
        cardNameLabel.text = viewModel.userVerifiableCredentialData.cardName ?? ""
        if let cardImage = viewModel.userVerifiableCredentialData.cardImage {
            cardImageView.image = UIImage(data: cardImage)
        } else {
            cardImageView.image = .normalCardTop
        }
        viewModel.getCardRecord()
    }
    
    private func initBinding() {
        viewModel.recordSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                self?.logTableView.reloadData()
            }.store(in: &cancelSet)
        
        /*重新排序*/
        pickerView.selectSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] item in
                guard let self else { return }
                
                sortButton.setTitle(item.name, for: .normal)
                
                if item.name == NSLocalizedString("NewToOld", comment: "") {
                    selectSortType = .newToOld
                } else {
                    selectSortType = .oldToNew
                }
                
                viewModel.sortCards(isNewToOld: selectSortType == .newToOld)
                
                pickerView.setupSelecedName(selectName: item.name)
            }.store(in: &cancelSet)
    }
    
    /**排序*/
    @IBAction func onClickSort(_ sender: UIButton) {
        
        present(pickerView, animated: true)
    }
    
    /*
     * 重新抓卡片順序
     * 新->舊
     */
    private func refreshUI() {
        if selectSortType == .newToOld{
            sortButton.setTitle(NSLocalizedString("NewToOld", comment: ""), for: .normal)
            pickerView.setupSelecedName(selectName: NSLocalizedString("NewToOld", comment: ""))
            
        }
        /*舊->新*/
        else{
            sortButton.setTitle(NSLocalizedString("OldToNew", comment: ""), for: .normal)
            pickerView.setupSelecedName(selectName: NSLocalizedString("OldToNew", comment: ""))
            self.viewModel.sortCards(isNewToOld: false)
        }
    }
    
    @objc func onClickMore() {
        let vc = CardInfomationViewController(viewModel: CardInfomationViewModel(verifiableManager: viewModel.repository.verifiableManager,
                                                                                  databaseManager: viewModel.repository.databaseManager,
                                                                                  verifiableCredential: viewModel.userVerifiableCredentialData,
                                                                                  repository:  viewModel.repository, biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!))
        navigationController?.pushViewController(vc, animated: true)
    }
}

extension CardRecordViewController: UITableViewDataSource, UITableViewDelegate {
    /**每組共有多少列*/
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.record.count
    }
    
    /**第indexPath.section組 第indexPath.row 行顯示怎樣的cell*/
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(CardLogTableViewCell.self, forIndexPath: indexPath)
        
        let data: CardRecord = viewModel.record[indexPath.row]
        
        cell.setupCell(data)
        cell.onClickExpand = { [weak self] in
            guard let self else { return }
            viewModel.updateCardRecord(indexPath: indexPath)
        }
        
        return cell
    }
}
