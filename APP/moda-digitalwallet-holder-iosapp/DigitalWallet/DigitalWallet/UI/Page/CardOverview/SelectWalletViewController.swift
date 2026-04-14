//
//  SelectWalletViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class SelectWalletViewController: BotttomSheetViewController {
    
    @IBOutlet weak var createWalletButton: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var backgroundView: UIView!
    @IBOutlet weak var shadowView: UIView!
    @IBOutlet weak var contentView: UIView!
    
    let viewModel: SelectWalletViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: SelectWalletViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "SelectWalletViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupData()
        initBinding()
    }
    
    override func viewDidLayoutSubviews() {
        initView()
    }
    
    private func setupData() {
        tableView.delegate = self
        tableView.dataSource = self
        tableView.separatorColor = .DEDEDE
        tableView.registerWithNib(SelectWalletTableViewCell.self)
    }
    
    private func initBinding() {
        viewModel.logoutSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                toLogin(loginAction: .normal)
            }.store(in: &cancelSet)
    }
    
    private func initView() {
        backgroundView.layer.cornerRadius = 16
        createWalletButton.layer.cornerRadius = createWalletButton.frame.height / 2
        
        shadowView.layer.shadowColor = UIColor.C_6_C_6_C_6.cgColor
        shadowView.layer.shadowOpacity = 1
        shadowView.layer.shadowOffset = CGSize(width: 0, height: 0)
        shadowView.layer.shadowRadius = 5
        shadowView.layer.cornerRadius = 10
        shadowView.backgroundColor = .clear
        
        contentView.backgroundColor = .white
        contentView.layer.cornerRadius = 10
        contentView.clipsToBounds = true
    }
    
    @IBAction func onClickCreateWallet(_ sender: Any) {
        toLogin(loginAction: .createWallet)
    }
    
    @IBAction func onClickClose(_ sender: Any) {
        dismiss(animated: true)
    }
    
    private func toLogin(loginAction: LoginViewModel.LoginActionType) {
        let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
        let loginViewModel = LoginViewModel(loginRepository: loginRepository, loginAction: loginAction)
        let loginVC = LoginViewController(viewModel: loginViewModel)
        let navi = CustomNavigationController(rootViewController: loginVC)
        UIApplication.shared.windows.first?.rootViewController = navi
        UIApplication.shared.windows.first?.makeKeyAndVisible()
    }
}

extension SelectWalletViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel.wallets.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(SelectWalletTableViewCell.self, forIndexPath: indexPath)
        cell.nameLabel.text = viewModel.wallets[indexPath.row].name
        if viewModel.repository.wallet.name == viewModel.wallets[indexPath.row].name {
            cell.radioImageView.image = .radioButtonEnableFill
        } else {
            cell.radioImageView.image = .radioButtonEnable
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        viewModel.selectedWallet(wallet: viewModel.wallets[indexPath.row])
    }
}
