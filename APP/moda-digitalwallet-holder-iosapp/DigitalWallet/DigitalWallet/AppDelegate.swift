//
//  AppDelegate.swift
//  DigitalWallet
//

import UIKit
import UserNotifications
import IQKeyboardManager
import Swinject
import Combine

@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    static var shared: AppDelegate {
        return UIApplication.shared.delegate as! AppDelegate
    }
    
    let container = Container()
    let crossAppSubject = CurrentValueSubject<String, Never>("")
    
    private var crossAppURL: String? = nil
    private var cancelSet = Set<AnyCancellable>()
    var countdownTimer: CountdownTimer?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        IQKeyboardManager.shared().isEnabled = true
        IQKeyboardManager.shared().shouldResignOnTouchOutside = true
        
        let center: UNUserNotificationCenter = UNUserNotificationCenter.current()
        center.delegate = self
        
        /*角標數字清除*/
        UIApplication.shared.applicationIconBadgeNumber = 0
        
        // 檢查log建立時間
        LogUtil.shared.checkFileCreate()
        
        Task {
            // 推播通知檢查
            let center = UNUserNotificationCenter.current()
            let status = await center.notificationSettings().authorizationStatus
            
            switch status {
                /*從未問過權限*/
                case .notDetermined:
                    break
                default:
                    UIApplication.shared.registerForRemoteNotifications()//取得推播Token
            }
        }
        
        setupContainer()
        
        // 監聽截圖事件
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(userDidTakeScreenshot),
            name: UIApplication.userDidTakeScreenshotNotification,
            object: nil
        )
        
        // 請求push token
        UIApplication.shared.registerForRemoteNotifications()
        
        return true
    }

    // MARK: UISceneSession Lifecycle
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
    
    
    
    /// 顯示螢幕截圖提示alert
    @objc private func userDidTakeScreenshot() {
        // 顯示提示 Alert
        let alert = UIAlertController(
            title: NSLocalizedString("ReminderMessage", comment: "提醒訊息"),
            message: NSLocalizedString("ScreenshotWarning", comment: "為維護您的個人資料安全，使用截圖功能時，請務必妥善保管。"),
            preferredStyle: .alert
        )
        alert.addAction(UIAlertAction(title: NSLocalizedString("Confirm", comment: ""), style: .default, handler: nil))
        
        if let topVC = getTopMostViewController() {
            topVC.present(alert, animated: true)
        }
    }
    
    /// iXGuard觸發後顯示提示alert，點擊後關閉APP
    /// - Parameter notify: iXGuard觸發後帶來的notify物件，內含觸發名稱與對應的UInt
    @objc private func displayIXGuardErrorAlert(notify: NSNotification) {
        guard let eventType = notify.userInfo?["eventType"] as? UInt else { return }
        
        let message = String(format: NSLocalizedString("IXGuardMessage", comment: ""), eventType)
        
        let alert = UIAlertController(title: NSLocalizedString("Attention", comment: ""),
                                      message: message,
                                      preferredStyle: .alert)
        let action = UIAlertAction(title: NSLocalizedString("Confirm", comment: ""),
                                   style: .default) { _ in
            exit(0)
        }
        alert.addAction(action)
        
        if let topVC = getTopMostViewController() {
            topVC.present(alert, animated: true)
        }
    }
    
    /// 透過遞迴的方式，取得目前App最上層的VC
    /// - Parameter base: 帶入目前找到的上層VC
    /// - Returns: 回傳最上層的VC
    private func getTopMostViewController(base: UIViewController? = nil) -> UIViewController? {
        let root = UIApplication.shared
                .connectedScenes
                .compactMap { $0 as? UIWindowScene }
                .first?
                .windows
                .first(where: { $0.isKeyWindow })?
                .rootViewController

        let baseVC = base ?? root

        if let nav = baseVC as? UINavigationController {
            return getTopMostViewController(base: nav.visibleViewController)
        } else if let tab = baseVC as? UITabBarController,
                  let selected = tab.selectedViewController {
            return getTopMostViewController(base: selected)
        } else if let presented = baseVC?.presentedViewController {
            return getTopMostViewController(base: presented)
        }

        return baseVC
    }
}

//MARK: Other
extension AppDelegate {
    func application(_ application: UIApplication, shouldAllowExtensionPointIdentifier extensionPointIdentifier: UIApplication.ExtensionPointIdentifier) -> Bool {
        //禁止第三方鍵盤使用
        if extensionPointIdentifier == UIApplication.ExtensionPointIdentifier.keyboard {
            return false
        }
        
        return true
    }
}

