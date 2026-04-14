//
//  ContactCustomerServiceViewController.swift
//  DigitalWallet
//

import UIKit
import MessageUI

class ContactCustomerServiceViewController: BaseViewController {
    
    @IBOutlet weak var customerBackgroundView: UIView!
    
    @IBOutlet weak var phoneTitleLabel: UILabel!
    @IBOutlet weak var phoneContentLabel: UILabel!
    @IBOutlet weak var timeTitleLabel: UILabel!
    @IBOutlet weak var timeContentLabel: UILabel!
    @IBOutlet weak var emailTitleLabel: UILabel!
    @IBOutlet weak var emailContentButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        initView()
    }

    private func initView() {
        title = NSLocalizedString("ContactCustomerService", comment: "聯絡客服")
        backButtonType = .normal
        
        customerBackgroundView.layer.cornerRadius = 12.0
        customerBackgroundView.layer.shadowColor = UIColor.C_6_C_6_C_6.cgColor
        customerBackgroundView.layer.shadowOpacity = 0.25
        customerBackgroundView.layer.shadowOffset = CGSize(width: 0, height: 0)
        customerBackgroundView.layer.shadowRadius = 5
        
        phoneTitleLabel.text = NSLocalizedString("CustomerPhoneTitle", comment: "客服專線：")
        phoneContentLabel.text = NSLocalizedString("CustomerPhoneContent", comment: "請填入實際客服電話")
        timeTitleLabel.text = NSLocalizedString("CustomerTimeTitle", comment: "客服時間：")
        timeContentLabel.text = NSLocalizedString("CustomerTimeContent", comment: "請填入實際服務時間")
        emailTitleLabel.text = NSLocalizedString("CustomerEmailTitle", comment: "客服信箱")
        emailContentButton.setTitle(NSLocalizedString("ServiceEmail", comment: ""), for: .normal)
        emailContentButton.titleLabel?.font = FontName.NotoSansTC_Bold.font
        
    }
    
    @IBAction func clickOnEmail(_ sender: Any) {
        if MFMailComposeViewController.canSendMail() {
            let composeVC = MFMailComposeViewController()
            composeVC.mailComposeDelegate = self
            // 請替換為實際的客服信箱
            composeVC.setToRecipients(["support@example.com"])
            self.present(composeVC, animated: true)
        }else{
            self.showAlert(content: "發生錯誤，請確認寄件所需相關設定均已設妥。")
        }
    }
}


extension ContactCustomerServiceViewController: MFMailComposeViewControllerDelegate {
    func mailComposeController(_ controller: MFMailComposeViewController, didFinishWith result: MFMailComposeResult, error: Error?) {
        controller.dismiss(animated: true)
    }
}
