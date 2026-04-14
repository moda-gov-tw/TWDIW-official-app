//
//  VerifiablePresentationViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class VerifiablePresentationViewController: BaseViewController {
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var verifyButton: UIButton!
    @IBOutlet weak var cancelButton: UIButton!
    @IBOutlet weak var agreementTitleLabel: UILabel!
    @IBOutlet weak var agreementLabel: UILabel!
    @IBOutlet weak var agreementView: UIView!
    
    let viewModel: VerifiablePresentationViewModel
    
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: VerifiablePresentationViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "VerifiablePresentationViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupData()
        initBinding()
        checkInitHint(viewModel.checkInitHint())
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        viewModel.checkVerifyButtonEnable()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
    }
    
    private func initBinding() {
        viewModel.updateVerifyButtonSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isEnabled in
                guard let self else { return }
                viewModel.isEnableVerify = isEnabled
                verifyButton.isEnabled = isEnabled
                verifyButton.backgroundColor = isEnabled ? ._4_E_61_A_7 : .DEDEDE
            }.store(in: &cancelSet)
        
        viewModel.isVerifyingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isVerifying in
                if isVerifying {
                    self?.showVerifying()
                } else {
                    self?.dismissVerifying()
                }
            }.store(in: &cancelSet)
        
        viewModel.finishVerifySubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (isSuccess, resultPageType) in
                guard let self else { return }
                dismissVerifying()
                
                if isSuccess, (resultPageType == .showQRCode) {
                    showQRCodePage()
                    return
                }
                
                presentResultPage(isSuccess, resultPageType)
            }.store(in: &cancelSet)
        
        viewModel.authenticationSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isBiometric in
                guard let self = self else { return }
                
                if isBiometric{
                    viewModel.verify()
                }else{
                    let enterPinCodeViewModel = EnterPinCodeViewModel(repository: viewModel.userRepository, title: viewModel.title)
                    let enterPinCodeVC = EnterPinCodeViewController(viewModel: enterPinCodeViewModel)
                    
                    enterPinCodeViewModel.veriflySuccessSubject
                        .receive(on: DispatchQueue.main)
                        .sink { [weak self] in
                            guard let self = self else { return }
                            
                            viewModel.verify()
                        }.store(in: &cancelSet)
                    
                    self.navigationController?.pushViewController(enterPinCodeVC, animated: true)
                }
            }.store(in: &cancelSet)
        
        viewModel.incompleteInformationSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                let title = NSLocalizedString("IncompleteInformationTitle", comment: "提供資料確認")
                let content = NSLocalizedString("IncompleteInformationContent", comment: "若無提供完整資料內容，可能會造成驗證失敗，需洽授權單位。")
                askAlert(title: title,
                         content: content) { confirm in
                    if confirm {
                        self.viewModel.authentication()
                    }
                }
            }.store(in: &cancelSet)
        
        viewModel.checkRegularExpressionSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                tableView.reloadData()
            }.store(in: &cancelSet)
    }
    
    private func setupData() {
        tableView.dataSource = self
        tableView.delegate = self
        
        tableView.registerWithNib(VerifyCardTableViewCell.self)
        tableView.registerWithNib(VerifiablePresentationInfoTableViewCell.self)
        tableView.registerWithNib(VerifiablePresentationListInfoTableViewCell.self)
        tableView.registerWithNib(VerifiablePresentationCustomTableViewCell.self)
        
        title = viewModel.title
        setupAgreementView()
    }
    
    /// 送出按鈕設定
    private func setupAgreementView() {
        verifyButton.layer.cornerRadius = verifyButton.frame.height / 2
        verifyButton.clipsToBounds = true
        cancelButton.layer.borderWidth = 1
        cancelButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        cancelButton.layer.cornerRadius = verifyButton.frame.height / 2
        cancelButton.layer.masksToBounds = false
        
        agreementLabel.text = String(format: NSLocalizedString("VerifyAgreementSubtitle", comment: "%@知情同意書"), viewModel.title)
        
        agreementView.isHidden = viewModel.repository.termsURI.isEmpty
        var verifyButtonTitle: String?
        switch viewModel.resultPageType {
        case .showResult:
            verifyButtonTitle = NSLocalizedString("VerifyButtonTitle", comment: "送出資料")
        case .showQRCode:
            verifyButtonTitle = NSLocalizedString("VerifyButtonShowScanTitle", comment: "產生條碼")
        }
        agreementTitleLabel.text = String(format: NSLocalizedString("VerifyAgreementTitle", comment: "點擊「%@」即表示同意資料提供"), verifyButtonTitle ?? "")
        verifyButton.setTitle(verifyButtonTitle, for: .normal)
        verifyButton.isEnabled = viewModel.isEnableVerify
        verifyButton.backgroundColor = viewModel.isEnableVerify ? ._4_E_61_A_7 : .DEDEDE
        
        let tapAgreemntGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(clickAgreement))
        agreementView.addGestureRecognizer(tapAgreemntGestureRecognizer)
    }
    
    @objc private func clickAgreement() {
        checkNetworkStatus { hasNetwork in
            if hasNetwork {
                let terms = Terms(title: self.viewModel.title, content: .VPAgreement(urlString: self.viewModel.repository.termsURI), isAgree: false)
                terms.$isAgree
                    .receive(on: DispatchQueue.main)
                    .sink { isAgree in
                    }.store(in: &self.cancelSet)
                let termsViewModel = TermsViewModel(terms: terms, showButton: false)
                let termsVC = TermsViewController(viewModel: termsViewModel)
                self.navigationController?.pushViewController(termsVC, animated: true)
            } else {
                self.showAlert(title: NSLocalizedString("ConnectionFailedTitle", comment: "連線失敗"),
                               content: NSLocalizedString("ConnectionFailedContent", comment: "原因可能是目前使用裝置或該機關網路異常,請注意裝置連線情況，稍後再嘗試使用。"))
            }
        }
    }
    
    @IBAction func onClickVerify(_ sender: Any) {
        viewModel.authorizationCheck()
    }
    
    @IBAction func onClickCancel(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
    
    /// 用遞迴去顯示跳窗
    func checkInitHint(_ checkList: [VerifiablePresentationViewModel.InitHintCheck]) {
        var reshowList = checkList
        var title: String = ""
        var content: String = ""
        switch checkList.first {
        case .noInTrustedList:
            title = NSLocalizedString("NotInTrustedListTitle", comment: "確認單位資訊")
            content = NSLocalizedString("NotInTrustedListContent", comment: "提醒您，您將提供資料的單位尚未列入【信任清單】，建議再次確認是否要『送出資料』。")
        case .missingCards:
            title = NSLocalizedString("HasMissingCardsTitle", comment: "無法送出資料")
            content = NSLocalizedString("HasMissingCardsContent", comment: "缺少必要授權內容，請前往申請可授權憑證後再次嘗試。")
        default: return
        }
        showAlert(title: title,
                  content: content) { [weak self] in
            guard let self = self else { return }
            reshowList.removeFirst()
            if reshowList.isEmpty { return }
            checkInitHint(reshowList)
        }
    }
}

