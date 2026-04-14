//
//  Untitled.swift
//  DigitalWallet
//

import Foundation
import Combine
import UIKit

class AddCertificateViewModel: BaseViewModel {
    
    enum ShowVPCellType {
        /// 快速授權清單
        case quickAuthList(VCItems)
    }
    
    enum ErrorType {
        /// API錯誤
        case apiError(String)
    }
    
    /// TableViewCell種類
    var tableviewCell: [ShowVPCellType] = []
    
    let callTabbarProtocol: CallTabbarProtocol
    let parseLinkManager: ParseLinkProtocol
    let repository: UserRepositoryProtocol
    let imageLoadManager: ImageLoadProtocol
    
    let reloadTableViewSubject = PassthroughSubject<Void, Never>()
    let errorSubject = PassthroughSubject<ErrorType, Never>()
    let stopLoadingSubject = PassthroughSubject<Void, Never>()
    let toWebViewSubject = PassthroughSubject<(urlString: String, name: String), Never>()
    // 實作邏輯
    let toOpenURLSubject = PassthroughSubject<(urlString: String, name: String), Never>()
    
    private var cancelSet = Set<AnyCancellable>()
    
    /// 所有VC資料
    @Published var vcItems: [VCItems] = []
    
    init(repository: UserRepositoryProtocol,
         callTabbarProtocol: CallTabbarProtocol,
         parseLinkManager: ParseLinkProtocol,
         imageLoadManager: ImageLoadProtocol) {
        self.repository = repository
        self.callTabbarProtocol = callTabbarProtocol
        self.parseLinkManager = parseLinkManager
        self.imageLoadManager = imageLoadManager
        super.init()
        dataBinding()
    }
    
    private func dataBinding() {
        $vcItems
            .receive(on: DispatchQueue.main)
            .sink { [weak self] items in
                guard let self = self else { return }
                setupTableViewCellType()
            }.store(in: &cancelSet)
    }
    
    func getVCLink(checkData: VCItems) {
        switch checkData.openType {
        case .openUrl:
            if let urlString = checkData.issuerServiceUrl,
               let name = checkData.name {
                toOpenURLSubject.send((urlString, name))
            }
        case .embedWebView:
            if let urlString = checkData.issuerServiceUrl,
               let name = checkData.name {
                toWebViewSubject.send((urlString, name))
            }
        case .none: /// 沒定義到的類型
            break
        }
    }
}

// MARK: ShowVPCellType
extension AddCertificateViewModel {
    func setupTableViewCellType() {
        tableviewCell.removeAll()
        tableviewCell.append(contentsOf: vcItems.map({.quickAuthList($0)}))
        reloadTableViewSubject.send()
    }
}

// MARK: API
extension AddCertificateViewModel {
    func getCertificates(page: Int = 0, size: Int = 10) {
        Task {
            do {
                let model = GetCertificatesListRequest(page: 0, size: 10)
                let certificatesResult = try await repository.getVCApplyList(bodyModel: model)
                judgeGetMoreCertificates(data: certificatesResult)
            } catch (let error as DIDError) {
                errorSubject.send(.apiError(error.message))
            } catch {
                errorSubject.send(.apiError(error.localizedDescription))
            }
        }
    }
    
    private func judgeGetMoreCertificates(data: VCCardApplyListResponse) {
        guard let pageSize = data.pageSize,
              let currentPage = data.currentPage,
              let totalPages = data.totalPages else { return }
        if currentPage == 0 {
            vcItems = data.vcItems ?? []
        } else {
            vcItems.append(contentsOf: data.vcItems ?? [])
        }
        guard currentPage < totalPages - 1 else {
            stopLoadingSubject.send()
            return
        }
        getCertificates(page: currentPage + 1, size: pageSize)
    }
}

