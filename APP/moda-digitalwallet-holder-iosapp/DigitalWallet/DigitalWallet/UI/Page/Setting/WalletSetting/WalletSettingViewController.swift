//
//  WalletSettingViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class WalletSettingViewController: BaseViewController {
    @IBOutlet weak var tableView: UITableView!
    
    let viewModel: WalletSettingViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: WalletSettingViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "WalletSettingViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
        
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidBecomeActive), name: UIApplication.didBecomeActiveNotification, object: nil)
    }
    
    func initView() {
        title = NSLocalizedString("WalletSetting", comment: "皮夾設定")
        tableView.separatorStyle = .none
        tableView.delegate = self
        tableView.dataSource = self
        tableView.registerWithNib(SettingTableViewCell.self)
        backButtonType = .normal
    }
    
    func initBinding() {
        viewModel.verifyUserSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (isVerifySuccess, type) in
                guard let self else { return }
                if isVerifySuccess {
                    // 生物辨識成功
                    switch type {
                    case .changeWalletPinCode:
                        updatePinCode(isBiometric: true)
                        break
                    }
                } else {
                    // PinCode驗證
                    let enterPinCodeViewModel = EnterPinCodeViewModel(repository: viewModel.repository)
                    let enterPinCodeVC = EnterPinCodeViewController(viewModel: enterPinCodeViewModel)
                    enterPinCodeViewModel.veriflySuccessSubject
                        .receive(on: DispatchQueue.main)
                        .sink { [weak self] in
                            guard let self else { return }
                            switch type {
                            case .changeWalletPinCode:
                                updatePinCode(isBiometric: false)
                                break
                            }
                    }.store(in: &cancelSet)
                    self.navigationController?.pushViewController(enterPinCodeVC, animated: true)
                }
            }.store(in: &cancelSet)
    }
    
    /**使用者切回前景*/
    @objc func applicationDidBecomeActive(notification: NSNotification) {
        tableView.reloadData()//重新整理，判斷目前生物辨識狀態
    }
    
    /// 前往系統設定頁
    private func toSystemSetting() {
        if let settingsURL = URL(string: UIApplication.openSettingsURLString) {
            if UIApplication.shared.canOpenURL(settingsURL) {
                UIApplication.shared.open(settingsURL, options: [:], completionHandler: nil)
            }
        }
    }
}

extension WalletSettingViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel.settingItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(SettingTableViewCell.self, forIndexPath: indexPath)
        let item = viewModel.settingItems[indexPath.row]
        cell.setItemStyle(itemName: item.itemName, style: item.type, cellStyle: item.styleType)
        cell.onClickSwitchButton = { [weak self] style, isOn in
            guard let self else { return }
            switch style {
            case .settingBiometrics:
                if isOn {
                    askAlert(title: NSLocalizedString("BiometricSettings", comment: "生物辨識設定"),
                             content: NSLocalizedString("BiometricMessage", comment: "您確認要關閉生物辨識嗎?關閉後只能使用手機密碼或皮夾密碼登入。點選「確認」後將前往裝置設定頁面進行調整。")) { confirm in
                        if confirm {
                            self.toSystemSetting()
                        }
                    }
                } else {
                    askAlert(title: NSLocalizedString("BiometricSettings", comment: "生物辨識設定"),
                             content: NSLocalizedString("BiometricOpenMessage", comment: "點選「確認」後將前往裝置設定頁面進行調整。")) { confirm in
                        if confirm {
                            self.toSystemSetting()
                        }
                    }
                }
                break
            default:
                break
            }
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let type = viewModel.settingItems[indexPath.row].type
        viewModel.doAction(type: type)
    }
}

extension WalletSettingViewController{
    
    /// 修改皮夾密碼
    /// - Parameter isBiometric: 是否有生物辨識
    private func updatePinCode(isBiometric: Bool){
        let settingWalletPinCodeViewModel = SetWalletPinCodeViewModel(repository: viewModel.repository, isBiometric: isBiometric)
        
        let settingWalletPinCodeVC = CreateWalletWithPinCodeViewController(viewModel: settingWalletPinCodeViewModel)
        
        self.navigationController?.pushViewController(settingWalletPinCodeVC, animated: true)
    }
}
