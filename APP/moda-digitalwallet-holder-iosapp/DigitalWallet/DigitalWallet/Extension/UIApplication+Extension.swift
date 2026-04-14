//
//  UIApplication+Extension.swift
//  DigitalWallet
//

import UIKit

extension UIApplication {
    var firstKeyWindow: UIWindow? {
        return connectedScenes
            .compactMap { $0 as? UIWindowScene }
            .first?.windows
            .filter { $0.isKeyWindow }
            .first
    }
    
    @objc
    var topViewController: UIViewController? {
        return firstKeyWindow?.rootViewController
            .flatMap { getTopViewController(from: $0) }
    }

    private func getTopViewController(from viewController: UIViewController) -> UIViewController? {
        if let tabBarVC = viewController as? UITabBarController {
            return tabBarVC.selectedViewController.flatMap { getTopViewController(from: $0) }
        }
        if let presentedVC = viewController.presentedViewController {
            return getTopViewController(from: presentedVC)
        }
        if let navigationVC = viewController as? UINavigationController {
            return navigationVC.visibleViewController.flatMap { getTopViewController(from: $0) }
        }
        return viewController
    }
}
