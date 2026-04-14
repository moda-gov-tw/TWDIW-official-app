//
//  OperationLogViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class OperationLogViewController: BaseViewController {

    @IBOutlet weak var buttonBackgroundView: UIView!
    @IBOutlet weak var authorizationRecordButton: UIButton!
    @IBOutlet weak var changeRecordButton: UIButton!
    @IBOutlet weak var sortButton: UIButton!
    @IBOutlet weak var updateTimeLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    let viewModel: OperationLogViewModel
    var cancelSet = Set<AnyCancellable>()
    let pickerView = CustomPickerViewController()
    
    init(viewModel: OperationLogViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "OperationLogViewController", bundle: nil)
    }
    
    @MainActor required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        setupData()
        initBinding()
        updateUpdateTime()
        viewModel.loadNextPage()
    }
    
    private func initView() {
        buttonBackgroundView.layer.cornerRadius = buttonBackgroundView.frame.height / 2
        authorizationRecordButton.layer.cornerRadius = authorizationRecordButton.frame.height / 2
        changeRecordButton.layer.cornerRadius = changeRecordButton.frame.height / 2
        
        sortButton.layer.cornerRadius = sortButton.frame.height * 0.5
        sortButton.layer.masksToBounds = true
        sortButton.layer.borderWidth = 1
        sortButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        sortButton.semanticContentAttribute = .forceRightToLeft
        
        let sortItems = [
            CustomPickerItem(name: NSLocalizedString("NewToOld", comment: ""), tag: "0"),
            CustomPickerItem(name: NSLocalizedString("OldToNew", comment: ""), tag: "1")
        ]
        pickerView.setupPicker(list: sortItems,
                               selectName: NSLocalizedString("NewToOld", comment: ""),
                               targetView: sortButton)
        pickerView.modalTransitionStyle = .crossDissolve
        pickerView.modalPresentationStyle = .overFullScreen
        
        navigationItem.rightBarButtonItem = UIBarButtonItem(image: UIImage(named: "Share"),
                                                            style: .plain,
                                                            target: self,
                                                            action: #selector(shareRecord))
    }
    
    private func setupData() {
        title = NSLocalizedString("OperationLog", comment: "")
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(CardLogTableViewCell.self)
    }
    
    private func initBinding() {
        viewModel.reloadPassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                tableView.reloadData()
                updateUpdateTime()
            }.store(in: &cancelSet)
        
        viewModel.shareRecordShbject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] url in
                guard let self else { return }
                let shareVC = UIActivityViewController(activityItems: [url], applicationActivities: nil)
                present(shareVC, animated: true)
            }.store(in: &cancelSet)
        
        pickerView.selectSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] item in
                guard let self else { return }
                sortButton.setTitle(item.name, for: .normal)
                pickerView.setupSelecedName(selectName: item.name)
                guard let tag = item.tag else { return }
                if tag == "0" {
                    viewModel.ascending = false
                } else {
                    viewModel.ascending = true
                }
                viewModel.resetData()
                viewModel.loadNextPage()
            }.store(in: &cancelSet)
    }
    
    @IBAction func onClickAuthorizationRecordButton(_ sender: UIButton) {
        changeSelectedRecordType(type: .authorizationRecord)
    }
    
    @IBAction func onClickChangeRecordButton(_ sender: UIButton) {
        changeSelectedRecordType(type: .changeRecord)
    }
    
    @IBAction func onClickSort(_ sender: Any) {
        present(pickerView, animated: true)
    }
    
    private func changeSelectedRecordType(type: RecordLogType) {
        authorizationRecordButton.backgroundColor = type == .authorizationRecord ? .white : .clear
        changeRecordButton.backgroundColor = type == .changeRecord ? .white : .clear
        viewModel.selected(type: type)
        viewModel.loadNextPage()
    }
    
    private func updateUpdateTime() {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .gregorian)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = .current
        formatter.dateFormat = "yyyy/MM/dd"
        let todayString = formatter.string(from: Date())
        updateTimeLabel.text = String(format: NSLocalizedString("UpdateTime", comment: ""), todayString)
    }
    
    @objc private func shareRecord() {
        viewModel.prepareToShare()
    }
}

// MARK: UITableViewDelegate, UITableViewDataSource
extension OperationLogViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        switch viewModel.selectedType {
        case .authorizationRecord:
            return viewModel.authorizationRecords.count
        case .changeRecord:
            return viewModel.changeRecords.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(CardLogTableViewCell.self, forIndexPath: indexPath)
        let data: CardRecord = viewModel.selectedType == .authorizationRecord ? viewModel.authorizationRecords[indexPath.row] : viewModel.changeRecords[indexPath.row]
        cell.setupCell(data)
        cell.onClickExpand = { [weak self] in
            guard let self else { return }
            viewModel.updateRecordExpand(indexPath: indexPath)
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        let count = viewModel.selectedType == .authorizationRecord ? viewModel.authorizationRecords.count : viewModel.changeRecords.count
        if indexPath.row == count - 1 {
            viewModel.loadNextPage()
        }
    }
}
