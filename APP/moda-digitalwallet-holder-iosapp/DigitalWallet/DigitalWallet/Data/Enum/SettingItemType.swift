//
//  SettingItemType.swift
//  DigitalWallet
//

import Foundation

enum SettingItemType: Equatable {
    // 設定頁
    case walletSetting
    case personalSetting
    case questionCenter
    case walletWeb(URL)
    case logout
    // 皮夾設定頁
    case changeWalletName
    case changePinCode
    case settingBiometrics
    case autoRefreshCard
    case walletLockSetting
    case deleteWallet
    // 問題中心頁
    case commonQuestions
    case problemReport
    case contactCustomerService
    // 個人化管理
    case autoLogoutSetting
}
