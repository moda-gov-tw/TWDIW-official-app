//
//  AddCertificateViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class AddCertificateViewController: BaseViewController {
    
    @IBOutlet weak var searchTextField: SearchTextField!
    @IBOutlet weak var applyCertificateListLabel: UILabel!
    @IBOutlet weak var certificateListTableView: UITableView!
    
    private let viewModel: AddCertificateViewModel
    private let refreshControl = UIRefreshControl()
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: AddCertificateViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "AddCertificateViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        dataBinding()
        setupNavigation()
        setupView()
        setupTableView()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getVerifiableCredentialList()
    }
    
    private func setupNavigation() {
        navigationItem.leftBarButtonItem = nil
        title = NSLocalizedString("AddCertificate", comment: "")
    }
    
    private func setupView() {
        applyCertificateListLabel.font = FontName.NotoSansTC_Bold.font
        applyCertificateListLabel.text = NSLocalizedString("ApplyCertificateList", comment: "")
        searchTextField.delegate = self
        searchTextField.leftIconSize = 24
        searchTextField.textPadding = UIEdgeInsets(top: 12,
                                                   left: 44,
                                                   bottom: 12,
                                                   right: 44)
        searchTextField.setBorderColor(borderColor: UIColor.DEDEDE.cgColor)
        searchTextField.setLeftImage(UIImage.searchKeyWord)
        searchTextField.setAttributedPlaceHolder(NSLocalizedString("InputKeyWord", comment: ""),
                                                 font: FontName.NotoSansTC_Regular.font.withSize(16),
                                                 color: ._464646)
    }
    
    private func setupTableView() {
        certificateListTableView.delegate = self
        certificateListTableView.dataSource = self
        certificateListTableView.clipsToBounds = false
        certificateListTableView.separatorStyle = .none
        certificateListTableView.registerWithNib(ApplyCardListTableViewCell.self)
        
        refreshControl.addTarget(self, action: #selector(onPullToRefresh), for: .valueChanged)
        certificateListTableView.refreshControl = refreshControl
    }
    
    private func dataBinding() {
        viewModel.$vcItems
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self = self else { return }
                certificateListTableView.reloadData()
            }.store(in: &cancelSet)
        
        viewModel.stopLoadingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                dismissLoading()
                refreshControl.endRefreshing()
            }.store(in: &cancelSet)
        
        viewModel.toWebViewSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (urlString, name) in
                guard let self = self else { return }
                let title = String(format: NSLocalizedString("addCertificateOpenWebTitle", comment: "%@線上申請"), name)
                checkNetworkAndToWebView(urlString, title: title, viewModel: AddCertificateWebViewViewModel(parseLinkManager: viewModel.parseLinkManager), isShowUrlOnTitle: false)
            }.store(in: &cancelSet)
        
        viewModel.toOpenURLSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] (urlString, name) in
                guard let self else { return }
                let content = String(format: NSLocalizedString("OpenURLReminder", comment: ""), name)
                askAlert(title: NSLocalizedString("ReminderMessage", comment: "提醒訊息"),
                         content: content) { isAgree in
                    if isAgree,
                       let url = URL(string: urlString),
                       UIApplication.shared.canOpenURL(url) {
                        UIApplication.shared.open(url)
                    }
                }
            }.store(in: &cancelSet)
        
        viewModel.errorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] type in
                guard let self = self else { return }
                switch type {
                case .apiError(let content):
                    dismissLoading()
                    refreshControl.endRefreshing()
                    showAlert(content: content)
                    break
                @unknown default: break
                }
            }.store(in: &cancelSet)
    }
    
    private func getVerifiableCredentialList() {
        if viewModel.vcItems.isEmpty {
            showLoading(style: .white)
            viewModel.getCertificates()
        }
    }
    
    @objc
    private func onPullToRefresh() {
        viewModel.getCertificates()
    }
    
    @IBAction func clickOnScanButton(_ sender: Any) {
        viewModel.callTabbarProtocol.showScanVC()
    }
}


extension AddCertificateViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.vcItems.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(ApplyCardListTableViewCell.self, forIndexPath: indexPath)
        let cellData = viewModel.vcItems[indexPath.row]
        cell.setupCell(cellData, imageLoadManager: viewModel.imageLoadManager)
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let cellData = viewModel.vcItems[indexPath.row]
        viewModel.getVCLink(checkData: cellData)
    }
}


extension AddCertificateViewController: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        let viewModel = SearchAddCerificateViewModel(repository: viewModel.repository,
                                                     parseLinkManager: viewModel.parseLinkManager,
                                                     imageLoadManager: viewModel.imageLoadManager,
                                                     vcItems: viewModel.vcItems)
        let vc = SearchAddCerificateViewController(viewModel: viewModel)
        vc.isHideNavigationBar = true
        navigationController?.pushViewController(vc, animated: true)
        return false
    }
}
