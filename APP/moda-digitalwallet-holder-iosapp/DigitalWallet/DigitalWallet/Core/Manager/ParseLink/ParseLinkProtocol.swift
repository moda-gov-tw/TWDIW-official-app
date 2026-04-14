//
//  ParseLinkProtocol.swift
//  DigitalWallet
//

import Foundation
import Combine

/// 解析連結抽象層
protocol ParseLinkProtocol {
    /// crossApp
    var crossAppCurrentValueSubject: CurrentValueSubject<CrossAppStatus, Never> { get }
    
    var inAppCurrentValueSubject: PassthroughSubject<Bool, Never> { get }
    /// 解析連結
    /// - Parameter text: 解析內容
    /// - Returns: 解析結果
    func parseLink(_ url: URL) -> ParseLinkResult
}
