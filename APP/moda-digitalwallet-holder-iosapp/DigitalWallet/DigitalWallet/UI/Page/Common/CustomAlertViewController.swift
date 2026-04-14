//
//  CustomAlertViewController.swift
//  DigitalWallet
//

import UIKit

class CustomAlertViewController: UIViewController {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var rightButton: UIButton!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var alertView: UIView!
    
    typealias Completion = () -> Void
    
    var alertTitle: String
    var alertContent: String
    var leftButtonTitle: String?
    var rightButtonTitle: String?
    var leftButtonCompletion: Completion?
    var rightButtonCompletion: Completion?
    
    init(title: String, content: String, leftButtonTitle: String?, leftButtonCompletion: Completion?, rightButtonTitle: String?, rightButtonCompletion: Completion?) {
        self.alertTitle = title
        self.alertContent = content
        self.leftButtonTitle = leftButtonTitle
        self.leftButtonCompletion = leftButtonCompletion
        self.rightButtonTitle = rightButtonTitle
        self.rightButtonCompletion = rightButtonCompletion
        super.init(nibName: nil, bundle: nil)
        modalPresentationStyle = .overFullScreen
        modalTransitionStyle = .crossDissolve
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        initView()
        setupContent()
    }
    
    private func initView() {
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 18)
        leftButton.layer.cornerRadius = leftButton.frame.height * 0.5
        leftButton.layer.borderColor = UIColor._4_E_61_A_7.cgColor
        leftButton.layer.borderWidth = 1
        rightButton.layer.cornerRadius = rightButton.frame.height * 0.5
        alertView.layer.cornerRadius = 12
    }
    
    private func setupContent() {
        titleLabel.text = alertTitle
        contentLabel.text = alertContent
        
        if let leftButtonTitle {
            leftButton.setTitle(leftButtonTitle, for: .normal)
            leftButton.addTarget(self, action: #selector(onClickLeftButton), for: .touchUpInside)
        } else {
            leftButton.isHidden = true
        }
        
        if let rightButtonTitle {
            rightButton.setTitle(rightButtonTitle, for: .normal)
            rightButton.addTarget(self, action: #selector(onClickRightButton), for: .touchUpInside)
        } else {
            rightButton.isHidden = true
        }
    }
    
    @objc private func onClickLeftButton() {
        dismiss(animated: true) {
            self.leftButtonCompletion?()
        }
    }
    
    @objc private func onClickRightButton() {
        dismiss(animated: true) {
            self.rightButtonCompletion?()
        }
    }
}