extension VerifiablePresentationViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.verifiablePresentationItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        switch viewModel.verifiablePresentationItems[indexPath.row] {
            
        case .Info(let authorizingAgency,
                   let purpose):
            return updateInfoCell(authorizingAgency: authorizingAgency, purpose: purpose, indexPath: indexPath)
        case .ListInfo(let eyeOpen):
            return updateCardInfoCell(eyeOpen: eyeOpen, indexPath: indexPath)
        case .Requirement(let group, let eyeOpen):
            return updateRequirementCell(data: group, indexPath: indexPath, eyeOpen: eyeOpen)
        case .CustomFields(let field, let eyeOpen):
            return updateCustomField(indexPath: indexPath, field: field, eyeOpen: eyeOpen)
        }
    }
    
    /// 授權資料
    private func updateInfoCell(authorizingAgency: String?, purpose: String?, indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(VerifiablePresentationInfoTableViewCell.self, forIndexPath: indexPath)
        cell.setupCell(authorizingAgency: authorizingAgency)
        return cell
    }
    
    /// 授權卡片清單
    private func updateCardInfoCell(eyeOpen: Bool, indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(VerifiablePresentationListInfoTableViewCell.self, forIndexPath: indexPath)
        cell.setupCell(eyeOpen: eyeOpen)
        cell.changeEyeStatus = { [weak self] eyeOpen in
            guard let self else { return }
            viewModel.changeEyeOpen(eyeOpen)
            tableView.reloadData()
        }
        return cell
    }
    
    /// 卡片內容
    private func updateRequirementCell(data: VerifyGroupData, indexPath: IndexPath, eyeOpen: Bool) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(VerifyCardTableViewCell.self, forIndexPath: indexPath)
        cell.setupCell(data, vpTitle: viewModel.title, eyeOpen: eyeOpen)
        /// 卡片群組要求刷新畫面
        cell.refreshUI = { [weak self] in
            guard let self else { return }
            viewModel.checkVerifyButtonEnable()
            tableView.reloadData()
        }
        
        /// 更換卡片
        cell.changeCard = { [weak self] in
            guard let self else { return }
            let changeCardViewModel = ChangeCardViewModel(repository: viewModel.repository,
                                                          verifyGroup: data,
                                                          verifyGroupCopy: data.copy())
            let changeCardVC = ChangeCardViewController(viewModel: changeCardViewModel) {
                self.viewModel.checkVerifyButtonEnable()
                self.tableView.reloadData()
            }
            navigationController?.pushViewController(changeCardVC, animated: true)
        }
        
        cell.needRemoveCard = { [weak self] cardInfo in
            guard let self else { return }
            let title = NSLocalizedString("VerifiablePresentationRemoveCardTitle", comment: "提供資料確認")
            let content = String(format: NSLocalizedString("VerifiablePresentationRemoveCardContent", comment: "「%@」目前尚未選擇任何授權資料，請重新選擇憑證進行授權。"), cardInfo?.card.cardName ?? "")
            askAlert(title: title, content: content) { confirm in
                cardInfo?.isSelect = !confirm
                self.tableView.reloadData()
            }
        }
        
        /// 查可授權卡片清單
        cell.noAuthorized = { [weak self] in
            guard let self else { return }
            let viewModel = AuthorizedCardListViewModel(groupData: data)
            let vc = AuthorizedCardListViewController(viewModel: viewModel)
            navigationController?.pushViewController(vc, animated: true)
        }
        return cell
    }
    
    /// 客製化欄位
    private func updateCustomField(indexPath: IndexPath, field: DwModa201iCustomField, eyeOpen: Bool) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(VerifiablePresentationCustomTableViewCell.self, forIndexPath: indexPath)
        let row: Int = indexPath.row - (2 + viewModel.repository.verifyResp.groupList.count)
        let cellData = viewModel.customFields[row]
        cell.setupCell(cellData, eyeOpen: eyeOpen)
        cell.onFinishEdit = { [weak self] in
            guard let self else { return }
            viewModel.checkVerifyButtonEnable()
        }
        return cell
    }
}

extension VerifiablePresentationViewController {
    func showQRCodePage() {
        let viewModel = VP05QRCodeViewModel(repository: viewModel.userRepository,
                                            callTabbarProtocol: viewModel.callTabbarProtocol)
        let vc = VP05QRCodeViewController(viewModel: viewModel)
        navigationController?.pushViewController(vc, animated: true)
    }
    
    func presentResultPage(_ isSuccess: Bool, _ resultPageType: VerifiablePresentationResult) {
        let viewModel = VerifivationResultsViewModel(repository: viewModel.repository,
                                                     resultType: resultPageType,
                                                     customFields: viewModel.customFields)
        let vc = VerifivationResultsViewController(viewModel: viewModel)
        vc.resultState = isSuccess ? .success : .fail
        vc.modalPresentationStyle = .overFullScreen
        
        navigationController?.present(vc, animated: true, completion: {
            self.navigationController?.popToRootViewController(animated: true)
        })
    }
    
}
