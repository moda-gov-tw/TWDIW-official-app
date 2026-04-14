//
//  UIView+Extension.swift
//  DigitalWallet
//

import UIKit

private var WIDTH_CONSTRAINT: NSLayoutConstraint?
private var HEIGHT_CONSTRAINT: NSLayoutConstraint?

extension UIView {
    /// A Boolean value indicating whether the view is gone.
    var isGone: Bool {
        get {
            return !isHidden && (heightConstraint?.constant == 0)
        }
        set {
            if newValue {
                if heightConstraint == nil {
                    self.heightConstraint = heightAnchor.constraint(equalToConstant: 0)
                }
                heightConstraint?.isActive = true
                isHidden = true
            } else {
                heightConstraint?.isActive = false
                isHidden = false
            }
        }
    }
    
    /// Find or set the height constraint of the view.
    private var heightConstraint: NSLayoutConstraint? {
        get {
            return constraints.first { $0.firstAttribute == .height && $0.relation == .equal }
        }
        set {
            if let newConstraint = newValue {
                addConstraint(newConstraint)
            }
        }
    }
    
    /// 載入`Nib`
    /// - Parameter _: 帶入目標的`Class`  ex.  TestView.self
    func loadNib<T: UIView>(_: T.Type) {
        guard let contentView = UINib(nibName: String(describing: T.self), bundle: Bundle(for: T.self)).instantiate(withOwner: self).first as? UIView else {
            return
        }
        self.addSubview(contentView)
        contentView.translatesAutoresizingMaskIntoConstraints = false
        contentView.topAnchor.constraint(equalTo: topAnchor).isActive = true
        contentView.bottomAnchor.constraint(equalTo: bottomAnchor).isActive = true
        contentView.leadingAnchor.constraint(equalTo: leadingAnchor).isActive = true
        contentView.trailingAnchor.constraint(equalTo: trailingAnchor).isActive = true
    }
}

extension UIView {
    static var reusableId: String {
        return String(describing: self)
    }
}

extension UIView {
    func loadNibName<T: UIView>(_: T.Type) {
        Bundle.main.loadNibNamed(T.reusableId, owner: self, options: nil)
    }
}
