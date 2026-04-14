//
//  SettingViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class SettingViewController: BaseViewController {
    @IBOutlet weak var tableViewSetting: UITableView!
    @IBOutlet weak var versionLabel: UILabel!
    
    let viewModel: SettingViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: SettingViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "SettingViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        setupData()
        setupVersion()
        initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    private func initView() {
        navigationItem.leftBarButtonItem = nil
        title = NSLocalizedString("Personal", comment: "")
        navigationController?.navigationBar.titleTextAttributes = [
            .font: FontName.NotoSansTC_Bold.font.withSize(18),
            .foregroundColor: UIColor._2_E_3_A_64
        ]
    }
    
    private func setupVersion() {
        let verisonInformation = NSLocalizedString("VersionInformation", comment: "") // 版本資訊
        let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
        let versionBuild = "20251128_01"
        let target = {
            switch Config.environmentType {
            case .PROD:
                return ""
            }
        }()
        let connectionEnvironment = {
            switch Config.environmentType {
            case .PROD:
                return ""
            }
        }()
        versionLabel.text = verisonInformation
            .appending(" ")
            .appending(version)
            .appending(target)
            .appending(connectionEnvironment)
            .appending("+")
            .appending(versionBuild)
    }
    
    private func setupData() {
        tableViewSetting.separatorStyle = .none
        tableViewSetting.dataSource = self
        tableViewSetting.delegate = self
        tableViewSetting.registerWithNib(SettingTableViewCell.self)
    }
    
    private func initBinding() {
        viewModel.pushToPersonalSettingSubject
            .receive(on: DispatchQueue.main)
            .sink { [unowned self] in
                let container = AppDelegate.shared.container
                let personalManagementVC = PersonalManagementViewController(
                    viewModel: PersonalManagementViewModel(verifiableManager: container.resolve(VerifiableManager.self)!,
                                                           repository: self.viewModel.repository))
                self.navigationController?.pushViewController(personalManagementVC, animated: true)
            }.store(in: &cancelSet)
        
        viewModel.questionCenterSubject
            .receive(on: DispatchQueue.main)
            .sink { [unowned self] in
                let vc = QuestionCenterViewController(viewModel: QuestionCenterViewModel())
                navigationController?.pushViewController(vc, animated: true)
            }.store(in: &cancelSet)
        
        viewModel.openURLSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] url in
                guard let self else { return }
                checkNetworkAndToWebView(url.absoluteString, title: NSLocalizedString("DigitalWalletOfficialWebsiteTitle", comment: "數位憑證皮夾官網"), isShowUrlOnTitle: true)
            }.store(in: &cancelSet)
        
        viewModel.doLogoutSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                
                askAlert(title: NSLocalizedString("Logout", comment: ""), content: NSLocalizedString("IsWantToLogout", comment: ""), complete: {
                    result in
                    
                    /*確定登出*/
                    if result{
                        FunctionUtil.shared.logout()
                    }
                })
            }.store(in: &cancelSet)
        
        viewModel.pushToWalletSettingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                let viewModel = WalletSettingViewModel(repository: viewModel.repository, biometricVerifyManager: AppDelegate.shared.container.resolve(BiometricVerifyManager.self)!)
                let walletSettingVC = WalletSettingViewController(viewModel: viewModel)
                self.navigationController?.pushViewController(walletSettingVC, animated: true)
            }.store(in: &cancelSet)
    }
}

extension SettingViewController: UITableViewDelegate, UITableViewDataSource{
    /**每組共有多少列*/
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.settingItems.count
    }
    
    /**UITableView每列資訊*/
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(SettingTableViewCell.self, forIndexPath: indexPath)
        let settingItem: SettingItem = viewModel.settingItems[indexPath.row]
        cell.setItemStyle(itemName: settingItem.itemName, style: settingItem.type, cellStyle: settingItem.styleType)
        return cell
    }
    
    /**點選列表資料*/
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel.doAction(type: viewModel.settingItems[indexPath.row].type)
    }
}
