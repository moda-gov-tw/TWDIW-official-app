//
//  ShowCertificates.swift
//  DigitalWallet
//

import Combine
import Foundation

class ShowCertificatesViewModel: BaseViewModel {
    
    enum ShowCertificatesCellType {
        /// 我的最愛Title
        case myFavoriteTitle(String, Bool)
        /// 提示點擊
        case hintClick(String)
        /// 我的最愛清單
        case myFavorite(MyFavoriteList)
        /// 快速授權Title
        case quickAuthTitle(String, Bool)
        /// 快速授權清單
        case quickAuthList(VPItems)
    }
    
    enum ErrorType {
        /// API錯誤
        case apiError(String)
    }
    
    let repository: UserRepositoryProtocol
    let parseLinkManager: ParseLinkProtocol
    let errorSubject = PassthroughSubject<ErrorType, Never>()
    let stopLoadingSubject = PassthroughSubject<Void, Never>()
    let vp05RegeneratePassthroughSubject = PassthroughSubject<Void, Never>()
    let reloadTableViewSubject = PassthroughSubject<Void, Never>()
    /// 防止連點
    var cantClick: Bool = false
    /// 我的最愛展開收合
    var myFavoriteExpand: Bool = true
    /// 快速授權列表展開收合
    var quickAuthExpand: Bool = true
    /// TableViewCell種類
    var tableviewCell: [ShowCertificatesCellType] = []
    private var cancelSet = Set<AnyCancellable>()
    /// 憑證DeepLinkInfo
    @Published var vp05DeepLinkInformation: VP05DeepLinkInformation?
    /// 所有憑證
    @Published var vpItems: [VPItems] = []
    /// 快速授權清單
    @Published var quickAuthList: [VPItems] = []
    /// 我的最愛清單
    @Published var myFavoriteList: [MyFavoriteList] = []
    
    init(repository: UserRepositoryProtocol, parseLinkManager: ParseLinkProtocol) {
        self.repository = repository
        self.parseLinkManager = parseLinkManager
        super.init()
        dataBinding()
    }
    
    private func dataBinding() {
        $vpItems
            .receive(on: DispatchQueue.main)
            .sink { [weak self] items in
                guard let self = self else { return }
                filterAddedFavorite(vpItem: items)
            }.store(in: &cancelSet)
        
        $quickAuthList
            .receive(on: DispatchQueue.main)
            .sink { [weak self] items in
                guard let self = self else { return }
                setupTableViewCellType()
            }.store(in: &cancelSet)
        
        $myFavoriteList
            .receive(on: DispatchQueue.main)
            .compactMap{ $0 }
            .sink { [weak self] items in
                guard let self = self else { return }
                setupTableViewCellType()
            }.store(in: &cancelSet)
        
        $vp05DeepLinkInformation
            .receive(on: DispatchQueue.main)
            .compactMap{ $0 }
            .sink { [weak self] data in
                guard let self = self else { return }
                repository.saveVP05Info(data: data)
                if let deepLink = data.certificateDetailInfo.deepLink,
                   let url = URL(string: deepLink) {
                    parseLinkManager.crossAppCurrentValueSubject.send(.crossAppActive(url: url, isShowFullScreen: true))
                }
            }.store(in: &cancelSet)
        
        vp05RegeneratePassthroughSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                getDeepLinkAndTransactionId(clickData: repository.vp05Soruce)
            }.store(in: &cancelSet)
    }
    
    private func judgeGetMoreCertificates(data: VPCardApplyListResponse) {
        guard let pageSize = data.pageSize,
              let currentPage = data.currentPage,
              let totalPages = data.totalPages else { return }
        if currentPage == 0 {
            vpItems = data.vpItems ?? []
        } else {
            vpItems.append(contentsOf: data.vpItems ?? [])
        }
        guard currentPage < totalPages - 1 else {
            getFavortieListDB()
            stopLoadingSubject.send()
            return
        }
        getCertificates(page: currentPage + 1, size: pageSize)
        
    }
}

extension ShowCertificatesViewModel {
    func getCertificates(page: Int = 0, size: Int = 10) {
        Task {
            do {
                let model = GetCertificatesListRequest(page: 0, size: 10)
                let certificatesResult = try await repository.getCertificateList(bodyModel: model)
                judgeGetMoreCertificates(data: certificatesResult)
            } catch (let error as DIDError) {
                errorSubject.send(.apiError(error.message))
            } catch {
                errorSubject.send(.apiError(error.localizedDescription))
            }
        }
    }
    
