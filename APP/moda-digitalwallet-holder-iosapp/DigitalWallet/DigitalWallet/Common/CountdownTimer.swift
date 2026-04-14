//
//  CountdownTimer.swift
//  DigitalWallet
//

import Foundation
import Combine

class CountdownTimer {
    private var timer: Timer?
    private var remainingSeconds = 0//倒數時間(秒)
    
    let repository: UserRepositoryProtocol
    let timeUpSubject = PassthroughSubject<Void, Never>()
    
    init(repository: UserRepositoryProtocol) {
        self.repository = repository
    }
    
    /**開始計時*/
    func startCountdown() {
        stopTimer()
        
        let remainingMinutes = Int(repository.wallet.autoLogout ?? 0)
        
        if remainingMinutes > 0 {
            remainingSeconds = remainingMinutes * 60
            
            timer = Timer.scheduledTimer(withTimeInterval: 1.0, repeats: true) { [weak self] _ in
                guard let self = self else { return }
                
                if self.remainingSeconds > 0 {
                    self.remainingSeconds -= 1
                }
                /*倒數結束*/
                else {
                    self.timer?.invalidate()
                    timeUpSubject.send()
                }
            }
        }
    }
    
    /**計時停止*/
    func stopTimer() {
        timer?.invalidate()
        timer = nil
    }
}
