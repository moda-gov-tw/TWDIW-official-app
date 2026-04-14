//
//  SearchCertificatesViewModel.swift
//  DigitalWallet
//

import Combine
import Foundation

class SearchCertificatesViewModel: BaseViewModel {
    
    enum TextFieldStatus {
        /// 搜尋中(有搜到)
        case searching
        /// 搜尋中(沒搜到)
        case noSearchResults
        /// 尚未搜尋
        case noSearch
    }
    
    enum SearchCertificateCellType {
        /// Title
        case searchTitle(String)
        /// 搜尋紀錄
        case searchCertificateLog(SearchCertificateLog)
        /// 我的最愛搜尋結果
        case searchCertificateFavoriteResult(MyFavoriteList)
        /// 未加入我的最愛搜尋結果
        case searchCertificateResult(VPItems)
    }
    
    enum TableViewType: Equatable {
        /// 顯示
        case show
        /// 不顯示
        case hidden(String)
    }
    
    enum ErrorType {
        /// API錯誤
        case apiError(String)
    }
    
    let repository: UserRepositoryProtocol
    let parseLinkManager: ParseLinkProtocol
    let errorSubject = PassthroughSubject<ErrorType, Never>()
    let reloadTableViewSubject = PassthroughSubject<TableViewType, Never>()
    let popPassthroughSubject = PassthroughSubject<Void, Never>()
    let showLoadingPassthroughSubject = PassthroughSubject<Void, Never>()
    let stopLoadingSubject = PassthroughSubject<Void, Never>()
    let updateVPSourcePassthroughSubject = PassthroughSubject<VPSource?, Never>()
    /// TextField狀態
    var textFieldStatus: TextFieldStatus = .noSearch
    /// TableViewCell種類
    var tableviewCell: [SearchCertificateCellType] = []
    /// 所有憑證
    var vpItems: [VPItems]
    /// 我的最愛清單
    var myFavoriteList: [MyFavoriteList] = []
    private var cancelSet = Set<AnyCancellable>()
    /// 憑證DeepLinkInfo
    @Published var vp05DeepLinkInformation: VP05DeepLinkInformation?
    /// 搜尋字串
    @Published var searchString: String?
    /// 搜尋紀錄
    @Published var searchCertificatesLogArray: [SearchCertificateLog] = []
    
    
    init(repository: UserRepositoryProtocol,
         parseLinkManager: ParseLinkProtocol,
         vpItems: [VPItems]) {
        self.repository = repository
        self.parseLinkManager = parseLinkManager
        self.vpItems = vpItems
        super.init()
        getFavortieListDB()
        getSearchCertificatesLog()
        dataBinding()
    }
    
    private func dataBinding() {
        $searchCertificatesLogArray
            .receive(on: DispatchQueue.main)
            .filter{ [weak self] _ in
                self?.textFieldStatus == .noSearch
            }
            .sink { [weak self] items in
                guard let self = self else { return }
                generateTableViewCellType(searchString: "", searchLogs: items)
            }.store(in: &cancelSet)
        
        $searchString
            .receive(on: DispatchQueue.main)
            .compactMap{ $0 }
            .sink { [weak self] str in
                guard let self = self else { return }
                generateTableViewCellType(searchString: str, searchLogs: searchCertificatesLogArray)
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
    }
    
    private func generateTableViewCellType(searchString: String, searchLogs: [SearchCertificateLog]) {
        tableviewCell.removeAll()
        textFieldStatus = searchString.isEmpty ? .noSearch : .searching
        
        var matchItems: [VPItems] = []
        if textFieldStatus == .searching {
            matchItems = vpItems.filter { [weak self] in
                guard let self = self else { return false }
                return getSearchMatchResults(strFull: $0.name, strSearch: searchString)
            }
            textFieldStatus = matchItems.isEmpty ? .noSearchResults : .searching
        }

        switch textFieldStatus {
        case .searching:
            generateSearchingCellType(matchItems: matchItems.sortedByENThenZHTW())
            break
        case .noSearchResults:
            reloadTableViewSubject.send(.hidden(NSLocalizedString("NoSearchResults", comment: "")))
            break
        case .noSearch:
            generateNoSearchCellType(searchLogs: searchLogs)
            break
        }
    }
    
    private func generateSearchingCellType(matchItems: [VPItems]) {
        tableviewCell.removeAll()
        tableviewCell.append(.searchTitle(NSLocalizedString("SearchResults", comment: "")))
        let favoriteResults = matchItems.flatMap { [weak self] matchItem in
            guard let self = self else { return [] }
            return myFavoriteList.filter{ $0.name == matchItem.name }
        } as? [MyFavoriteList]
        
        favoriteResults?.forEach {
            tableviewCell.append(.searchCertificateFavoriteResult($0))
        }
        
        guard let favoriteResults = favoriteResults, !favoriteResults.isEmpty else {
            matchItems.forEach {
                tableviewCell.append(.searchCertificateResult($0))
            }
            reloadTableViewSubject.send(.show)
            return
        }
        
        let favoriteNames = Set(favoriteResults.map{ $0.name })
        
        let normalResults: [VPItems] = matchItems.filter {
            !favoriteNames.contains($0.name)
        }
        
        normalResults.forEach {
            tableviewCell.append(.searchCertificateResult($0))
        }
        
        reloadTableViewSubject.send(.show)
        
    }
    
    private func generateNoSearchCellType(searchLogs: [SearchCertificateLog]) {
        tableviewCell.append(.searchTitle(NSLocalizedString("SearchLogs", comment: "")))
        searchLogs.forEach {
            tableviewCell.append(.searchCertificateLog($0))
        }
        reloadTableViewSubject.send(.show)
    }
    
}

// MARK: 我的最愛
extension SearchCertificatesViewModel {
    private func getFavortieListDB() {
        repository.getFavoriteCertificates()
            .compactMap{ $0 }
            .sink { [weak self] items in
                guard let self = self else { return }
                myFavoriteList = items.sortedByENThenZHTW()
            }.store(in: &cancelSet)
        
    }
    
