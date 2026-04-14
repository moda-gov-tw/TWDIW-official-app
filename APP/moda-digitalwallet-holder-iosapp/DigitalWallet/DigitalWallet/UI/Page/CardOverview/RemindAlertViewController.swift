//
//  RemindAlertViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class RemindAlertViewController: UIViewController {

    @IBOutlet weak var remindAlertView: RemindAlertView!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var pageControl: UIPageControl!
    @IBOutlet weak var mainStackView: UIStackView!
    
    let viewModel: RemindAlertViewModel
    var cancelSet = Set<AnyCancellable>()
    
    init(viewModel: RemindAlertViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "RemindAlertViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupView()
        dataBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        viewModel.handleRemindData()
        pageControl.isHidden = viewModel.showList.count <= 1
    }
    
    private func setupView() {
        scrollView.delegate = self
        scrollView.isPagingEnabled = true
        scrollView.isHidden = true
        scrollView.showsHorizontalScrollIndicator = false
    }
    
    private func dataBinding() {
        viewModel.refreshUISubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                pageControl.numberOfPages = viewModel.showList.count
                genRemindAlert()
            }.store(in: &cancelSet)
        
    }
    
    private func genRemindAlert() {
        scrollView.isHidden = false
        mainStackView.arrangedSubviews.forEach {
            $0.removeFromSuperview()
        }
        
        viewModel.showList.enumerated().forEach { (index, type) in
            let remindView = RemindAlertView()
            remindView.addView(remindData: type)
            remindView.onClickConfirm = { [weak self] in
                guard let self = self else { return }
                let isLast = index == viewModel.showList.count - 1
                let currentPage: Int = index + 1
                if isLast {
                    viewModel.updateRemind()
                    dismiss(animated: true)
                } else {
                    scrollView.scrollRectToVisible(CGRect(origin: CGPoint(x: scrollView.frame.width * CGFloat(currentPage),
                                                                          y: 0),
                                                          size: scrollView.frame.size), animated: true)
                    pageControl.currentPage = currentPage
                }
            }
            mainStackView.addArrangedSubview(remindView)
            NSLayoutConstraint.activate([
                remindView.widthAnchor.constraint(equalToConstant: view.frame.width),
                remindView.heightAnchor.constraint(equalTo: scrollView.frameLayoutGuide.heightAnchor)
            ])
        }
        view.layoutIfNeeded()
        view.setNeedsLayout()
    }
}

extension RemindAlertViewController: UIScrollViewDelegate {
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView) {
        let pageWidth = scrollView.frame.size.width
        let fractionalPage = scrollView.contentOffset.x / pageWidth
        let currentPage = Int(round(fractionalPage))
        pageControl.currentPage = currentPage
    }
}
