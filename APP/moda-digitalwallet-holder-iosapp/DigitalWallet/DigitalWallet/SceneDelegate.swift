//
//  SceneDelegate.swift
//  DigitalWallet
//

import UIKit
import Combine

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    
    var window: UIWindow?
    let isEnterForegroundSubject = PassthroughSubject<Void, Never>()
    private var privacyOverlay: UIView?
    
    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = (scene as? UIWindowScene) else { return }
        
        // 判斷是否透過cross app啟動
        if let urlContext = connectionOptions.urlContexts.first,
           let parseLinkManager = AppDelegate.shared.container.resolve(ParseLinkManager.self) {
            parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: urlContext.url, isShowFullScreen: false))
        }
        
        // 判斷是否透過Universal Link啟動
        if let userActivity = connectionOptions.userActivities.first,
           userActivity.activityType == NSUserActivityTypeBrowsingWeb,
           let url = userActivity.webpageURL,
           let parseLinkManager = AppDelegate.shared.container.resolve(ParseLinkManager.self) {
            parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: false))
        }
        
        let window = UIWindow(windowScene: windowScene)
        window.overrideUserInterfaceStyle = .light
        window.rootViewController = LaunchViewController(viewModel: LaunchViewModel(sqliteManager: AppDelegate.shared.container.resolve(SQLiteManager.self)!))
        window.makeKeyAndVisible()
        self.window = window
        /// 強制走新模式
        UserDefaultManager.shared.setObject(value: true, key: .New601iFlag)
    }
    
    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // This occurs shortly after the scene enters the background, or when its session is discarded.
        // Release any resources associated with this scene that can be re-created the next time the scene connects.
        // The scene may re-connect later, as its session was not necessarily discarded (see `application:didDiscardSceneSessions` instead).
    }
    
    func sceneDidBecomeActive(_ scene: UIScene) {
        hidePrivacyOverlay()
        guard let brightness = UserDefaultManager.shared.getObject(key: .Brightness, type: CGFloat.self) else { return }
        if brightness != -1 {
            UIScreen.main.brightness = 1
        }
        isEnterForegroundSubject.send()
    }
    
    func sceneWillResignActive(_ scene: UIScene) {
        guard let window = window else { return }
        showPrivacyOverlay(on: window)
        guard let brightness = UserDefaultManager.shared.getObject(key: .Brightness, type: CGFloat.self) else { return }
        if brightness != -1 {
            UIScreen.main.brightness = brightness
        }
    }
    
    /**後台進入前台*/
    func sceneWillEnterForeground(_ scene: UIScene) {
        isEnterForegroundSubject.send()
    }
    
    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // to restore the scene back to its current state.
    }
    
    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        guard let url = URLContexts.first?.url,
              let parseLinkManager = AppDelegate.shared.container.resolve(ParseLinkManager.self) else {
            return
        }
        parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: false))
    }
    
    func scene(_ scene: UIScene, continue userActivity: NSUserActivity) {
        if userActivity.activityType == NSUserActivityTypeBrowsingWeb,
           let url = userActivity.webpageURL,
           let parseLinkManager = AppDelegate.shared.container.resolve(ParseLinkManager.self) {
            parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: false))
        }
    }
}

// MARK: 退背遮罩處理
extension SceneDelegate {
    // MARK: - Overlay helpers
    private func showPrivacyOverlay(on window: UIWindow) {
        // 若已存在就先移除，避免重複疊加
        privacyOverlay?.removeFromSuperview()
        
        // 容器
        let container = UIView()
        container.isUserInteractionEnabled = false   // 避免遮擋手勢
        container.accessibilityLabel = "PrivacyOverlay"
        container.translatesAutoresizingMaskIntoConstraints = false
        
        // 底圖
        let bgImageView = UIImageView(image: .launchScreenBackground)
        bgImageView.contentMode = .scaleAspectFill
        bgImageView.translatesAutoresizingMaskIntoConstraints = false
        
        // Logo（固定 100×82，置中）
        let logoImageView = UIImageView(image: .logo)
        logoImageView.contentMode = .scaleAspectFit
        logoImageView.translatesAutoresizingMaskIntoConstraints = false
        logoImageView.widthAnchor.constraint(equalToConstant: 100).isActive = true
        logoImageView.heightAnchor.constraint(equalToConstant: 82).isActive = true
        
        container.addSubview(bgImageView)
        container.addSubview(logoImageView)
        window.addSubview(container)
        
        // 讓 overlay 充滿整個 window（支援旋轉/多工尺寸變化）
        NSLayoutConstraint.activate([
            container.topAnchor.constraint(equalTo: window.topAnchor),
            container.leadingAnchor.constraint(equalTo: window.leadingAnchor),
            container.trailingAnchor.constraint(equalTo: window.trailingAnchor),
            container.bottomAnchor.constraint(equalTo: window.bottomAnchor),
            
            bgImageView.topAnchor.constraint(equalTo: container.topAnchor),
            bgImageView.leadingAnchor.constraint(equalTo: container.leadingAnchor),
            bgImageView.trailingAnchor.constraint(equalTo: container.trailingAnchor),
            bgImageView.bottomAnchor.constraint(equalTo: container.bottomAnchor),
            
            logoImageView.centerXAnchor.constraint(equalTo: container.centerXAnchor),
            logoImageView.centerYAnchor.constraint(equalTo: container.centerYAnchor),
        ])
        
        privacyOverlay = container
    }
    
    private func hidePrivacyOverlay() {
        privacyOverlay?.removeFromSuperview()
        privacyOverlay = nil
    }
}