    func saveFavoriteListDB(item: VPItems) {
        repository.saveFavoriteCertificates(vpItem: item)
            .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                return self?.refreshFilterData() ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
            }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                generateTableViewCellType(searchString: searchString ?? "", searchLogs: searchCertificatesLogArray)
            }.store(in: &cancelSet)
        
    }
    
    func deleteFavoriteListDB(item: MyFavoriteList) {
        repository.deleteFavoriteCertificates(favoriteCertificateId: item.favoriteCertificateUUID!)
            .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                return self?.refreshFilterData() ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
            }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                generateTableViewCellType(searchString: searchString ?? "", searchLogs: searchCertificatesLogArray)
            }.store(in: &cancelSet)
    }
    
    private func refreshFilterData() -> AnyPublisher<Void, Error> {
        repository.getFavoriteCertificates()
            .handleEvents(receiveOutput: { [weak self] favorites in
                guard let self = self else { return }
                myFavoriteList = (favorites ?? []).sortedByENThenZHTW()
            })
            .map { _ in () }
            .setFailureType(to: Error.self)
            .eraseToAnyPublisher()
    }
}

// MARK: 搜尋紀錄
extension SearchCertificatesViewModel {
    /// 儲存搜尋紀錄
    func saveSearchCertificatesLog(searchString: String) {
        guard !searchString.isEmpty else { return }
        if searchCertificatesLogArray.count > 4 {
            repository.deleteSearchCertificateLog(searchCertificateLogUUID: searchCertificatesLogArray.last?.searchCertificatesLogUUID ?? UUID())
                .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                    return self?.repository.saveSearchCertificatesLog(searchCertificateLogName: searchString) ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
                }
                .sink { completion in
                    guard case let .failure(error) = completion else { return }
                } receiveValue: { [weak self] _ in
                    guard let self = self else { return }
                    getSearchCertificatesLog()
                }.store(in: &cancelSet)
        } else {
            repository.saveSearchCertificatesLog(searchCertificateLogName: searchString)
                .sink { completion in
                    guard case let .failure(error) = completion else { return }
                } receiveValue: { [weak self] _ in
                    guard let self = self else { return }
                    getSearchCertificatesLog()
                }.store(in: &cancelSet)
        }
    }
    
    /// 獲取搜尋紀錄
    func getSearchCertificatesLog() {
        repository.getSearchCertificatesLog()
            .compactMap{ $0 }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] items in
                guard let self = self else { return }
                searchCertificatesLogArray = items.sorted(by: { $0.creatDate ?? .distantPast > $1.creatDate ?? .distantPast })
            }.store(in: &cancelSet)
    }
    
    /// 刪除搜尋紀錄
    func deleteSearchCertificatesLog(searchCertificateLog: SearchCertificateLog) {
        repository.deleteSearchCertificateLog(searchCertificateLogUUID: searchCertificateLog.searchCertificatesLogUUID!)
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                getSearchCertificatesLog()
            }.store(in: &cancelSet)
    }
}


// MARK: 點擊Certificate
extension SearchCertificatesViewModel {
    // 獲取該筆資料
    func getDeepLinkAndTransactionId(clickData: VPSource) {
        repository.updateVP05Source(data: clickData)
        let model: DwVerIfierMgr401iRequest
        let url: String
        
        switch clickData {
        case .favorite(let data):
            showLoadingPassthroughSubject.send()
            model = DwVerIfierMgr401iRequest(vpUid: data.vpUid ?? "")
            url = data.verifierModuleUrl ?? ""
            break
        case .normal(let data):
            showLoadingPassthroughSubject.send()
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
                errorSubject.send(.apiError(error.localizedDescription))
            }
        }
    }
}

extension SearchCertificatesViewModel {
    private func getSearchMatchResults(strFull: String?, strSearch: String?) -> Bool {
        guard let strFull = strFull,
              let strSearch = strSearch else { return false }
        let pattern = NSRegularExpression.escapedPattern(for: strSearch)
        do {
            // 建立正則表達式物件
            let regex = try NSRegularExpression(pattern: pattern)
            
            // 找到所有匹配的範圍
            let matches = regex.matches(in: strFull, range: NSRange(strFull.startIndex..., in: strFull))
            
            return !matches.isEmpty
        } catch {
            return false
        }
    }
}
