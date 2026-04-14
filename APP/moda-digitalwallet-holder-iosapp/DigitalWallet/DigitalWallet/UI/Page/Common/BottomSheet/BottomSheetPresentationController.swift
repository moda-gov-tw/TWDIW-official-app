//
//  BottomSheetPresentationController.swift
//  DigitalWallet
//

import UIKit

final class BottomSheetPresentationController: UIPresentationController {
    
    // MARK: Private properties
    
    private let pullBarView: UIView = {
        let view = UIView()
        view.bounds.size = CGSize(width: 32, height: 4)
        view.backgroundColor = .systemFill
        view.layer.cornerRadius = view.frame.height / 2
        return view
    }()
    
    private lazy var overlayView: UIView = {
        let view = UIView()
        view.backgroundColor = .black.withAlphaComponent(0.3)
        
        return view
    }()
    
    private var cornerRadius: CGFloat {
        24
    }
    
    private var dismissThreshold: CGFloat {
        0.3
    }
  
    // MARK: UIPresentationController
    
    override var frameOfPresentedViewInContainerView: CGRect {
        guard
            let containerView = containerView,
            let presentedView = presentedView
        else {
            return super.frameOfPresentedViewInContainerView
        }
        /// The maximum height allowed for the sheet. We allow the sheet to reach the top safe area inset.
        let maximumHeight = containerView.frame.height - containerView.safeAreaInsets.top - containerView.safeAreaInsets.bottom
        
        let fittingSize = CGSize(width: containerView.bounds.width,
                                 height: UIView.layoutFittingCompressedSize.height)
        
        let presentedViewHeight = presentedView.systemLayoutSizeFitting(
            fittingSize,
            withHorizontalFittingPriority: .required,
            verticalFittingPriority: .fittingSizeLevel
        ).height
        /// The target height of the presented view.
        /// If the size of the of the presented view could not be computed, meaning its equal to zero, we default to the maximum height.
        let targetHeight = presentedViewHeight == .zero ? maximumHeight : presentedViewHeight
        /// Adjust the height of the view by adding the bottom safe area inset.
        let adjustedHeight = min(targetHeight, maximumHeight) + containerView.safeAreaInsets.bottom
        
        let targetSize = CGSize(width: containerView.frame.width, height: adjustedHeight)
        let targetOrigin = CGPoint(x: .zero, y: containerView.frame.maxY - targetSize.height)
        
        return CGRect(origin: targetOrigin, size: targetSize)
    }
    
    override func presentationTransitionWillBegin() {
        super.presentationTransitionWillBegin()
        
        containerView?.addSubview(overlayView)
        overlayView.alpha = 0
        
        presentedViewController.transitionCoordinator?.animate(alongsideTransition: { [weak self] _ in
            guard let self = self else { return }
            self.presentedView?.layer.cornerRadius = self.cornerRadius
            self.overlayView.alpha = 1
        })
    }
    
    override func containerViewDidLayoutSubviews() {
        super.containerViewDidLayoutSubviews()
        
        setupSubviews()
    }
    
    override func dismissalTransitionWillBegin() {
        super.dismissalTransitionWillBegin()
        
        presentedViewController.transitionCoordinator?.animate(alongsideTransition: { [weak self] _ in
            guard let self = self else { return }
            self.presentedView?.layer.cornerRadius = .zero
            self.overlayView.alpha = 0
        })
    }
    
    // MARK: Private methods

    private func setupSubviews() {
        setupPresentedView()
        setupOverlayLayout()
    }
    
    private func setupPresentedView() {
        guard let presentedView = presentedView else {
            return
        }
        presentedView.layer.cornerCurve = .continuous
        presentedView.layer.maskedCorners = [
            .layerMinXMinYCorner,
            .layerMaxXMinYCorner
        ]
    }
    
    private func setupOverlayLayout() {
        guard let containerView = containerView else {
            return
        }
        overlayView.frame = containerView.bounds
    }
    
    /// Triggers the dismiss transition in an interactive manner.
    /// - Parameter isInteractive: Whether the transition should be started interactively by the user.
    private func dismiss(interactively isInteractive: Bool) {
        presentedViewController.dismiss(animated: true)
    }
}

