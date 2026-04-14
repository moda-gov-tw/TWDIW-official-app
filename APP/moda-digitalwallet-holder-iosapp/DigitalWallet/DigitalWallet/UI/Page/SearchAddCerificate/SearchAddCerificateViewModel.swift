//
//  SearchAddCerificateViewModel.swift
//  DigitalWallet
//

import Combine
import Foundation

class SearchAddCerificateViewModel: BaseViewModel {
    
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
        case searchVCLog(SearchVCLog)
        /// 搜尋結果
        case searchCertificateResult(VCItems)
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
    let imageLoadManager: ImageLoadProtocol
    
    let errorSubject = PassthroughSubject<ErrorType, Never>()
    let reloadTableViewSubject = PassthroughSubject<TableViewType, Never>()
    let popPassthroughSubject = PassthroughSubject<Void, Never>()
    let showLoadingPassthroughSubject = PassthroughSubject<Void, Never>()
    let stopLoadingSubject = PassthroughSubject<Void, Never>()
    let updateVPSourcePassthroughSubject = PassthroughSubject<VPSource?, Never>()
    let toWebViewSubject = PassthroughSubject<(urlString: String, name: String), Never>()
    // 可優化的實作
    let toOpenURLSubject = PassthroughSubject<(urlString: String, name: String), Never>()
    /// TextField狀態
    var textFieldStatus: TextFieldStatus = .noSearch
    /// TableViewCell種類
    var tableviewCell: [SearchCertificateCellType] = []
    /// 所有憑證
    var vcItems: [VCItems]
    private var cancelSet = Set<AnyCancellable>()
    /// 搜尋字串
    @Published var searchString: String?
    /// 搜尋紀錄
    @Published var searchVCsLogArray: [SearchVCLog] = []
    
    
    init(repository: UserRepositoryProtocol,
         parseLinkManager: ParseLinkProtocol,
         imageLoadManager: ImageLoadProtocol,
         vcItems: [VCItems]) {
        self.repository = repository
        self.parseLinkManager = parseLinkManager
        self.imageLoadManager = imageLoadManager
        self.vcItems = vcItems
        super.init()
        getSearchVCsLog()
        dataBinding()
    }
    
    private func dataBinding() {
        $searchVCsLogArray
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
                generateTableViewCellType(searchString: str, searchLogs: searchVCsLogArray)
            }.store(in: &cancelSet)
    }
    
    private func generateTableViewCellType(searchString: String, searchLogs: [SearchVCLog]) {
        tableviewCell.removeAll()
        textFieldStatus = searchString.isEmpty ? .noSearch : .searching
        
        var matchItems: [VCItems] = []
        if textFieldStatus == .searching {
            matchItems = vcItems.filter { [weak self] in
                guard let self = self else { return false }
                return getSearchMatchResults(strFull: $0.name, strSearch: searchString)
            }
            textFieldStatus = matchItems.isEmpty ? .noSearchResults : .searching
        }
                
        switch textFieldStatus {
        case .searching:
            generateSearchingCellType(matchItems: matchItems)
            break
        case .noSearchResults:
            reloadTableViewSubject.send(.hidden(NSLocalizedString("NoSearchResults", comment: "")))
            break
        case .noSearch:
            generateNoSearchCellType(searchLogs: searchLogs)
            break
        }
    }
    
    private func generateSearchingCellType(matchItems: [VCItems]) {
        tableviewCell.removeAll()
        tableviewCell.append(.searchTitle(NSLocalizedString("SearchResults", comment: "")))
        
        matchItems.forEach {
            tableviewCell.append(.searchCertificateResult($0))
        }
        
        reloadTableViewSubject.send(.show)
        
    }
    
    private func generateNoSearchCellType(searchLogs: [SearchVCLog]) {
        tableviewCell.append(.searchTitle(NSLocalizedString("SearchLogs", comment: "")))
        searchLogs.forEach {
            tableviewCell.append(.searchVCLog($0))
        }
        reloadTableViewSubject.send(.show)
    }
    
}

// MARK: 搜尋紀錄
extension SearchAddCerificateViewModel {
    /// 儲存搜尋紀錄
    func saveSearchVCsLog(searchString: String) {
        guard !searchString.isEmpty else { return }
        if searchVCsLogArray.count > 4 {
            repository.deleteSearchVCLog(searchVCLogUUID: searchVCsLogArray.last?.searchVCsLogUUID ?? UUID())
                .flatMap { [weak self] _ -> AnyPublisher<Void, Error> in
                    return self?.repository.saveSearchVCsLog(searchVCLogName: searchString) ?? Just(()).setFailureType(to: Error.self).eraseToAnyPublisher()
                }
                .sink { completion in
                    guard case let .failure(error) = completion else { return }
                } receiveValue: { [weak self] _ in
                    guard let self = self else { return }
                    getSearchVCsLog()
                }.store(in: &cancelSet)
        } else {
            repository.saveSearchVCsLog(searchVCLogName: searchString)
                .sink { completion in
                    guard case let .failure(error) = completion else { return }
                } receiveValue: { [weak self] _ in
                    guard let self = self else { return }
                    getSearchVCsLog()
                }.store(in: &cancelSet)
        }
    }
    
    /// 獲取搜尋紀錄
    func getSearchVCsLog() {
        repository.getSearchVCsLog()
            .compactMap{ $0 }
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] items in
                guard let self = self else { return }
                searchVCsLogArray = items.sorted(by: { $0.creatDate ?? .distantPast > $1.creatDate ?? .distantPast })
            }.store(in: &cancelSet)
    }
    
    /// 刪除搜尋紀錄
    func deleteSearchVCLog(searchVCLog: SearchVCLog) {
        repository.deleteSearchVCLog(searchVCLogUUID: searchVCLog.searchVCsLogUUID!)
            .sink { completion in
                guard case let .failure(error) = completion else { return }
            } receiveValue: { [weak self] _ in
                guard let self = self else { return }
                getSearchVCsLog()
            }.store(in: &cancelSet)
    }
}


// MARK: 點擊Certificate
extension SearchAddCerificateViewModel {
    // 獲取該筆資料
    func getDeepLinkAndTransactionId(checkData: VCItems) {
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

extension SearchAddCerificateViewModel {
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
