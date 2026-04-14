//
//  CustomWarningViewViewController.swift
//  DigitalWallet
//

import UIKit

class CustomWarningViewViewController: UIViewController {
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    
    typealias Completion = () -> Void
    
    var alertTitle: String
    var alertContent: String
    var buttonCompletion: Completion?
    
    init(title: String, content: String, buttonCompletion: Completion?) {
        self.alertTitle = title
        self.alertContent = content
        self.buttonCompletion = buttonCompletion
        
        super.init(nibName: nil, bundle: nil)
        modalPresentationStyle = .overFullScreen
        modalTransitionStyle = .crossDissolve
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupContent()
    }
    
    private func setupContent() {
        titleLabel.text = alertTitle
        contentLabel.text = alertContent
    }
    
    /**點擊確認*/
    @IBAction func onClickConfirm(_ sender: UIButton) {
        dismiss(animated: true) {
            self.buttonCompletion?()
        }
    }
}
