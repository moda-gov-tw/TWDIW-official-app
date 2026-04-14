//
//  CardInformationViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class CardInfomationViewController: BaseViewController {
    @IBOutlet weak var cardImageView: UIImageView!
    @IBOutlet weak var cardUnitLabel: UILabel!
    @IBOutlet weak var cardNameLabel: UILabel!
    @IBOutlet weak var cardInfoTitleLabel: UILabel!
    @IBOutlet weak var ialLabel: UILabel!
    @IBOutlet weak var infoBackgroundView: UIView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var expLabel: UILabel!
    @IBOutlet weak var changeStatusButton: UIButton!
    
    private let viewModel: CardInfomationViewModel
    private let pickerView = CustomPickerViewController()
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: CardInfomationViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "CardInformationViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        setupData()
        initBinding()
    }
    
    private func initBinding() {
        viewModel.finishDecodeSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                self?.tableView.reloadData()
            }.store(in: &cancelSet)
        
        viewModel.finishDeleteSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                viewModel.recordDelete()
                navigationController?.popToRootViewController(animated: true)
                if let rootViewController = navigationController?.viewControllers.first as? CardOverviewViewController {
                    rootViewController.showAlert(title: NSLocalizedString("RemoveCardFinish", comment: ""),
                                                 content: String(format: NSLocalizedString("RemoveCardMessage", comment: ""), viewModel.userVerifiableCredentialData.cardName ?? ""))
                }
            }.store(in: &cancelSet)
        
        viewModel.removeCardSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isBiometric in
                guard let self = self else { return }
                if isBiometric{
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        self.viewModel.deleteCard()
                    }
                }else{
                    let enterPinCodeViewModel = EnterPinCodeViewModel(repository: viewModel.repository)
                    let enterPinCodeVC = EnterPinCodeViewController(viewModel: enterPinCodeViewModel)
                    
                    enterPinCodeViewModel.veriflySuccessSubject
                        .receive(on: DispatchQueue.main)
                        .sink { [weak self] in
                            guard let self = self else { return }
                            
                            viewModel.deleteCard()
                        }.store(in: &cancelSet)
                    
                    self.navigationController?.pushViewController(enterPinCodeVC, animated: true)
                }
            }.store(in: &cancelSet)
        
        pickerView.selectSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] item in
                guard let self = self else { return }
                switch item.name{
                case NSLocalizedString("DeleteCard", comment: ""):
                    deleteCard()
                case NSLocalizedString("CertificateLog", comment: ""):
                    certificateLog()
                    break
                default:
                    break
                }
            }.store(in: &cancelSet)
    }
    
    private func setupData() {
        title = NSLocalizedString("CardInfoPage", comment: "")
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(CardInfomationTableViewCell.self)
        tableView.allowsSelection = false
        if let exp = viewModel.userVerifiableCredentialData.exp {
            let expString = Int(exp).toDateString()
            expLabel.text = String(format: NSLocalizedString("ExpDate", comment: ""), expString)
        }
        cardNameLabel.text = viewModel.userVerifiableCredentialData.cardName
        cardUnitLabel.text = viewModel.userVerifiableCredentialData.orgName
        
        if let ial = viewModel.userVerifiableCredentialData.vc.credentialDefinition.display.first?.description?.ial,
           !ial.isEmpty {
            ialLabel.text = NSLocalizedString("IAL", comment: "") + ial
        } else {
            ialLabel.text = NSLocalizedString("IAL", comment: "") + NSLocalizedString("NoIAL", comment: "")
        }
        
        
        if let cardImage = viewModel.userVerifiableCredentialData.cardImage {
            cardImageView.image = UIImage(data: cardImage)
        } else {
            cardImageView.image = .normalCardTop
        }
        
        viewModel.getDisplayData()
    }

    private func initView() {
        backButtonType = .normal
        
        infoBackgroundView.clipsToBounds = true
        infoBackgroundView.layer.cornerRadius = 16.0
        
        cardInfoTitleLabel.font = FontName.NotoSansTC_Bold.font.withSize(14)
        cardInfoTitleLabel.text = NSLocalizedString("CardInfoPage", comment: "憑證資料")
        
        changeStatusButton.titleLabel?.font = FontName.NotoSansTC_Medium.font.withSize(16.0)

        let moreInfoButton = UIButton(type: .system)
        moreInfoButton.setImage(.moreOption, for: .normal)
        moreInfoButton.frame = CGRect(x: 0, y: 0, width: 20, height: 20)
        moreInfoButton.addTarget(self, action: #selector(onClickMore), for: .touchUpInside)
        moreInfoButton.contentVerticalAlignment = .fill
        moreInfoButton.contentHorizontalAlignment = .fill
        moreInfoButton.imageView?.contentMode = .scaleAspectFit
        navigationItem.rightBarButtonItem = UIBarButtonItem(customView: moreInfoButton)
        
        if let targetView = navigationItem.rightBarButtonItem?.customView {
            var item: Array<CustomPickerItem> = Array()
            item.append(CustomPickerItem(name: NSLocalizedString("CertificateLog", comment: ""), status: .certificateLog))
            item.append(CustomPickerItem(name: NSLocalizedString("DeleteCard", comment: ""), status: .delete))
            pickerView.modalPresentationStyle = .overFullScreen
            pickerView.modalTransitionStyle = .crossDissolve
            pickerView.setupPicker(list: item, selectName: "", targetView: targetView)
        }
    }
    
    /**是否全部隱藏/顯示*/
    @IBAction func onClickChangeAllStatus(_ sender: Any) {
        /*隱藏全部*/
        if changeStatusButton.titleLabel?.text == NSLocalizedString("HideAll", comment: ""){
            isHideData(isHide: true)
            changeStatusButton.setTitle(NSLocalizedString("ShowAll", comment: ""), for: .normal)//隱藏後，文字提示調整為顯示
        }
        /*顯示全部*/
        else{
            isHideData(isHide: false)
            changeStatusButton.setTitle(NSLocalizedString("HideAll", comment: ""), for: .normal)//顯示後，文字提示調整為隱藏
        }
    }
    
    @objc func onClickMore() {
        present(pickerView, animated: true)
    }
    
    @objc private func hideCellText(_ sender: UIButton) {
        let row = sender.tag
        
        sender.isSelected.toggle()
        viewModel.displayDatas[row].isHide = sender.isSelected
        
        /*檢查全部資料隱藏/顯示狀態是否一致*/
        let statusAllSame: Bool = viewModel.displayDatas.allSatisfy { $0.isHide == viewModel.displayDatas.first?.isHide }
        
        if !statusAllSame {
            if sender.isSelected {
                changeStatusButton.setTitle(NSLocalizedString("ShowAll", comment: ""), for: .normal)
            } else {
                changeStatusButton.setTitle(NSLocalizedString("HideAll", comment: ""), for: .normal)
            }
        } else {
            if sender.isSelected {
                changeStatusButton.setTitle(NSLocalizedString("ShowAll", comment: ""), for: .normal)
            } else {
                changeStatusButton.setTitle(NSLocalizedString("HideAll", comment: ""), for: .normal)
            }
        }
        
        tableView.reloadData()
    }
}

