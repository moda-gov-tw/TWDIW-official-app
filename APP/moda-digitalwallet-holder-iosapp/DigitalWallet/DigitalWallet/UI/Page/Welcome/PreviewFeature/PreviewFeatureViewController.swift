//
//  PreviewFeatureViewController.swift
//  DigitalWallet
//

import UIKit

class PreviewFeatureViewController: BaseViewController {
    
    private struct PreviewData {
        var previewImage: UIImage
        var title: String
        var content: String
    }
    
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var contentLabel: UILabel!
    @IBOutlet weak var leftButton: UIButton!
    @IBOutlet weak var pageControl: UIPageControl!
    
    private var previewData: [PreviewData] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()

        initView()
    }

    private func initView() {
        previewData = [
            PreviewData(previewImage: 
                            UIImage(named: ImageName.Welcome.PreviewStep1.rawValue)!,
                        title: 
                            Localization.Welcome.PreviewFeatureVC_Step1Title.localizedString,
                        content: 
                            Localization.Welcome.PreviewFeatureVC_Step1Content.localizedString),
            PreviewData(previewImage:
                            UIImage(named: ImageName.Welcome.PreviewStep2.rawValue)!,
                        title: 
                            Localization.Welcome.PreviewFeatureVC_Step2Title.localizedString,
                        content: 
                            Localization.Welcome.PreviewFeatureVC_Step2Content.localizedString),
            PreviewData(previewImage:
                            UIImage(named: ImageName.Welcome.PreviewStep3.rawValue)!,
                        title: 
                            Localization.Welcome.PreviewFeatureVC_Step3Title.localizedString,
                        content: 
                            Localization.Welcome.PreviewFeatureVC_Step3Content.localizedString)
        ]
        
        imageView.image = previewData.first?.previewImage
        titleLabel.text = previewData.first?.title
        contentLabel.text = previewData.first?.content
        
        let swipeLeft = UISwipeGestureRecognizer(target: self, action: #selector(onClickRight))
        swipeLeft.direction = .left
        let swipeRight = UISwipeGestureRecognizer(target: self, action: #selector(onClickLeft))
        swipeRight.direction = .right
        view.addGestureRecognizer(swipeLeft)
        view.addGestureRecognizer(swipeRight)
        view.isUserInteractionEnabled = true
        
        titleLabel.font = .boldNotoSansTCFontOfSize(fontSize: 20)
        contentLabel.font = .regularNotoSansTCFontOfSize(fontSize: 16)
        
        isHideNavigationBar = true
    }

    @IBAction func changePage(_ sender: UIPageControl) {
        UIView.transition(with: view, duration: 0.3, options: .transitionCrossDissolve) {
            self.imageView.image = self.previewData[sender.currentPage].previewImage
            self.titleLabel.text =  self.previewData[sender.currentPage].title
            self.contentLabel.text =  self.previewData[sender.currentPage].content
        }
    }
    // 第一頁時隱藏按鈕邏輯
    @objc @IBAction func onClickRight(_ sender: Any) {
        if pageControl.currentPage < 2 {
            pageControl.currentPage += 1
            changePage(pageControl)
        } else {
            let terms = [
                Terms(title: NSLocalizedString("LicenseTermsTitle", comment: ""),content: .License, isAgree: false),
                Terms(title: NSLocalizedString("PrivacyTermsTitle", comment: ""),content: .Privacy, isAgree: false)
            ]
            let userConsentViewModel = UserConsentViewModel(terms: terms)
            let userConsentVC = UserConsentViewController(viewModel: userConsentViewModel)
            navigationController?.pushViewController(userConsentVC, animated: true)
        }
    }
    
    @objc @IBAction func onClickLeft(_ sender: Any) {
        if pageControl.currentPage > 0 {
            pageControl.currentPage -= 1
            changePage(pageControl)
        } else {
            navigationController?.popViewController(animated: true)
        }
    }
    
}
