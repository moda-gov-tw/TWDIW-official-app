//
//  WelcomeViewController.swift
//  DigitalWallet
//

import UIKit

class WelcomeViewController: BaseViewController {
    
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var subtitleLabel: UILabel!
    @IBOutlet weak var startButton: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
    }

    private func initView() {
        titleLabel.text = Localization.Welcome.WelcomeVC_Title.localizedString
        
        let subtitle = Localization.Welcome.WelcomeVC_Subtitle.localizedString
        subtitleLabel.text = subtitle
        
        let startButtonTitle = Localization.Welcome.WelcomeVC_Start.localizedString
        startButton.setTitle(startButtonTitle, for: .normal)
        startButton.layer.cornerRadius = startButton.frame.height * 0.5
        
        isHideNavigationBar = true
    }

    @IBAction func onClickStart(_ sender: Any) {
        let previewVC = PreviewFeatureViewController(nibName: "PreviewFeatureViewController", bundle: nil)
        navigationController?.pushViewController(previewVC, animated: true)
    }
}
