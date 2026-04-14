//
//  CardOverviewViewController.swift
//  DigitalWallet
//

import UIKit
import Combine
import AVFoundation

class CardOverviewViewController: BaseViewController {
    
    /// 更新驗證狀態
    enum VerifyType {
        /// 驗證中
        case verifying(number: String, totalCount: String)
        /// 更新失敗
        case updateFail(count: String)
        /// 網路失敗
        case internetError
        /// 更新完成
        case finish(total: String)
    }

    @IBOutlet weak var addCardView: UIView!
    @IBOutlet weak var addCardImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var sortButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var addFirstCardNowLabel: UILabel!
    @IBOutlet weak var verifyStatusLabel: UILabel!
    @IBOutlet weak var verifyStatusImageView: UIImageView!
    @IBOutlet weak var verifyingView: UIView!
    @IBOutlet weak var verifyButton: UIButton!
    
    private let customNavigationBar: CustomNavigationBar = CustomNavigationBar()
    private let pickerView = CustomPickerViewController()
    private let viewModel: CardOverViewViewModel
    private lazy var scanCardRecognizer = UITapGestureRecognizer()
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: CardOverViewViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "CardOverviewViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
        viewModel.initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if verifyingView.isHidden {
            refreshCards()
            updateWallet()
        } else {
            viewModel.checkVerifySubject.send(verifyingView.isHidden)
        }
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        tableView.refreshControl?.endRefreshing()
    }
    
    /// 刷新卡片
    private func refreshCards() {
        // 1) 先從DB取出所有卡片
        viewModel.getCards()
        // 2) 刷新卡片
        viewModel.refreshCards()
    }
    
    private func initView() {
        // CustomNavigationBar
        customNavigationBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(customNavigationBar)
        let statusBarHeight = UIApplication.shared.windows.first?.windowScene?.statusBarManager?.statusBarFrame.height ?? 44
        NSLayoutConstraint.activate([
            customNavigationBar.topAnchor.constraint(equalTo: view.topAnchor),
            customNavigationBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            customNavigationBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            customNavigationBar.heightAnchor.constraint(equalToConstant: statusBarHeight + 44)
        ])
        
        isHideNavigationBar = true
        
        // tableView設定
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(NormalCardTableViewCell.self)
        tableView.refreshControl = UIRefreshControl()
        tableView.refreshControl?.addTarget(self, action: #selector(handleRefreshControl), for: .valueChanged)
        
        // 畫面元件設定
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 14)
        titleLabel.text = NSLocalizedString("AllCertificates", comment: "")
        addFirstCardNowLabel.font = FontName.NotoSansTC_Bold.font.withSize(18)
        addFirstCardNowLabel.text = NSLocalizedString("AddFirstCardNow", comment: "")
        sortButton.layer.cornerRadius = sortButton.frame.height * 0.5
        sortButton.layer.masksToBounds = true
        sortButton.layer.borderWidth = 1
        sortButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        sortButton.semanticContentAttribute = .forceRightToLeft
        
        addCardImageView.addGestureRecognizer(scanCardRecognizer)
        addCardImageView.isUserInteractionEnabled = true
        
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
        
        // 驗證View的設定
        verifyingView.layer.cornerRadius = 12
        verifyingView.layer.masksToBounds = true
        
        // 到期通知按鈕
        let remindAttributedText = NSAttributedString(string: NSLocalizedString("CardUpdateExpirationRemindCheck", comment: "查看"),
                                       attributes:
                                        [.font : FontName.NotoSansTC_Medium.font.withSize(16.0),
                                         .underlineStyle: NSUnderlineStyle.single.rawValue,
                                         .foregroundColor: UIColor._4_E_61_A_7])
        verifyButton.setAttributedTitle(remindAttributedText, for: .normal)
    }
    
    private func initBinding() {
        viewModel.$userCards
            .receive(on: DispatchQueue.main)
            .sink { [weak self] usercards in
                guard let self else { return }
                reloadUI()
            }.store(in: &cancelSet)
        
        viewModel.verifyingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] number in
                guard let self else { return }
                setupVerifyingView(type: .verifying(number: "\(number)", totalCount: "\(viewModel.userCards.filter({$0.verificationStatus != .invalidation}).count)"))
                verifyingView.isHidden = false
            }.store(in: &cancelSet)
        
        viewModel.verifyingInternetErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                setupVerifyingView(type: .internetError)
                if let isRefreshing = tableView.refreshControl?.isRefreshing,
                   isRefreshing {
                    tableView.refreshControl?.endRefreshing()
                }
            }.store(in: &cancelSet)
        
        viewModel.isVerifySubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                if let isRefreshing = tableView.refreshControl?.isRefreshing,
                   isRefreshing {
                    tableView.refreshControl?.endRefreshing()
                }
            }.store(in: &cancelSet)
        
        viewModel.verifyFinishSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] updateFailedList in
                guard let self, checkOnTop() else { return }
                // 重新抓更新完的卡片資料
                viewModel.getCards()
                if updateFailedList.isEmpty {
                    setupVerifyingView(type: .finish(total: "\(viewModel.userCards.filter({$0.verificationStatus != .invalidation}).count)"))
                } else {
                    setupVerifyingView(type: .updateFail(count: "\(viewModel.updateFailedList.count)"))
                }
                
                if let isRefreshing = tableView.refreshControl?.isRefreshing,
                isRefreshing {
                    tableView.refreshControl?.endRefreshing()
                }
            }.store(in: &cancelSet)
        
        viewModel.errorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] errorMessage in
                guard let self = self else { return }
                self.showAlert(content: errorMessage)
            }.store(in: &cancelSet)
        
        viewModel.$sortType
            .receive(on: DispatchQueue.main)
            .sink { [weak self] type in
                guard let self else { return }
                var name = ""
                switch type {
                case .newToOld:
                    name = NSLocalizedString("NewToOld", comment: "")
                    break
                case .oldToNew:
                    name = NSLocalizedString("OldToNew", comment: "")
                    break
                }
                sortButton.setTitle(name, for: .normal)
                pickerView.setupSelecedName(selectName: name)
                viewModel.sortCards()
                tableView.reloadData()
            }.store(in: &cancelSet)
        
        pickerView.selectSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] item in
                guard let self else { return }
                if item.name == NSLocalizedString("NewToOld", comment: "") {
                    viewModel.sortType = .newToOld
                } else {
                    viewModel.sortType = .oldToNew
                }
            }.store(in: &cancelSet)
        
        /// 掃描卡片
        scanCardRecognizer.gesturePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                viewModel.callTabbarProtocol.showScanVC()
            }.store(in: &cancelSet)
        
        customNavigationBar.selectWalletSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                let seleWalletViewModel = SelectWalletViewModel(repository: viewModel.repository)
                let vc = SelectWalletViewController(viewModel: seleWalletViewModel)
                present(vc, animated: true)
            }.store(in: &cancelSet)
        
        customNavigationBar.refreshButtonClickSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                viewModel.verifyCards()
            }.store(in: &cancelSet)
        
        customNavigationBar.operationLogButtonClickSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                if let validatorManager = AppDelegate.shared.container.resolve(SecureURLValidatorManager.self) {
                    let vm = OperationLogViewModel(repository: viewModel.repository,
                                                   validatorManager: validatorManager)
                    let vc = OperationLogViewController(viewModel: vm)
                    navigationController?.pushViewController(vc, animated: true)
                }
            }.store(in: &cancelSet)
        
        viewModel.showRemindAlertSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self, checkOnTop() else { return }
                let viewModel = RemindAlertViewModel(repository: viewModel.repository, remindList: viewModel.remindList) {
                    self.viewModel.getCards()
                }
                let vc = RemindAlertViewController(viewModel: viewModel)
                vc.modalTransitionStyle = .crossDissolve
                vc.modalPresentationStyle = .overFullScreen
                navigationController?.present(vc, animated: false)
            }.store(in: &cancelSet)
    }
    
    /// 更新皮夾名稱
    private func updateWallet() {
        customNavigationBar.walletNameLabel.text = viewModel.repository.wallet.name
    }
    
    /// 檢查VC是否在最上層
    private func checkOnTop() -> Bool {
        return UIApplication.shared.topViewController is Self
    }
    
    private func reloadUI() {
        if viewModel.userCards.isEmpty {
            tableView.isHidden = true
            addCardView.isHidden = false
        } else {
            tableView.isHidden = false
            addCardView.isHidden = true
        }
        
        tableView.reloadData()
    }
    
    private func setupVerifyingView(type: VerifyType) {
        switch type {
        case .verifying(let number, let totalCount):
            verifyStatusLabel.text = String(format: NSLocalizedString("CardUpdating", comment: ""),
                                            number,
                                            totalCount)
            verifyStatusLabel.textColor = ._3_E_4_D_85
            verifyStatusImageView.image = .watingIcon
            verifyButton.isHidden = true
        case .internetError:
            verifyStatusLabel.text = NSLocalizedString("CardUpdateInterruption", comment: "")
            verifyStatusLabel.textColor = .D_07744
            verifyStatusImageView.image = .warn2
            verifyingView.isHidden = false
            verifyButton.isHidden = true
        case .finish(let total):
            // 如果網路錯誤就不刷新UI
            if verifyStatusLabel.text == NSLocalizedString("CardUpdateInterruption", comment: "") {
                return
            }
            
            verifyStatusLabel.text = String(format: NSLocalizedString("CardUpdateFinish", comment: ""), total, total)
            verifyStatusLabel.textColor = ._25_A_75_B
            verifyStatusImageView.image = .check2
            verifyingView.isHidden = false
            verifyButton.isHidden = true
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.5) {
                self.verifyingView.isHidden = true
            }
        case .updateFail(let count):
            // 更新失敗
            verifyStatusLabel.text = String(format: NSLocalizedString("CardUpdateExpirationRemind", comment: "憑證更新未完成 %@ 張"), count)
            verifyStatusLabel.textColor = .D_07744
            verifyStatusImageView.image = .warn2
            verifyingView.isHidden = false
            verifyButton.isHidden = false
        }
    }
    
    @objc private func handleRefreshControl() {
        viewModel.verifyCards()
    }
    
    @IBAction func onClickRemind(_ sender: Any) {
        let viewModel = RemindAlertViewModel(repository: viewModel.repository, remindList: viewModel.updateFailedList) {
            self.viewModel.getCards()
        }
        let vc = RemindAlertViewController(viewModel: viewModel)
        vc.modalTransitionStyle = .crossDissolve
        vc.modalPresentationStyle = .overFullScreen
        navigationController?.present(vc, animated: false)
    }
    
    @IBAction func onClickCloseVerifying(_ sender: Any) {
        Task {
            verifyingView.isHidden = true
            // 如果網路錯誤要重新判斷是否有網路，有的話要再次驗證
            if verifyStatusLabel.text == NSLocalizedString("CardUpdateInterruption", comment: "") {
                if await viewModel.checkNetworkStatus() {
                    viewModel.verifyCards()
                }
            }
        }
    }
    
    /**順序排序*/
    @IBAction func onClickFilter(_ sender: Any) {
        present(pickerView, animated: true)
    }
}

// MARK: UITableViewDataSource, UITableViewDelegate
extension CardOverviewViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.userCards.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(NormalCardTableViewCell.self, forIndexPath: indexPath)
        let cellData = viewModel.userCards[indexPath.row]
        cell.setupCell(cellData)
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let viewModel = CardInfomationViewModel(verifiableManager: viewModel.repository.verifiableManager,
                                                databaseManager: viewModel.repository.databaseManager,
                                                verifiableCredential: viewModel.userCards[indexPath.row],
                                                repository: viewModel.repository, biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!)
        let vc = CardInfomationViewController(viewModel: viewModel)
        navigationController?.pushViewController(vc, animated: true)
    }
}
