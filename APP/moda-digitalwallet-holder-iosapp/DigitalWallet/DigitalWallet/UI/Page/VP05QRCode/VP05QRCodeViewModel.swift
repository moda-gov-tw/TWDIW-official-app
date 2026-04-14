//
//  VP05QRCodeViewModel.swift
//  DigitalWallet
//

import UIKit
import Combine
import Foundation

class VP05QRCodeViewModel: BaseViewModel {
    enum QRCodeStatus {
        /// 失效
        case expired
        /// 尚未失效
        case active
    }
    
    let repository: UserRepositoryProtocol
    let callTabbarProtocol: CallTabbarProtocol
    /// 倒數前段文字
    private let prefixAttr: NSAttributedString = {
        NSAttributedString(string: NSLocalizedString("ShowQRCodeCountDown", comment: ""), attributes: [
           .font: FontName.NotoSansTC_Regular.font,
           .foregroundColor: UIColor._767676
       ])
    }()
    /// 倒數後段文字
    private let suffixAttr: NSAttributedString = {
        NSAttributedString(string: NSLocalizedString("LaterExpired", comment: ""), attributes: [
           .font: FontName.NotoSansTC_Regular.font,
           .foregroundColor: UIColor._767676
       ])
    }()
    
    private var startCountDownTime: Date?
    private var countDownTimer: AnyCancellable?
    private var comeBackCountDownTimer: AnyCancellable?
    private var cancelSet = Set<AnyCancellable>()
    /// 倒數時間設定
    @Published var countDownTime: Int = 30
    /// 倒數字串
    @Published var countDownString: NSMutableAttributedString?
    /// qrCode data
    @Published var qrCodeData: Data?
    /// qrCode顯示狀態
    @Published var qrCodeStatus: QRCodeStatus = .active

    init(repository: UserRepositoryProtocol,
         callTabbarProtocol: CallTabbarProtocol) {
        self.repository = repository
        self.callTabbarProtocol = callTabbarProtocol
        super.init()
        getTransactionIdQRCode()
    }
    
    private func dataBinding() {
        $countDownTime
            .receive(on: DispatchQueue.main)
            .sink { [weak self] _ in
                guard let self else { return }
                startCountdown()
            }.store(in: &cancelSet)
    }
}

// MARK: 倒數
extension VP05QRCodeViewModel {
    private func startRemainTime(_ time: TimeInterval) {
        comeBackCountDownTimer = Timer.publish(every: 1,
                                               on: .main,
                                               in: .common)
        .autoconnect()
        .sink(receiveValue: { [weak self] _ in
            guard let self = self else { return }
            if countDownTime > 0 {
                countDownTime -= 1
                countDownString = formatCountDown(countDownTime)
            } else {
                qrCodeStatus = .expired
                comeBackCountDownTimer?.cancel()
            }
        })
    }
    
    /// 到數顯示邏輯
    /// - Parameter seconds: 倒數秒數
    private func formatCountDown(_ seconds: Int) -> NSMutableAttributedString {
        let minutes = seconds / 60
        let seconds = seconds % 60
        let middle = String(format: NSLocalizedString("CountDownSecond", comment: ""), minutes, seconds)
        
        let middleAttr = NSAttributedString(string: middle, attributes: [
            .font: FontName.NotoSansTC_Bold.font,
            .foregroundColor: UIColor.C_94_D_70
        ])
        
        let finalAttr = NSMutableAttributedString()
        finalAttr.append(prefixAttr)
        finalAttr.append(middleAttr)
        finalAttr.append(suffixAttr)

        return finalAttr
    }
    
    func startCountdown() {
        startCountDownTime = Date()
        countDownTimer = Timer.publish(every: 1,
                                       on: .main,
                                       in: .common)
        .autoconnect()
        .sink(receiveValue: { [weak self] _ in
            guard let self = self else { return }
            if countDownTime > 0 {
                // 倒數
                countDownTime -= 1
                countDownString = formatCountDown(countDownTime)
            } else {
                qrCodeStatus = .expired
                countDownTimer?.cancel()
            }
        })
    }
    
    func cancelTimer() {
        countDownTimer?.cancel()
        comeBackCountDownTimer?.cancel()
    }
    
    func comeBackToTimer() {
        guard let startCountDownTime = startCountDownTime else { return }
        let afterDate = Date().timeIntervalSince(startCountDownTime)
        guard afterDate > TimeInterval(countDownTime) else {
            startRemainTime(afterDate)
            return
        }
        cancelTimer()
        qrCodeStatus = .expired
    }
}

// MARK: SDK
extension VP05QRCodeViewModel {
    /// 獲取QRCode
    private func getTransactionIdQRCode() {
        let request = DwVerIfierMgr402iRequest(transactionId: repository.vp05Info?.certificateDetailInfo.transactionId ?? "")
        
        Task {
            do {
                let result = try await repository.getTransactionIdQRCode(url: repository.vp05Info?.certificateInfo.verifierModuleUrl ?? "", request: request)
                handleResponse(result)
            } catch {
                
            }
        }
    }
    
    private func handleResponse(_ result: DwVerIfierMgr402iResponse) {
        if let qrCode = result.qrcode, let time = Int(result.totptimeout ?? "") {
            if let commaIndex = qrCode.firstIndex(of: ",") {
                let base64DataString = String(qrCode.suffix(from: qrCode.index(after: commaIndex)))
                let data = Data(base64Encoded: base64DataString)
                countDownTime = time
                qrCodeData = data
            }
        }
    }
}
