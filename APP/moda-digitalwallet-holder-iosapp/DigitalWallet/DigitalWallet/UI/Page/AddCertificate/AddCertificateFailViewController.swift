//
//  AddCertificateFailViewController.swift
//  DigitalWallet
//

import UIKit

class AddCertificateFailViewController: BaseViewController {
    @IBOutlet weak var confirmButton: UIButton!
    @IBOutlet weak var timeLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupView()
    }
    
    private func setupView() {
        confirmButton.layer.cornerRadius = confirmButton.frame.height / 2
        let today = Date()
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone.current
        formatter.dateFormat = "yyyy / MM / dd  HH:mm:ss"
        timeLabel.text = formatter.string(from: today)
    }

    @IBAction func onClickConfirm(_ sender: Any) {
        dismiss(animated: true) {
            CustomTabBarController.selectIndexSubject.send(0)
        }
    }
    
}
