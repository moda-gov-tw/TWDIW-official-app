//
//  AutoLogoutSettingViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class AutoLogoutSettingViewController: BaseViewController {
    @IBOutlet weak var tableView: UITableView!
    
    let viewModel: AutoLogoutSettingViewModel
    var tableViewHeightConstraint: NSLayoutConstraint?
    
    init(viewModel: AutoLogoutSettingViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "AutoLogoutSettingViewController", bundle: nil)
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
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        
        tableViewHeightConstraint?.constant = tableView.contentSize.height
    }
    
    private func initView() {
        title = NSLocalizedString("AutoLogoutTime", comment: "")
        
        // Auto Layout 設定
        tableView.translatesAutoresizingMaskIntoConstraints = false
        tableViewHeightConstraint = tableView.heightAnchor.constraint(equalToConstant: 100)
        tableViewHeightConstraint?.isActive = true
        backButtonType = .normal
    }
    
    private func initBinding() {
        
    }
    
    /**設定元件*/
    private func setupData() {
        tableView.registerWithNib(TimeSelectTableViewCell.self)
        viewModel.selectItemCode = Int(viewModel.repository.wallet.autoLogout ?? 0)
    }
}

extension AutoLogoutSettingViewController: UITableViewDelegate, UITableViewDataSource{
    /**共有多少組*/
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    /**每組共有多少列*/
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.timeSelectItem.count
    }
    
    /**UITableView每列資訊*/
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(TimeSelectTableViewCell.self, forIndexPath: indexPath)
        
        let itemName: String = viewModel.timeSelectItem[indexPath.row]
        cell.itemNameLabel.text = itemName
        
        let itemCode = TimeSelectEnum.allCases[indexPath.row]
        
        if viewModel.selectItemCode == itemCode.rawValue{
            cell.radioImageView.image = .radioButtonEnableFill
        } else {
            cell.radioImageView.image = .radioButtonEnable
        }
        
        return cell
    }
    
    /**點擊item*/
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        
        let itemCode = TimeSelectEnum.allCases[indexPath.row]
        viewModel.selectItemCode = itemCode.rawValue
        
        viewModel.updateWalletAutoLogout(autoLogout: itemCode.rawValue)
        tableView.reloadData()
    }
}
