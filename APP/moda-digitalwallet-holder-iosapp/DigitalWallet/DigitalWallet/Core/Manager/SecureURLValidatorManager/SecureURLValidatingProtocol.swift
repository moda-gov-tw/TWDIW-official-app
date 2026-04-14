//
//  SecureURLValidating.swift
//  DigitalWallet
//


import Foundation

// MARK: - Manager Protocol

public protocol SecureURLValidatingProtocol {
    /// 檢查「已存在的路徑」（來源檔等）：確認不為 symlink、仍在 root 內
    /// - Parameters:
    ///   - url: 要檢查的檔案 URL
    ///   - root: 允許的根目錄（通常是 sandbox 的 Documents / tmp）
    func validateExisting(url: URL, root: URL) throws
    /// 檢查「準備要建立的目的地」（檔案尚未存在）：檢查檔名、父層與邊界
    /// - Parameters:
    ///   - url: 要檢查的檔案 URL
    ///   - root: 允許的根目錄（通常是 sandbox 的 Documents / tmp）
    func validateDestinationForCreate(url: URL, root: URL) throws
}
