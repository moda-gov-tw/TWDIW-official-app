//
//  ShowCertificatesViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class ShowCertificatesViewController: BaseViewController {
    
    @IBOutlet weak var searchCertificatesTextField: SearchTextField!
    @IBOutlet weak var authorizationListTableView: UITableView!
    
    private let viewModel: ShowCertificatesViewModel
    private let refreshControl = UIRefreshControl()
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: ShowCertificatesViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "ShowCertificatesViewController", bundle:  nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupNavigation()
        setupView()
        setupTableView()
        dataBinding()
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        getVerifiablePresentationList()
    }
    
    private func setupNavigation() {
        navigationItem.leftBarButtonItem = nil
        title = NSLocalizedString("ShowCertificates", comment: "")
        navigationController?.navigationBar.titleTextAttributes = [
            .font: FontName.NotoSansTC_Bold.font.withSize(18),
            .foregroundColor: UIColor._2_E_3_A_64
        ]
    }
    
    private func setupView() {
        searchCertificatesTextField.delegate = self
        searchCertificatesTextField.leftIconSize = 24
        searchCertificatesTextField.textPadding = UIEdgeInsets(top: 12,
                                                               left: 44,
                                                               bottom: 12,
                                                               right: 16)
        searchCertificatesTextField.setBorderColor(borderColor: UIColor.DEDEDE.cgColor)
        searchCertificatesTextField.setLeftImage(UIImage.searchCertificates24)
        searchCertificatesTextField.setAttributedPlaceHolder(NSLocalizedString("SearchCertificates", comment: ""),
                                                             font: FontName.NotoSansTC_Regular.font.withSize(16),
                                                             color: ._464646)
    }
    
    private func setupTableView() {
        authorizationListTableView.delegate = self
        authorizationListTableView.dataSource = self
        authorizationListTableView.clipsToBounds = true
        authorizationListTableView.separatorStyle = .none
        authorizationListTableView.registerWithNib(MyFavoriteListTitleTableViewCell.self)
        authorizationListTableView.registerWithNib(HintClickTableViewCell.self)
        authorizationListTableView.registerWithNib(QuickAuthTitleTableViewCell.self)
        authorizationListTableView.registerWithNib(CertificateTableViewCell.self)
        
        refreshControl.addTarget(self, action: #selector(onPullToRefresh), for: .valueChanged)
        authorizationListTableView.refreshControl = refreshControl
    }
    
    private func dataBinding() {
        viewModel.reloadTableViewSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                authorizationListTableView.reloadData()
            }.store(in: &cancelSet)
        
        viewModel.stopLoadingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                dismissLoading()
                refreshControl.endRefreshing()
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
    
    @objc
    private func onPullToRefresh() {
        viewModel.getCertificates()
    }
    
    private func getVerifiablePresentationList() {
        if viewModel.vpItems.isEmpty {
            showLoading(style: .white)
            viewModel.getCertificates()
        } else {
            viewModel.getFavortieListDB()
        }
    }
    
    func regenerateVP05() {
        showLoading(style: .white)
        viewModel.vp05RegeneratePassthroughSubject.send()
    }
}

extension ShowCertificatesViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.tableviewCell.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let type = viewModel.tableviewCell[indexPath.row]
        switch type {
        case .myFavoriteTitle(let title, let arrow):
            let cell = tableView.dequeueReusableCell(MyFavoriteListTitleTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(title: title, arrow: arrow)
            cell.onClickExpand = { [weak self] in
                guard let self = self else { return }
                viewModel.myFavoriteExpand.toggle()
                viewModel.setupTableViewCellType()
            }
            return cell
        case .hintClick(let content):
            let cell = tableView.dequeueReusableCell(HintClickTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(content: content)
            return cell
        case .myFavorite(let favoriteData):
            let cell = tableView.dequeueReusableCell(CertificateTableViewCell.self, forIndexPath: indexPath)
            cell.setupMyFavoriteCell(data: favoriteData)
            cell.onClickStar = { [weak self] in
                guard let self = self else { return }
                switch $0 {
                case .added(let data):
                    viewModel.deleteFavoriteListDB(item: data)
                    break
                default: break
                }
            }
            return cell
        case .quickAuthTitle(let title, let arrow):
            let cell = tableView.dequeueReusableCell(QuickAuthTitleTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(title: title, arrow: arrow)
            cell.onClickExpand = { [weak self] in
                guard let self = self else { return }
                viewModel.quickAuthExpand.toggle()
                viewModel.setupTableViewCellType()
            }
            return cell
        case .quickAuthList(let authData):
            let cell = tableView.dequeueReusableCell(CertificateTableViewCell.self, forIndexPath: indexPath)
            cell.setupAuthCell(data: authData)
            cell.onClickStar = { [weak self] in
                guard let self else { return }
                switch $0 {
                case .notAdded(let data):
                    viewModel.saveFavoriteListDB(item: data)
                    break
                default: break
                }
            }
            return cell
                
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let type = viewModel.tableviewCell[indexPath.row]
        switch type {
        case .myFavorite(let data):
            showLoading()
            viewModel.getDeepLinkAndTransactionId(clickData: .favorite(data))
            break
        case .quickAuthList(let data):
            showLoading()
            viewModel.getDeepLinkAndTransactionId(clickData: .normal(data))
            break
        default: break
        }
    }
    
}

extension ShowCertificatesViewController: UITextFieldDelegate {
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
        let viewModel = SearchCertificatesViewModel(repository: viewModel.repository,
                                                    parseLinkManager: viewModel.parseLinkManager,
                                                    vpItems: viewModel.vpItems)
        let vc = SearchCertificatesViewController(viewModel: viewModel)
        vc.isHideNavigationBar = true
        navigationController?.pushViewController(vc, animated: true)
        return false
    }
}
