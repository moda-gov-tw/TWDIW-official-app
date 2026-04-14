//
//  QuestionCenterViewController.swift
//  DigitalWallet
//

import UIKit
import Combine
import MessageUI

class QuestionCenterViewController: BaseViewController {
    private let viewModel: QuestionCenterViewModel
    
    @IBOutlet weak var tableView: UITableView!
    
    private var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: QuestionCenterViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "QuestionCenterViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        setupData()
    }
    
    private func initView() {
        title = NSLocalizedString("QuestionCenter", comment: "")
        backButtonType = .normal
    }
    
    private func setupData() {
        tableView.separatorStyle = .none
        tableView.registerWithNib(SettingTableViewCell.self)
        tableView.registerWithNib(ContactCustomerServiceTableViewCell.self)
    }
}

extension QuestionCenterViewController: UITableViewDataSource, UITableViewDelegate {
    /**每組共有多少列*/
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return viewModel.questionCenterItems.count
    }
    
    /**第indexPath.section組 第indexPath.row行顯示怎樣的cell*/
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(SettingTableViewCell.self, forIndexPath: indexPath)
        
        let settingItem: SettingItem = viewModel.questionCenterItems[indexPath.row]
        cell.setItemStyle(itemName: settingItem.itemName, style: settingItem.type, content: settingItem.content ?? "")
        
        return cell
    }
    
    /**點選列表資料*/
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        switch indexPath.row{
            /*常見問題*/
            case 0:
            checkNetworkAndToWebView(Config.CORPORTE_WEBSITE_QA_URL, title: NSLocalizedString("DigitalWalletOfficialWebsiteTitle", comment: "數位憑證皮夾官網"), isShowUrlOnTitle: true)
            /*問題回報*/
            case 1:
            if let url = URL(string: Config.QUESTION_REPORT_URL) {
                UIApplication.shared.open(url)
            }
            /*聯絡客服*/
            case 2:
            toContactCustomerServiceViewController()
            default:
                break
        }
    }
    
    /// 前往聯絡客服
    private func toContactCustomerServiceViewController() {
        let vc = ContactCustomerServiceViewController()
        navigationController?.pushViewController(vc, animated: true)
    }
    
}
