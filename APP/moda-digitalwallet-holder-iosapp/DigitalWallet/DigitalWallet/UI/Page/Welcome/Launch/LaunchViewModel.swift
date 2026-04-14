//
//  LaunchViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class LaunchViewModel {
    
    enum RootViewControllerType {
        case Login
        case Register
    }
    
    let sqliteManager: DatabaseModelProtocol
    let unsuportedPassthroughSubject = PassthroughSubject<Bool, Never>()
    let setupRootViewControllerPassthroughSubject = PassthroughSubject<RootViewControllerType, Never>()
    let databaseConfigureErrorPassthroughSubject = PassthroughSubject<Void, Never>()
    
    init(sqliteManager: DatabaseModelProtocol) {
        self.sqliteManager = sqliteManager
    }
    
    func checkUserDevice() {
        var systemInfo = utsname()
        uname(&systemInfo)
        
        let machineMirror = Mirror(reflecting: systemInfo.machine)
        let identifier = machineMirror.children.compactMap { element in
            guard let value = element.value as? Int8, value != 0 else { return nil }
            return String(UnicodeScalar(UInt8(value)))
        }.joined()
        
        let unsupportedSocModels = ["iPod5,1", "iPod7,1", "iPod9,1", "iPhone3,1", "iPhone3,2", "iPhone3,3", "iPhone4,1", "iPhone5,1", "iPhone5,2", "iPhone5,3", "iPhone5,4", "iPhone6,1", "iPhone6,2", "iPhone7,2", "iPhone7,1", "iPhone8,1", "iPhone8,2", "iPhone9,1", "iPhone9,3", "iPhone9,2", "iPhone9,4", "iPhone10,1", "iPhone10,4", "iPhone10,2", "iPhone10,5", "iPhone10,3", "iPhone10,6"]
        
        if unsupportedSocModels.contains(identifier) {
            unsuportedPassthroughSubject.send(false)
        } else {
            unsuportedPassthroughSubject.send(true)
        }
    }
    
    func setupDatabaseAndRootViewController() {
        do {
            // 初始化sqliteManager設定
            try sqliteManager.configure()
            // 判斷是否已有建立的皮夾
            if let wallets = sqliteManager.fetchWallets(),
               wallets.count != 0 {
                setupRootViewControllerPassthroughSubject.send(.Login)
            } else {
                setupRootViewControllerPassthroughSubject.send(.Register)
            }
        } catch {
            assertionFailure("db configure錯誤 \(error.localizedDescription)")
            databaseConfigureErrorPassthroughSubject.send()
        }
    }
}