//MARK: UNNotification
extension AppDelegate: UNUserNotificationCenterDelegate {
    /**獲取Device Token成功*/
    func application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenString = deviceToken.map({
            String(format: "%02.2hhx", $0)
        }).joined()
        UserDefaultManager.shared.setObject(value: tokenString, key: .PushToken)
    }
    
    /**獲取Device Token失敗*/
    func application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        
    }
}

//MARK: UNNotification
extension AppDelegate {
    /**App處於前台接收通知時*/
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        
        let title = notification.request.content.title
        let message = notification.request.content.body
        NotificationCenter.default.post(name: Notification.Name(Config.Notification.unsubscribeVC), object: nil, userInfo: ["title": title, "message": message])
        UIApplication.shared.applicationIconBadgeNumber = 0
    }
    
    /**App通知的點擊事件*/
    func userNotificationCenter(_ center: UNUserNotificationCenter, didReceive response: UNNotificationResponse, withCompletionHandler completionHandler: @escaping () -> Void) {
        
        let title = response.notification.request.content.title
        let message = response.notification.request.content.body
        NotificationCenter.default.post(name: Notification.Name(Config.Notification.unsubscribeVC), object: nil, userInfo: ["title": title, "message": message])
        UIApplication.shared.applicationIconBadgeNumber = 0
    }
}

// CrossApp
extension AppDelegate {
    func getCrossAppURL() -> String? {
        let url = crossAppURL
        crossAppURL = nil
        return url
    }
    
    func setCrossAppURL(url: String) {
        crossAppURL = url
    }
}

extension AppDelegate {
    private func setupContainer() {
        container.register(VerifiableManager.self) { _ in
            VerifiableManager.shared
        }
        
        container.register(SQLiteManager.self) { _ in
            SQLiteManager.shared
        }
        
        container.register(BiometricVerifyManager.self) { _ in
            BiometricVerifyManager()
        }
        
        container.register(UserRepository.self) { r, wallet in
            UserRepository(databaseManager: r.resolve(SQLiteManager.self)!, verifiableManager: r.resolve(VerifiableManager.self)!, wallet: wallet)
        }
        
        container.register(VerifiablePresentationRepository.self) { r, userId, verifyResp, parseVPData, wallet in
            VerifiablePresentationRepository(userId: userId, verifiableManager: r.resolve(VerifiableManager.self)!, databaseManager: r.resolve(SQLiteManager.self)!, verifyResp: verifyResp, parseVPData: parseVPData, wallet: wallet)
        }
        
        container.register(LoginRepository.self) { r in
            LoginRepository(databaseManager: r.resolve(SQLiteManager.self)!,
                            verifiableManager: r.resolve(VerifiableManager.self)!,
                            biometricVerifyManager: r.resolve(BiometricVerifyManager.self)!)
        }
        
        container.register(ParseLinkManager.self) { _ in
            ParseLinkManager()
        }.inObjectScope(.container)
        
        container.register(ImageLoadManager.self) { r in
            ImageLoadManager(cache: ImageCache())
        }
        
        container.register(SecureURLValidatorManager.self) { r in
            SecureURLValidatorManager()
        }
    }
}

/**自動登出計時*/
extension AppDelegate {
    func autoLogoutTimer() {
        guard let countdownTimer else{
            return
        }
        
        countdownTimer.timeUpSubject
            .receive(on: DispatchQueue.main)
            .sink { _ in
                if let currentVC = FunctionUtil.shared.getTopViewController() {
                    let baseVC = currentVC as? BaseViewController
                    
                    baseVC?.askAlert(title: NSLocalizedString("KeepLoginTitle", comment: ""),
                                     content: NSLocalizedString("LogoutTimeUp", comment: ""),
                                     leftButtonTitle: NSLocalizedString("LogoutWallet", comment: ""),
                                     rightButtonTitle: NSLocalizedString("KeepLogin", comment: ""),
                                     complete: {
                        result in
                        
                        /*繼續使用*/
                        if result{
                            FunctionUtil.shared.logout(reLogin: true)
                        }
                        /*登出*/
                        else{
                            FunctionUtil.shared.logout()
                        }
                    })
                }
        }.store(in: &cancelSet)
    }
}
