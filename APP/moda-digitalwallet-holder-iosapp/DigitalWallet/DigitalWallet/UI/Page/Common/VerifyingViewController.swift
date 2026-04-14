//
//  LoadingViewController.swift
//  DigitalWallet
//

import UIKit

class VerifyingViewController: UIViewController {
    
    enum LoadingType {
        case verify
        case normal
    }
    
    @IBOutlet weak var imgLoading: UIImageView!
    @IBOutlet weak var walletImageView: UIImageView!
    @IBOutlet weak var loadingLabel: UILabel!
    
    private var timer: Timer!
    private var degrade: CGFloat = 0//旋轉角度
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
    }
    
    private func initView() {
        walletImageView.isHidden = false
        loadingLabel.text = NSLocalizedString("VerifyingCard", comment: "")
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        startLoading()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        stopLoading()
    }
    
    /**開始動畫*/
    private func startLoading() {
        timer = Timer.scheduledTimer(withTimeInterval: 0.003, repeats: true) { [weak self] timer in
            guard let self = self else { return }
            
            let oneDegrade: CGFloat = CGFloat.pi / 180
            self.imgLoading.transform = CGAffineTransform(rotationAngle: oneDegrade * self.degrade)
            
            self.degrade += 1
            
            if self.degrade >= 360{
                self.degrade = 0
            }
        }
    }
    
    /**停止動畫*/
    private func stopLoading(){
        timer.invalidate()
    }
}
