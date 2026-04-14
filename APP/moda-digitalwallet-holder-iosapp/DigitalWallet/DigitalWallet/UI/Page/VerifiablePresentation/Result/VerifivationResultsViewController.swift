//
//  VerifivationResultsViewController.swift
//  DigitalWallet
//

import UIKit

class VerifivationResultsViewController: UIViewController {
    
    enum ResultState {
        case success
        case fail
    }
    
    @IBOutlet weak var stateImageView: UIImageView!
    @IBOutlet weak var confirmButton: UIButton!
    @IBOutlet weak var resultTitleLabel: UILabel!
    @IBOutlet weak var verifivationTimeLabel: UILabel!
    @IBOutlet weak var stateLabel: UILabel!
    @IBOutlet weak var verifyInfoTableView: UITableView!
    
    var resultState: ResultState = .success
    
    let viewModel: VerifivationResultsViewModel
    
    init(viewModel: VerifivationResultsViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "VerifivationResultsViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
    }
    
    private func initView() {
        confirmButton.layer.cornerRadius = confirmButton.frame.height / 2
        
        let resultSuccess = resultState == .success
        
        stateImageView.image = resultSuccess ? .success : .fail
        
        resultTitleLabel.text = NSLocalizedString(resultSuccess ? "VerifySuccess" : "VerifyFail", comment: "")
        
        verifivationTimeLabel.text = FunctionUtil.shared.dateFormat(format: FunctionUtil.shared.yyyyMMddHHMMSS, dateTime: Date())
        
        let successStateText = String(format: NSLocalizedString("VerifyState", comment: "您已將資料授權送出至 %@。"), viewModel.title)
        let failStateText = NSLocalizedString("VerifyStateFailMessage", comment: "資料授權失敗，請稍候再嘗試。")
        stateLabel.text = resultSuccess ? successStateText : failStateText
        setupTableView()
        verifyInfoTableView.separatorStyle = viewModel.resultList.count == 1 ? .none : .singleLine
        verifyInfoTableView.isHidden = !resultSuccess
        verifyInfoTableView.reloadData()
    }
    
    private func setupTableView() {
        verifyInfoTableView.dataSource = self
        verifyInfoTableView.delegate = self
        
        verifyInfoTableView.registerWithNib(VerifyResultTableViewCell.self)
    }
    
    /// 取得邊角類型
    private func getCorners(_ index: Int) -> CACornerMask {
        let totalCount = viewModel.resultList.count - 1
        
        if totalCount == 0 {
            return [.layerMaxXMaxYCorner, .layerMaxXMinYCorner, .layerMinXMaxYCorner, .layerMinXMinYCorner]
        } else if index == 0 {
            return [.layerMaxXMinYCorner, .layerMinXMinYCorner]
        } else if index == totalCount {
            return [.layerMaxXMaxYCorner, .layerMinXMaxYCorner]
        } else {
            return []
        }
    }

    @IBAction func onClickConfirm(_ sender: Any) {
        CustomTabBarController.selectIndexSubject.send(0)
        dismiss(animated: true)
    }
}

extension VerifivationResultsViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        viewModel.resultList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(VerifyResultTableViewCell.self, forIndexPath: indexPath)
        let cellType = viewModel.resultList[indexPath.row]
        
        switch cellType {
        case .verifyData(let cellData):
            cell.setupCell(cellData, vpName: viewModel.title, corners: getCorners(indexPath.row))
        case .customFields(let cellData):
            cell.setupCell(cellData, vpName: viewModel.title, corners: getCorners(indexPath.row))
        }
        return cell
    }
}
