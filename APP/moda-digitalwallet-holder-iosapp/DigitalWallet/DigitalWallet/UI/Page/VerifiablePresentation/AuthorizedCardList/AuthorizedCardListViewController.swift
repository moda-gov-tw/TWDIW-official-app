//
//  AuthorizedCardListViewController.swift
//  DigitalWallet
//

import UIKit

class AuthorizedCardListViewController: BaseViewController {
    
    @IBOutlet weak var hintLabel: UILabel!
    @IBOutlet weak var tableView: UITableView!
    
    let viewModel: AuthorizedCardListViewModel
    
    init(viewModel: AuthorizedCardListViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "AuthorizedCardListViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupData()
        // Do any additional setup after loading the view.
    }
    
    private func setupData() {
        title = NSLocalizedString("AuthorizedCredentialsTitle", comment: "可授權的憑證清單")
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.separatorStyle = .singleLine
        tableView.separatorInset = .zero // 分隔線左右貼邊
        tableView.tableHeaderView = UIView(frame: .zero) // 移除頂部線
        tableView.registerWithNib(AuthorizedCardListTableViewCell.self)
        hintLabel.text = NSLocalizedString("AuthorizedCardListHint", comment: "")
    }
}


extension AuthorizedCardListViewController: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.needAuthorizedCardList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(AuthorizedCardListTableViewCell.self, forIndexPath: indexPath)
        let cellData = viewModel.needAuthorizedCardList[indexPath.row]
        cell.setupCell(cellData)
        return cell
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        
        // 檢查是否為最後一個 cell
        let lastRowIndex = tableView.numberOfRows(inSection: indexPath.section) - 1
        if indexPath.row == lastRowIndex {
            // 把 separator 推到底（看不到）
            cell.separatorInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: .greatestFiniteMagnitude)
        } else {
            // 其他 cell 正常顯示
            cell.separatorInset = tableView.separatorInset
        }
    }

}
