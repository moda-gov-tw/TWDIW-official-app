//
//  PersonalManagementViewController.swift
//  DigitalWallet
//  個人化管理
//

import UIKit
import Combine

class PersonalManagementViewController: BaseViewController {
    @IBOutlet weak var tableView: UITableView!
    
    let viewModel: PersonalManagementViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: PersonalManagementViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "PersonalManagementViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    private func initView() {
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(SettingTableViewCell.self)
        backButtonType = .normal
        title = NSLocalizedString("PersonalManagement", comment: "")
    }
    
    private func initBinding() {
        viewModel.openSettingSubject
            .receive(on: DispatchQueue.main)
            .sink {_ in
                /*開啟OS設定*/
                let url = if #available(iOS 16.0, *) {
                    URL(string: UIApplication.openNotificationSettingsURLString)
                } else {
                    URL(string: UIApplication.openSettingsURLString)
                }
                
                guard let url = url else{
                    return
                }
                
                UIApplication.shared.open(url)
            }.store(in: &cancelSet)
        
        viewModel.autoLogoutSettinSubject
            .receive(on: DispatchQueue.main)
            .sink {[unowned self] _ in
                let autoLogoutSettingViewModel = AutoLogoutSettingViewModel(repository: viewModel.repository)
                let vc = AutoLogoutSettingViewController(viewModel: autoLogoutSettingViewModel)
                
                navigationController?.pushViewController(vc, animated: true)
            }.store(in: &cancelSet)
        
        /*背景切回前景*/
        if let scene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
           let sceneDelegate = scene.delegate as? SceneDelegate {
            sceneDelegate.isEnterForegroundSubject.receive(on: DispatchQueue.main).sink { [unowned self] in
                
            }.store(in: &cancelSet)
        }
    }
    
    @objc private func onClickSwitch(_ sender: UIButton) {
        viewModel.doAction(type: viewModel.settingItems[sender.tag].type)
    }
}

extension PersonalManagementViewController: UITableViewDelegate, UITableViewDataSource{
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.settingItems.count
    }
   
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(SettingTableViewCell.self, forIndexPath: indexPath)
        
        let item = viewModel.settingItems[indexPath.row]
        
        cell.setItemStyle(itemName: item.itemName, style: item.type)
        cell.switchButton.tag = indexPath.row
        cell.switchButton.addTarget(self, action: #selector(onClickSwitch(_:)), for: .touchUpInside)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        viewModel.doAction(type: viewModel.settingItems[indexPath.row].type)
    }
}
