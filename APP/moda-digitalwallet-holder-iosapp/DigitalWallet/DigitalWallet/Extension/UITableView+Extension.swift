//
//  UITableView+Extension.swift
//  DigitalWallet
//

import UIKit

extension UITableViewCell {
    static var cellId: String {
        return String(describing: self)
    }
}

extension UITableView {
    func register<T: UITableViewCell>(_: T.Type) {
        register(T.self, forCellReuseIdentifier: T.cellId)
    }
    
    func registerWithNib<T: UITableViewCell>(_: T.Type) {
        let nib = UINib(nibName: T.cellId, bundle: nil)
        register(nib, forCellReuseIdentifier: T.cellId)
    }
    
    func dequeueReusableCell<T: UITableViewCell>(_: T.Type, forIndexPath indexPath: IndexPath) -> T {
        let cell = dequeueReusableCell(withIdentifier: T.cellId, for: indexPath) as! T
        return cell
    }
}
