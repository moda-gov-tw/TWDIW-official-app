//
//  OperationLogViewModel.swift
//  DigitalWallet
//

import Combine
import Foundation
import ZIPFoundation

class OperationLogViewModel {
    
    public var ascending = false
    public var selectedType: RecordLogType = .authorizationRecord
    public let reloadPassthroughSubject = PassthroughSubject<Void, Never>()
    public let shareRecordShbject = PassthroughSubject<URL, Never>()
    
    private let repository: UserRepositoryProtocol
    private let validatorManager: SecureURLValidatingProtocol
    private(set) var authorizationRecords: [CardRecord] = []
    private(set) var changeRecords: [CardRecord] = []
    private let pageSize = 10
    private var hasMore = true
    private var offset = 0
    private let dbQueue = DispatchQueue(label: "db.read.serial")
    
    init(repository: UserRepositoryProtocol, validatorManager: SecureURLValidatingProtocol) {
        self.repository = repository
        self.validatorManager = validatorManager
    }
    
    public func selected(type: RecordLogType) {
        if selectedType == type {
            return
        }
        selectedType = type
        resetData()
    }
    
    public func getAuthorizationRecords() {
        guard hasMore else { return }
        
        dbQueue.async { [weak self] in
            guard let self else { return }
            let page = repository.fetchAuthorizationRecords(ascending: ascending,
                                                            limit: pageSize,
                                                            offset: offset,
                                                            start: nil,
                                                            end: nil)
            
            DispatchQueue.main.async {
                self.authorizationRecords.append(contentsOf: page)
                self.offset += page.count
                self.hasMore = page.count == self.pageSize
                self.reloadPassthroughSubject.send()
            }
        }
    }
    
    public func getChangeRecords() {
        guard hasMore else { return }
        
        dbQueue.async { [weak self] in
            guard let self else { return }
            let page = repository.fetchChangeRecords(ascending: ascending,
                                                     limit: pageSize,
                                                     offset: offset,
                                                     start: nil,
                                                     end: nil)
            
            DispatchQueue.main.async {
                self.changeRecords.append(contentsOf: page)
                self.offset += page.count
                self.hasMore = page.count == self.pageSize
                self.reloadPassthroughSubject.send()
            }
        }
    }
    
    public func loadNextPage() {
        switch selectedType {
        case .authorizationRecord:
            getAuthorizationRecords()
            break
        case .changeRecord:
            getChangeRecords()
            break
        }
    }
    
    public func updateRecordExpand(indexPath: IndexPath) {
        switch selectedType {
        case .authorizationRecord:
            authorizationRecords[indexPath.row].isExpand.toggle()
        case .changeRecord:
            changeRecords[indexPath.row].isExpand.toggle()
        }
        reloadPassthroughSubject.send()
    }
    
    public func resetData() {
        offset = 0
        hasMore = true
        authorizationRecords.removeAll()
        changeRecords.removeAll()
    }
    
    public func prepareToShare() {
        do {
            let authorizationRecords = repository.fetchAuthorizationRecords(ascending: false, limit: nil, offset: 0, start: nil, end: nil)
            let authorizationRecordExports = authorizationRecords.map { record in
                let jsonStr = """
                            {
                              "日期":"\(record.getCreateDate())",
                              "標題":"\(record.recordMessage)",
                              "授權憑證":"\(record.vcNames ?? "")",
                              "單位":"\(record.client ?? "")",
                              "目的":"\(record.purpose ?? "")",
                              "資料":"\(record.applyInfos ?? "")"
                            }
                            """
                return jsonStr
            }.joined(separator: ",")
            
            let changeRecords = repository.fetchChangeRecords(ascending: false, limit: nil, offset: 0, start: nil, end: nil)
            let changeRecordExports = changeRecords.map { record in
                let jsonStr = """
                            {
                              "日期":"\(record.getCreateDate())",
                              "標題":"\(record.recordMessage)",
                              "類型":"\(record.type?.title ?? "")"
                            }
                            """
                return jsonStr
            }.joined(separator: ",")
            
            let authArrayJSON = "[\(authorizationRecordExports)]"
            let changeArrayJSON = "[\(changeRecordExports)]"
            let formatter = DateFormatter()
            formatter.calendar = Calendar(identifier: .gregorian)
            formatter.locale = Locale(identifier: "en_US_POSIX")
            formatter.timeZone = .current
            formatter.dateFormat = "yyyy_MM_dd_HHmm"

            let zipFileName = ""
                .appending("數位憑證皮夾操作日誌_")
                .appending(formatter.string(from: Date()))
            
            let zipURL = try exportZIP(authArrayJSON: authArrayJSON, changeArrayJSON: changeArrayJSON, zipFileName: zipFileName)
            shareRecordShbject.send(zipURL)
        } catch {
            // 匯出失敗
        }
    }
    
    func exportZIP(authArrayJSON: String, changeArrayJSON: String, zipFileName: String) throws -> URL {
        let fileManager = FileManager.default
        let tempDir = fileManager.temporaryDirectory

        // 1) 準備 logs 資料夾（存在就沿用，不整夾刪除）
        let logsDir = tempDir.appendingPathComponent("logs", isDirectory: true)
        if !fileManager.fileExists(atPath: logsDir.path) {
            try fileManager.createDirectory(at: logsDir, withIntermediateDirectories: true)
        }

        // 2) 準備 logs 內檔案 URL（如已存在同名檔，先移除再寫入）
        let authURL = logsDir.appendingPathComponent("授權紀錄.json")
        let changeURL = logsDir.appendingPathComponent("異動紀錄.json")

        if fileManager.fileExists(atPath: authURL.path)   { try fileManager.removeItem(at: authURL) }
        if fileManager.fileExists(atPath: changeURL.path) { try fileManager.removeItem(at: changeURL) }

        // 3) 寫入 JSON（原子 & 完整檔案保護）
        try Data(authArrayJSON.utf8).write(to: authURL,   options: [.completeFileProtectionUnlessOpen])
        try Data(changeArrayJSON.utf8).write(to: changeURL, options: [.completeFileProtectionUnlessOpen])

        // 4) 準備 ZIP 目的地（temporaryDirectory 下）
        let finalZipName = zipFileName.hasSuffix(".zip") ? zipFileName : (zipFileName + ".zip")
        let zipURL = tempDir.appendingPathComponent(finalZipName)

        if fileManager.fileExists(atPath: zipURL.path) {
            try fileManager.removeItem(at: zipURL)
        }

        // 5) 產生 ZIP 並將兩個檔案以 logs/xxxx.json 路徑放入（避免解壓後檔案散落）
        let archive = try Archive(url: zipURL, accessMode: .create)
        try archive.addEntry(with: "logs/授權紀錄.json", fileURL: authURL)
        try archive.addEntry(with: "logs/異動紀錄.json", fileURL: changeURL)

        // 6) 避免被 iCloud 備份（可選）
        var rv = URLResourceValues()
        rv.isExcludedFromBackup = true
        var mutableZipURL = zipURL
        try? mutableZipURL.setResourceValues(rv)

        return zipURL
    }
}
