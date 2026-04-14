//
//  LoadingViewController.swift
//  DigitalWallet
//

import UIKit
import Lottie

class LoadingViewController: UIViewController {
    
    enum LoadingStyle {
        case white
        case normal
    }
    
    @IBOutlet weak var loadingIconView: UIView!
    @IBOutlet weak var backgroundImageView: UIImageView!
    
    private var animationView: LottieAnimationView?
    var loadingStyle: LoadingStyle = .normal
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setupView()
        animationView?.play()
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        animationView?.stop()
        animationView?.currentProgress = 0  // 回到第一幀
    }
    
    private func setupView() {
        switch loadingStyle {
        case .white:
            backgroundImageView.isHidden = true
            view.backgroundColor = .white.withAlphaComponent(0.7)
        case .normal:
            backgroundImageView.isHidden = false
            view.backgroundColor = .white
        }
        
        let animation = LottieAnimation.named("Loading") 
        
        animationView = LottieAnimationView(animation: animation)
        animationView?.frame = loadingIconView.bounds
        animationView?.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        animationView?.contentMode = .scaleAspectFit
        animationView?.loopMode = .loop
        
        if let animationView = animationView {
            loadingIconView.addSubview(animationView)
        }
    }
}
