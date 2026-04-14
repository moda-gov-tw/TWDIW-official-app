//
//  SecureExportError.swift
//  DigitalWallet
//

import Foundation

// MARK: - Concrete Implementation

public final class SecureURLValidatorManager: SecureURLValidatingProtocol {
    
    private let filenameRegex = "^[A-Za-z0-9._\\-\\u4E00-\\u9FFF]{1,64}$"
    private let fileManager = FileManager.default
    
    public init() {}
    
    public func validateExisting(url: URL, root: URL) throws {
        let canonical = url.standardizedFileURL.resolvingSymlinksInPath()
        // 邊界檢查
        let rootCanon = root.standardizedFileURL.resolvingSymlinksInPath()
        let rootPath = rootCanon.path.hasSuffix("/") ? rootCanon.path : rootCanon.path + "/"
        guard canonical.path.hasPrefix(rootPath) else { throw SecureURLValidationError.outsideSandbox }
        
        // 必須存在，且不是 symlink
        let values = try canonical.resourceValues(forKeys: [.isSymbolicLinkKey, .isRegularFileKey, .isDirectoryKey])
        if values.isSymbolicLink == true { throw SecureURLValidationError.existingIsSymlink }
    }
    
    public func validateDestinationForCreate(url: URL, root: URL) throws {
        // 1) 檢查檔名（目的地可能還不存在，只能驗檔名）
        let fileName = url.lastPathComponent
        let fileNameRegex = try! NSRegularExpression(pattern: filenameRegex) // e.g. "^[A-Za-z0-9._\\-\\u4E00-\\u9FFF]{1,64}$"
        let fileNameRange = NSRange(location: 0, length: fileName.utf16.count)
        guard fileNameRegex.firstMatch(in: fileName, options: [], range: fileNameRange) != nil else {
            throw SecureURLValidationError.invalidFilename
        }

        // 2) 取得父層實際路徑並檢查
        let parentDirectoryURL = url
            .deletingLastPathComponent()
            .standardizedFileURL
            .resolvingSymlinksInPath()

        var isDirectory: ObjCBool = false
        guard fileManager.fileExists(atPath: parentDirectoryURL.path, isDirectory: &isDirectory) else {
            throw SecureURLValidationError.parentNotExists
        }
        guard isDirectory.boolValue else {
            throw SecureURLValidationError.parentNotDirectory
        }

        // 父層不能是 symlink
        let parentResourceValues = try parentDirectoryURL.resourceValues(forKeys: [.isSymbolicLinkKey])
        if parentResourceValues.isSymbolicLink == true {
            throw SecureURLValidationError.parentIsSymlink
        }

        // 3) 邊界檢查：用 FileManager.getRelationship 判斷是否位於 root 之內
        let canonicalRootDirectoryURL = root.standardizedFileURL.resolvingSymlinksInPath()
        var relationship: FileManager.URLRelationship = .other
        try fileManager.getRelationship(&relationship, ofDirectoryAt: canonicalRootDirectoryURL, toItemAt: parentDirectoryURL)
        guard relationship == .contains || relationship == .same else {
            throw SecureURLValidationError.outsideSandbox
        }

        // 4) 若目的地「已存在」，補查本身不可是 symlink
        if fileManager.fileExists(atPath: url.path) {
            let destinationResourceValues = try url.resourceValues(forKeys: [.isSymbolicLinkKey])
            if destinationResourceValues.isSymbolicLink == true {
                throw SecureURLValidationError.existingIsSymlink
            }
        }
    }
}
