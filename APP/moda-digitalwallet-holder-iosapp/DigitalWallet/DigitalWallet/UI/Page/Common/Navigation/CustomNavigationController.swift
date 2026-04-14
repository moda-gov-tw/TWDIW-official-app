//
//  CustomNavigationController.swift
//  DigitalWallet
//

import UIKit

class CustomNavigationController: UINavigationController {
    
    init(rootViewController: UIViewController, isTransparent: Bool = false) {
        super.init(rootViewController: rootViewController)
        self.setupNavigationBarAppearance(isTransparent: isTransparent)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    func setupNavigationBarAppearance(isTransparent: Bool) {
        let customBarAppearance = isTransparent ? TransparentNavigationBarAppearance() : CustomNavigationBarAppearance()
        navigationBar.standardAppearance = customBarAppearance
        navigationBar.scrollEdgeAppearance = customBarAppearance
        navigationBar.compactAppearance = customBarAppearance
    }

    @objc func onClickGoBack() {
        popViewController(animated: true)
    }
    
    override func pushViewController(_ viewController: UIViewController, animated: Bool) {
        if (children.count >= 1) {
            let shouldShowTabBar: [UIViewController.Type] = [
                SearchCertificatesViewController.self,
                SearchAddCerificateViewController.self
            ]
            viewController.hidesBottomBarWhenPushed = !shouldShowTabBar.contains {
                viewController.isKind(of: $0)
            }
        }
        super.pushViewController(viewController, animated: animated)
    }
}
