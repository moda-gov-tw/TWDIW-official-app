//
//  SearchCertificatesViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class SearchCertificatesViewController: BaseViewController {
    @IBOutlet weak var searchTextField: SearchTextField!
    @IBOutlet weak var searchLogTableView: UITableView!
    @IBOutlet weak var noSearchResultsView: UIView!
    @IBOutlet weak var notFoundLabel: UILabel!
    private let viewModel: SearchCertificatesViewModel
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: SearchCertificatesViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "SearchCertificatesViewController", bundle: nil)
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
    
    private func setupNavigation() {
        navigationItem.leftBarButtonItem = nil
    }

    private func setupView() {
        searchTextField.delegate = self
        searchTextField.leftIconSize = 24
        searchTextField.rightIconSize = 20
        searchTextField.textPadding = UIEdgeInsets(top: 12,
                                                   left: 52,
                                                   bottom: 12,
                                                   right: 48)
        searchTextField.setLeftImage(UIImage.searchCertificates24)
        searchTextField.setBorderColor(borderColor: UIColor.DEDEDE.cgColor)
        searchTextField.setAttributedPlaceHolder(NSLocalizedString("SearchCertificates", comment: ""),
                                                 font: FontName.NotoSansTC_Regular.font.withSize(16),
                                                 color: ._464646)
    }
    
    private func setupTableView() {
        searchLogTableView.delegate = self
        searchLogTableView.dataSource = self
        searchLogTableView.clipsToBounds = true
        searchLogTableView.separatorStyle = .none
        searchLogTableView.registerWithNib(SearchTitleTableViewCell.self)
        searchLogTableView.registerWithNib(SearchCertificatesLogTableViewCell.self)
        searchLogTableView.registerWithNib(CertificateTableViewCell.self)
    }
    
    private func dataBinding() {
        viewModel.reloadTableViewSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] type in
                guard let self = self else { return }
                searchLogTableView.isHidden = (type != .show)
                noSearchResultsView.isHidden = (type == .show)
                switch type {
                case .show:
                    searchLogTableView.reloadData()
                    break
                case .hidden(let str):
                    notFoundLabel.text = str
                    break
                }
            }.store(in: &cancelSet)
        
        viewModel.popPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self else { return }
                dismissLoading()
                navigationController?.popViewController(animated: true)
            }.store(in: &cancelSet)
        
        NotificationCenter.default.publisher(for: UIResponder.keyboardWillHideNotification)
            .receive(on: DispatchQueue.main)
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                setupTextfieldBorder(true)
            }.store(in: &cancelSet)
        
        NotificationCenter.default.publisher(for: UIResponder.keyboardWillShowNotification)
            .receive(on: DispatchQueue.main)
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                setupTextfieldBorder(false)
            }.store(in: &cancelSet)
        
        viewModel.stopLoadingSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                dismissLoading()
            }.store(in: &cancelSet)
    }
    
    
    private func setupTextfieldBorder(_ keyboardHide: Bool) {
        guard keyboardHide else {
            searchTextField.setBorderColor(borderColor: UIColor._4_E_61_A_7.cgColor)
            initCleanTextFieldButton()
            return
        }
        searchTextField.rightView = nil
        searchTextField.setBorderColor(borderColor: UIColor.DEDEDE.cgColor)
        viewModel.saveSearchCertificatesLog(searchString: searchTextField.text ?? "")
    }
    
    @IBAction func popAction(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
    
    @IBAction func editingChange(_ sender: Any) {
        viewModel.searchString = searchTextField.text
    }
}

// 建立RightButton
extension SearchCertificatesViewController {
    private func initCleanTextFieldButton() {
        let button = UIButton(type: .custom)
        button.setImage(UIImage.cleanSearchCertificates20, for: .normal)
        button.frame = CGRect(x: 0, y: 0, width: 20, height: 20)
        button.contentMode = .scaleAspectFit
        button.addTarget(self, action: #selector(cleanTextField), for: .touchUpInside)
        
        searchTextField.rightView = button
        searchTextField.rightViewMode = .always
    }
    
    @objc private func cleanTextField() {
        searchTextField.text = ""
        viewModel.searchString = ""
    }
}

extension SearchCertificatesViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.tableviewCell.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let type = viewModel.tableviewCell[indexPath.row]
        switch type {
        case .searchTitle(let title):
            let cell = tableView.dequeueReusableCell(SearchTitleTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(str: title)
            return cell
        case .searchCertificateLog(let data):
            let cell = tableView.dequeueReusableCell(SearchCertificatesLogTableViewCell.self, forIndexPath: indexPath)
            cell.setupCell(data: data)
            cell.onClickDelete = { [weak self] data in
                guard let self = self else { return }
                viewModel.deleteSearchCertificatesLog(searchCertificateLog: data)
            }
            return cell
        case .searchCertificateFavoriteResult(let data):
            let cell = tableView.dequeueReusableCell(CertificateTableViewCell.self, forIndexPath: indexPath)
            cell.setupMyFavoriteCell(data: data)
            cell.onClickStar = { [weak self] in
                guard let self = self else { return }
                switch $0 {
                case .added(let data):
                    viewModel.deleteFavoriteListDB(item: data)
                    break
                default:
                    break
                }
            }
            return cell
        case .searchCertificateResult(let item):
            let cell = tableView.dequeueReusableCell(CertificateTableViewCell.self, forIndexPath: indexPath)
            cell.setupAuthCell(data: item)
            cell.onClickStar = { [weak self] in
                guard let self else { return }
                switch $0 {
                case .notAdded(let data):
                    viewModel.saveFavoriteListDB(item: data)
                    break
                default:
                    break
                }
            }
            return cell
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let type = viewModel.tableviewCell[indexPath.row]
        switch type {
        case .searchCertificateLog(let data):
            searchTextField.text = data.name
            viewModel.searchString = searchTextField.text
            break
        case .searchCertificateFavoriteResult(let data):
            showLoading(style: .white)
            viewModel.getDeepLinkAndTransactionId(clickData: .favorite(data))
            break
        case .searchCertificateResult(let data):
            showLoading(style: .white)
            viewModel.getDeepLinkAndTransactionId(clickData: .normal(data))
            break
        default:
            break
        }
    }
}

extension SearchCertificatesViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        view.endEditing(true)
        return true
    }
}