    // 獲取該筆資料
    func getDeepLinkAndTransactionId(clickData: VPSource?) {
        repository.updateVP05Source(data: clickData)
        guard let clickData = clickData else { return }
        let model: DwVerIfierMgr401iRequest
        let url: String
        
        switch clickData {
        case .favorite(let data):
            model = DwVerIfierMgr401iRequest(vpUid: data.vpUid ?? "")
            url = data.verifierModuleUrl ?? ""
            break
        case .normal(let data):
            model = DwVerIfierMgr401iRequest(vpUid: data.vpUid ?? "")
            url = data.verifierModuleUrl ?? ""
            break
        }
        
        Task {
            do {
                let result = try await repository.getDeepLinkAndTransactionId(bodyModel: model, url: url)
                stopLoadingSubject.send()
                vp05DeepLinkInformation = VP05DeepLinkInformation(certificateInfo: clickData, certificateDetailInfo: result)
            } catch {
                stopLoadingSubject.send()
                if let didError = error as? DIDError {
                    errorSubject.send(.apiError(didError.message))
                }
            }
        }
    }
}

// MARK: ShowCertificatesCellType
extension ShowCertificatesViewModel {
    func setupTableViewCellType() {
        tableviewCell.removeAll()
        if myFavoriteList.isEmpty {
            tableviewCell.append(.myFavoriteTitle(NSLocalizedString("MyFavorite", comment: ""), true))
            tableviewCell.append(.hintClick(NSLocalizedString("HintClickStar", comment: "")))
            if quickAuthList.isEmpty {
                tableviewCell.append(.quickAuthTitle(NSLocalizedString("QuickAuthorizationList", comment: ""), true))
            } else {
                tableviewCell.append(.quickAuthTitle(NSLocalizedString("QuickAuthorizationList", comment: ""), false))
            }
            if quickAuthExpand {
                let _ = quickAuthList.map {
                    tableviewCell.append(.quickAuthList($0))
                }
            }
        } else {
            tableviewCell.append(.myFavoriteTitle(NSLocalizedString("MyFavorite", comment: ""), false))
            if myFavoriteExpand {
                let _ = myFavoriteList.map {
                    tableviewCell.append(.myFavorite($0))
                }
            }
            if quickAuthList.isEmpty {
                tableviewCell.append(.quickAuthTitle(NSLocalizedString("QuickAuthorizationList", comment: ""), true))
            } else {
                tableviewCell.append(.quickAuthTitle(NSLocalizedString("QuickAuthorizationList", comment: ""), false))
            }
            if quickAuthExpand {
                let _ = quickAuthList.map {
                    tableviewCell.append(.quickAuthList($0))
                }
            }
        }
        reloadTableViewSubject.send()
    }
}

// MARK: DB相關
extension ShowCertificatesViewModel {
    func getFavortieListDB() {
        repository.getFavoriteCertificates()
            .compactMap{ $0 }
            .sink { [weak self] items in
                guard let self = self else { return }
                myFavoriteList = items.sortedByENThenZHTW()
                filterAddedFavorite(vpItem: self.vpItems)
            }.store(in: &cancelSet)
    }
    
    func saveFavoriteListDB(item: VPItems) {
        repository.saveFavoriteCertificates(vpItem: item)
            .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                return self?.refreshFilterData() ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
            }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { _ in
            }.store(in: &cancelSet)

    }
    
    func deleteFavoriteListDB(item: MyFavoriteList) {
        repository.deleteFavoriteCertificates(favoriteCertificateId: item.favoriteCertificateUUID!)
            .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                return self?.refreshFilterData() ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
            }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { _ in
            }.store(in: &cancelSet)
    }
    
    private func refreshFilterData() -> AnyPublisher<Void, Error> {
        repository.getFavoriteCertificates()
            .compactMap{ $0 }
            .handleEvents(receiveOutput: { [weak self] favorites in
                guard let self = self else { return }
                myFavoriteList = favorites.sortedByENThenZHTW()
                filterAddedFavorite(vpItem: self.vpItems)
            })
            .map { _ in () }
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
    
    private func filterAddedFavorite(vpItem: [VPItems]) {
        let newItems = vpItem.sortedByName(localeIdentifier: "en")
        let addId = Set(myFavoriteList.compactMap{ $0.vpUid })
        quickAuthList = newItems.filter{ !addId.contains($0.vpUid ?? "") }.sortedByENThenZHTW()
    }
}

