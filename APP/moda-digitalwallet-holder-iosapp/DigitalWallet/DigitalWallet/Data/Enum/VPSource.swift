//
//  VPSource.swift
//  DigitalWallet
//

import Foundation

/// VP05點擊資訊
enum VPSource {
    /// 我的最愛
    case favorite(MyFavoriteList)
    /// 一般
    case normal(VPItems)

    var vpUid: String {
        switch self {
        case .favorite(let myFavorite):
            return myFavorite.vpUid ?? ""
        case .normal(let vpItems):
            return vpItems.vpUid ?? ""
        }
    }
    
    var name: String {
        switch self {
        case .favorite(let myFavorite):
            return myFavorite.name ?? ""
        case .normal(let vpItems):
            return vpItems.name ?? ""
        }
    }
    
    var verifierModuleUrl: String {
        switch self {
        case .favorite(let myFavorite):
            return myFavorite.verifierModuleUrl ?? ""
        case .normal(let vpItems):
            return vpItems.verifierModuleUrl ?? ""
        }
    }
    
    var logoUrl: String {
        switch self {
        case .favorite(let myFavorite):
            return myFavorite.logoUrl ?? ""
        case .normal(let vpItems):
            return vpItems.logoUrl ?? ""
        }
    }
    
    
    
    
    
}