extension CardInfomationViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.displayDatas.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(CardInfomationTableViewCell.self, forIndexPath: indexPath)
        
        let displayData = viewModel.displayDatas[indexPath.row]
        cell.titleLabel.text = displayData.displayName
        let value = displayData.value.isEmpty ? " " : displayData.value
        cell.contentLabel.text = displayData.isHide ? Config.hiddenText : value
        cell.hideOrShowButton.isSelected = displayData.isHide
        cell.hideOrShowButton.addTarget(self, action: #selector(hideCellText(_:)), for: .touchUpInside)
        cell.hideOrShowButton.tag = indexPath.row
        
        return cell
    }
}

extension CardInfomationViewController{
    /**清單資料是否全部隱藏/顯示*/
    private func isHideData(isHide: Bool){
        for index in 0...viewModel.displayDatas.count - 1 {
            viewModel.displayDatas[index].isHide = isHide
        }
        
        tableView.reloadData()
    }
    
    /**憑證紀錄*/
    private func certificateLog() {
        let vc = CardRecordViewController(viewModel: CardRecordViewModel(repository: viewModel.repository, verifiableCredential: viewModel.userVerifiableCredentialData))
        navigationController?.pushViewController(vc, animated: true)
    }
    
    /**移除卡片*/
    private func deleteCard() {
        let alert = CustomAlertViewController(title: NSLocalizedString("DeleteCard", comment: ""),
                                              content: String(format: NSLocalizedString("DeleteCardAlertContent", comment: ""), viewModel.userVerifiableCredentialData.cardName ?? ""),
                                              leftButtonTitle: NSLocalizedString("Cancel", comment: ""),
                                              leftButtonCompletion: nil,
                                              rightButtonTitle: NSLocalizedString("Confirm", comment: "")) { [weak self] in
            self?.viewModel.deleteAuthentication()
        }
        
        present(alert, animated: true)
    }
}
