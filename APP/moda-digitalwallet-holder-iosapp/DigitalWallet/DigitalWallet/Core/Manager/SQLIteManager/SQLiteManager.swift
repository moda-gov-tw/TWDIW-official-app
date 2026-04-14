//
//  SQLiteManager.swift
//  DigitalWallet
//

import Foundation
import Combine
import SQLite

struct MyWallet: Codable {
    let uuid: UUID
    let pinCode: String
    let name: String
    let keyData: String?
    let didFile: String?
    let createDate: Date?
    let autoLogout: Int64?
}

struct UserCard: Codable {
    let uuid: UUID?
    let verifiableCredential: String?
    let userId: UUID?
    let state: Int64?
    let orgName: String?
    let iss: String?
    let displayName: String?
    let createDate: Date?
    let cardImage: Data?
    let remind: Int64?
    let exp: Int64?
    let updateDate: Date?
    let trustBadge: Bool?
}

struct MyFavoriteList: Codable, NameSortableProtocol {
    let walletId: String?
    let favoriteCertificateUUID: UUID?
    let vpUid: String?
    let name: String?
    let verifierModuleUrl: String?
    let logoUrl: String?
    let creatDate: Date?
    let updateDate: Date?
}

struct SearchCertificateLog: Codable, NameSortableProtocol {
    let walletId: String?
    let searchCertificatesLogUUID: UUID?
    let name: String?
    let creatDate: Date?
}

struct SearchVCLog: Codable, NameSortableProtocol {
    let walletId: String?
    let searchVCsLogUUID: UUID?
    let name: String?
    let creatDate: Date?
}

class SQLiteManager: DatabaseModelProtocol {
    
    static let shared = SQLiteManager()
    
    private let dbName = "db.sqlite"
    private var db: Connection!
    
    private let keychainKey = "sqlcipherkey"
    
    // Tables
    private let walletTable = Table("Wallet")
    private let verifiableCredentialTable = Table("VerifiableCredential")
    private let cardRecordTable = Table("CardRecordTable")
    private let favoriteCertificateTable = Table("FavoriteCertificate")
    private let searchCertificatesTable = Table("SearchCertificates")
    private let searchVCsTable = Table("SearchVCs")
    private let credentialRecordTable = Table("CredentialRecord")
    private let operationRecordTable = Table("OperationRecord")
    private let presentationRecordTable = Table("PresentationRecord")
    
    // Aliases
    typealias Expression = SQLite.Expression
    
    private init() {}
    
    // MARK: Open + Migration
    public func configure() throws {
        try openAndMigrate()
    }
    
    private func openAndMigrate() throws {
        let dir = try FileManager.default.url(for: .applicationSupportDirectory, in: .userDomainMask, appropriateFor: nil, create: true)
        try FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
        let path = dir.appendingPathComponent(dbName).path 
        
        db = try Connection(path)
        db.busyTimeout = 5.0
        
        // SQLCipher key
        let key = getOrCreateEncryptionKey()
        try db.key(key)
        
        // Versioned migrations
        let current = try userVersion()
        if current < 1 {
            try createSchemaV1()
            try setUserVersion(1)
        }
        if current < 2 {
            try migrateV2_AddOptionalColumnsIfMissing()
            try setUserVersion(2)
        }
        if current < 3 {
            try addRecommendedIndexes()
            try setUserVersion(3)
        }
        if current < 4 {
            try migrateV3_CreateRecordTable()
            try migrateV3_migrationCardRecords()
            try setUserVersion(4)
        }
        if current < 5 {
            try migrateV4_migrationCardState()
            try setUserVersion(5)
        }
    }
    
    private func userVersion() throws -> Int {
        let row = try db.scalar("PRAGMA user_version") as! Int64
        return Int(row)
    }
    
    private func setUserVersion(_ v: Int) throws {
        try db.run("PRAGMA user_version = \(v)")
    }
    
    // MARK: Schema V1
    
    private func createSchemaV1() throws {
        // Wallet
        try createWalletTable()
        // VerifiableCredential
        try createVerifiableCredentialTable()
        // CardRecord
        try createCardRecordTable()
        // FavoriteCertificate
        try creatFavoriteCertificatesTable()
        // SearchCertificates
        try creatSearchCertificatesTable()
        // SearchVCs
        try creatSearchVCsTable()
        // CredentialRecord
        try createCredentialRecordTable()
        // OperationRecord
        try createOperationRecordTable()
        // PresentationRecord
        try createPresentationRecordTable()
    }
    
    // V2: Backfill columns in case existing DBs were created with older optionality
    private func migrateV2_AddOptionalColumnsIfMissing() throws {
        // Wallet: ensure autoUpdate exists
        if try !columnExists(in: "Wallet", columnName: "autoUpdate") {
            let autoUpdate = Expression<Bool?>("autoUpdate")
            try db.run(walletTable.addColumn(autoUpdate))
        }
        // VerifiableCredential: ensure updateDate + trustBadge exist
        if try !columnExists(in: "VerifiableCredential", columnName: "updateDate") {
            let updateDate = Expression<Date?>("updateDate")
            try db.run(verifiableCredentialTable.addColumn(updateDate))
        }
        if try !columnExists(in: "VerifiableCredential", columnName: "trustBadge") {
            let trustBadge = Expression<Bool?>("trustBadge")
            try db.run(verifiableCredentialTable.addColumn(trustBadge))
        }
        if try !columnExists(in: "VerifiableCredential", columnName: "remind") {
            let remind = Expression<Int64?>("remind")
            try db.run(verifiableCredentialTable.addColumn(remind, defaultValue: 0)) 
        }
        if try !columnExists(in: "VerifiableCredential", columnName: "exp") {
            let exp = Expression<Int64?>("exp")
            try db.run(verifiableCredentialTable.addColumn(exp))
        }
        // CardRecordTable: ensure client/purpose/applyInfos exist
        if try !columnExists(in: "CardRecordTable", columnName: "client") {
            let client = Expression<String?>("client")
            try db.run(cardRecordTable.addColumn(client))
        }
        if try !columnExists(in: "CardRecordTable", columnName: "purpose") {
            let purpose = Expression<String?>("purpose")
            try db.run(cardRecordTable.addColumn(purpose))
        }
        if try !columnExists(in: "CardRecordTable", columnName: "applyInfos") {
            let applyInfos = Expression<String?>("applyInfos")
            try db.run(cardRecordTable.addColumn(applyInfos))
        }
    }
    
    private func addRecommendedIndexes() throws {
        try db.run("CREATE INDEX IF NOT EXISTS idx_wallet_uuid ON Wallet(uuid)")
        try db.run("CREATE INDEX IF NOT EXISTS idx_vc_userId ON VerifiableCredential(userId)")
        try db.run("CREATE INDEX IF NOT EXISTS idx_vc_uuid ON VerifiableCredential(uuid)")
        try db.run("CREATE INDEX IF NOT EXISTS idx_card_vcUUID ON CardRecordTable(vcUUID)")
    }
    
    private func migrateV3_CreateRecordTable() throws {
        // CredentialRecord
        try createCredentialRecordTable()
        // OperationRecord
        try createOperationRecordTable()
        // PresentationRecord
        try createPresentationRecordTable()
    }
    
    private func migrateV3_migrationCardRecords() throws {
        let cardRecords = fetchOldCardRecords()
        cardRecords.forEach { cardRecord in
            guard let vcUUID = cardRecord.vcUUID,
                  let verifiableCredential = fetchVerifiableCredential(vcUUID: vcUUID),
                  let walletId = verifiableCredential.userId,
                  let status = cardRecord.type,
                  let createDate = cardRecord.createDate else {
                return
            }
            if cardRecord.type == .verify {
                saveCredentialRecord(uid: UUID(),
                                     walletId: walletId,
                                     vcId: vcUUID,
                                     text: cardRecord.recordMessage,
                                     authorizationUnit: cardRecord.client ?? "",
                                     authorizationPurpose: cardRecord.purpose ?? "",
                                     authorizationField: cardRecord.applyInfos ?? "",
                                     datetime: createDate )
            } else {
                saveOperationRecord(uid: UUID(),
                                    walletId: walletId,
                                    vcId: vcUUID,
                                    text: cardRecord.recordMessage,
                                    status: status.rawValue,
                                    datetime: createDate)
            }
        }
    }
    
    private func migrateV4_migrationCardState() throws {
        let userCards: [UserCard] = try db.prepare(verifiableCredentialTable).map { row in
            return try row.decode()
        }
        let uuidExpression = Expression<UUID>("uuid")
        let remindExpression = Expression<Int64?>("remind")
        
        try userCards.forEach { card in
            if card.state == 2, let cardUUID = card.uuid {
                let update = verifiableCredentialTable
                    .filter(uuidExpression == cardUUID)
                    .update(remindExpression <- -1)
                try db.run(update)
            }
        }
    }
    
    private func createWalletTable() throws {
        let uuid = Expression<UUID>("uuid")
        let pinCode = Expression<String>("pinCode")
        let name = Expression<String>("name")
        let keyData = Expression<String?>("keyData") // Optional aligned with model
        let didFile = Expression<String?>("didFile") // Optional
        let createDate = Expression<Date?>("createDate") // Optional
        let autoLogout = Expression<Int64?>("autoLogout")
        let autoUpdate = Expression<Bool?>("autoUpdate")
        
        try db.run(walletTable.create(ifNotExists: true) { t in
            t.column(uuid)
            t.column(pinCode)
            t.column(name)
            t.column(keyData)
            t.column(didFile)
            t.column(createDate)
            t.column(autoLogout)
            t.column(autoUpdate)
            t.primaryKey(uuid)
        })
    }
    
    private func createVerifiableCredentialTable() throws {
        let vc = Expression<String>("verifiableCredential")
        let vcUUID = Expression<UUID>("uuid")
        let userId = Expression<UUID>("userId")
        let state = Expression<Int64>("state")
        let orgName = Expression<String?>("orgName")
        let iss = Expression<String?>("iss")
        let displayName = Expression<String?>("displayName")
        let vcCreateDate = Expression<Date?>("createDate")
        let cardImage = Expression<Data?>("cardImage")
        let updateDate = Expression<Date?>("updateDate")
        let trustBadge = Expression<Bool?>("trustBadge")
        let remind = Expression<Int64?>("remind")
        let exp = Expression<Int64?>("exp")
        
        try db.run(verifiableCredentialTable.create(ifNotExists: true) { t in
            t.column(vc)
            t.column(vcUUID)
            t.column(userId)
            t.column(state)
            t.column(orgName)
            t.column(iss)
            t.column(displayName)
            t.column(vcCreateDate)
            t.column(cardImage)
            t.column(updateDate)
            t.column(trustBadge)
            t.column(remind, defaultValue: 0)
            t.column(exp)
            t.primaryKey(vcUUID)
        })
    }
    
    private func createCardRecordTable() throws {
        let cardVCUUID = Expression<UUID>("vcUUID")
        let cardRecordUUID = Expression<UUID>("recordUUID")
        let recordMessage = Expression<String>("recordMessage")
        let recordCreateDate = Expression<Date>("createDate")
        let cardRecordType = Expression<Int64>("cardRecordType")
        let client = Expression<String?>("client")
        let purpose = Expression<String?>("purpose")
        let applyInfos = Expression<String?>("applyInfos")
        
        try db.run(cardRecordTable.create(ifNotExists: true) { t in
            t.column(cardVCUUID)
            t.column(cardRecordUUID)
            t.column(recordMessage)
            t.column(recordCreateDate)
            t.column(cardRecordType)
            t.column(client)
            t.column(purpose)
            t.column(applyInfos)
            t.primaryKey(cardRecordUUID)
        })
    }
    
    private func creatFavoriteCertificatesTable() throws {
        let walletId = Expression<String>("walletId")
        let favoriteCertificateUUID = Expression<UUID>("favoriteCertificateUUID")
        let vpUid = Expression<String>("vpUid")
        let name = Expression<String>("name")
        let verifierModuleUrl = Expression<String>("verifierModuleUrl")
        let logoUrl = Expression<String>("logoUrl")
        let createData = Expression<Date>("creatDate")
        let updateDate = Expression<Date>("updateDate")
        
        try db.run(favoriteCertificateTable.create(ifNotExists: true) { t in
            t.column(walletId)
            t.column(favoriteCertificateUUID)
            t.column(vpUid)
            t.column(name)
            t.column(verifierModuleUrl)
            t.column(logoUrl)
            t.column(createData)
            t.column(updateDate)
        })
        
    }
    
    /// 建立搜尋憑證紀錄
    func creatSearchCertificatesTable() throws {
        let walletId = Expression<String>("walletId")
        let searchCertificatesLogUUID = Expression<UUID>("searchCertificatesLogUUID")
        let name = Expression<String>("name")
        let createData = Expression<Date>("creatDate")
        let updateDate = Expression<Date>("updateDate")
        
        try db.run(searchCertificatesTable.create(ifNotExists: true) { t in
            t.column(walletId)
            t.column(searchCertificatesLogUUID)
            t.column(name)
            t.column(createData)
            t.column(updateDate)
        })
    }
    
    /// 建立搜尋VC紀錄
    func creatSearchVCsTable() throws {
        let walletId = Expression<String>("walletId")
        let searchVCsLogUUID = Expression<UUID>("searchVCsLogUUID")
        let name = Expression<String>("name")
        let createData = Expression<Date>("creatDate")
        let updateDate = Expression<Date>("updateDate")
    
        try db.run(searchVCsTable.create(ifNotExists: true) { t in
            t.column(walletId)
            t.column(searchVCsLogUUID)
            t.column(name)
            t.column(createData)
            t.column(updateDate)
        })
    }
    
    private func createCredentialRecordTable() throws {
        let uid = Expression<UUID>("uid")
        let walletId = Expression<UUID>("walletId")
        let vcId = Expression<UUID>("vcId")
        let text = Expression<String>("text")
        let authorizationUnit = Expression<String>("authorizationUnit")
        let authorizationPurpose = Expression<String>("authorizationPurpose")
        let authorizationField = Expression<String>("authorizationField")
        let datetime = Expression<Date>("datetime")
        
        try db.run(credentialRecordTable.create(ifNotExists: true) { t in
            t.column(uid)
            t.column(walletId)
            t.column(vcId)
            t.column(text)
            t.column(authorizationUnit)
            t.column(authorizationPurpose)
            t.column(authorizationField)
            t.column(datetime)
        })
    }
    
    private func createOperationRecordTable() throws {
        let uid = Expression<UUID>("uid")
        let walletId = Expression<UUID>("walletId")
        let vcId = Expression<UUID>("vcId")
        let text = Expression<String>("text")
        let status = Expression<Int64>("status")
        let datetime = Expression<Date>("datetime")
        
        try db.run(operationRecordTable.create(ifNotExists: true) { t in
            t.column(uid)
            t.column(walletId)
            t.column(vcId)
            t.column(text)
            t.column(status)
            t.column(datetime)
        })
    }
    
    private func createPresentationRecordTable() throws {
        let uid = Expression<UUID>("uid")
        let walletId = Expression<UUID>("walletId")
        let text = Expression<String>("text")
        let vcIds = Expression<String>("vcIds")
        let vcNames = Expression<String>("vcNames")
        let authorizationUnit = Expression<String>("authorizationUnit")
        let authorizationPurpose = Expression<String>("authorizationPurpose")
        let authorizationFields = Expression<String>("authorizationFields")
        let datetime = Expression<Date>("datetime")
        
        try db.run(presentationRecordTable.create(ifNotExists: true) { t in
            t.column(uid)
            t.column(walletId)
            t.column(text)
            t.column(vcIds)
            t.column(vcNames)
            t.column(authorizationUnit)
            t.column(authorizationPurpose)
            t.column(authorizationFields)
            t.column(datetime)
        })
    }
}
    

extension SQLiteManager {
    func createWallet(walletName: String,
                      pinCode: String?,
                      uuid: UUID,
                      didFile: String,
                      keyData: String?,
                      createDate: Date = Date()) {
        do {
            let nameExpression = Expression<String>("name")
            let pinCodeExpression = Expression<String>("pinCode")
            let uuidExpression = Expression<UUID>("uuid")
            let didFileExpression = Expression<String>("didFile")
            let keyDataExpression = Expression<String>( "keyData")
            let createDataExpression = Expression<Date>("createDate")
            let insert = walletTable.insert(nameExpression <- walletName,
                                            pinCodeExpression <- pinCode ?? "",
                                            uuidExpression <- uuid,
                                            didFileExpression <- didFile,
                                            keyDataExpression <- keyData ?? "",
                                            createDataExpression <- createDate
            )
            try db.run(insert)
            
            /*紀錄當前預設登入的皮夾*/
            UserDefaultManager.shared.setObject(value: uuid, key: .DefaultLoginWallet)
        } catch {
            
        }
    }
    
    func fetchWallets() -> [MyWallet]?  {
        do {
            let wallets: [MyWallet] = try db.prepare(walletTable).map { row in
                return try row.decode()
            }
            return wallets
        } catch {
            return []
        }
    }
    
    func fetchWallet(uuid: UUID) -> MyWallet? {
        do {
            let uuidExpression = Expression<UUID>("uuid")
            let wallets: [MyWallet] = try db.prepare(walletTable.filter(uuidExpression == uuid).limit(1)).map { row in
                return try row.decode()
            }
            return wallets.first
        } catch {
            return nil
        }
    }
    
    func saveVerifiableCredential(vcUUID: UUID = UUID(),
                                  userId: UUID,
                                  verifiableCredential: String,
                                  state: CardStatus,
                                  iss: String,
                                  orgName: String,
                                  cardImage: Data?,
                                  remind: Int64,
                                  exp: Int64,
                                  displayName: String?) {
        do {
            let verifiableCredentialExpression = Expression<String>( "verifiableCredential")
            let uuidExpression = Expression<UUID>( "uuid")
            let userIdExpression = Expression<UUID>( "userId")
            let stateExpression = Expression<Int64>( "state")
            let orgNameExpression = Expression<String>( "orgName")
            let issExpression = Expression<String>( "iss")
            let displayNameExpression = Expression<String>( "displayName")
            let createDataExpression = Expression<Date>( "createDate")
            let cardImageExpression = Expression<Data?>( "cardImage")
            let remindExpression = Expression<Int64?>("remind")
            let expExpression = Expression<Int64>("exp")
            let vcUUID = vcUUID
            let insert = verifiableCredentialTable.insert(verifiableCredentialExpression <- verifiableCredential,
                                                          uuidExpression <- vcUUID,
                                                          userIdExpression <- userId,
                                                          stateExpression <- Int64(state.rawValue),
                                                          orgNameExpression <- orgName,
                                                          issExpression <- iss,
                                                          displayNameExpression <- displayName ?? "",
                                                          createDataExpression <- Date(),
                                                          cardImageExpression <- cardImage,
                                                          remindExpression <- remind,
                                                          expExpression <- exp
            )
            try db.run(insert)
        } catch {
            
        }
    }
    
    func insertVerifiableCredential(vcUUID: UUID = UUID(),
                                    userId: UUID,
                                    verifiableCredential: String,
                                    state: CardStatus,
                                    iss: String,
                                    orgName: String,
                                    cardImage: Data?,
                                    remind: Int64,
                                    exp: Int64,
                                    displayName: String?) {
        do {
            let verifiableCredentialExpression = Expression<String>( "verifiableCredential")
            let uuidExpression = Expression<UUID>( "uuid")
            let userIdExpression = Expression<UUID>( "userId")
            let stateExpression = Expression<Int64>( "state")
            let orgNameExpression = Expression<String>( "orgName")
            let issExpression = Expression<String>( "iss")
            let displayNameExpression = Expression<String>( "displayName")
            let createDataExpression = Expression<Date>( "createDate")
            let cardImageExpression = Expression<Data?>( "cardImage")
            let remindExpression = Expression<Int64?>("remind")
            let expExpression = Expression<Int64>("exp")
            let vcUUID = vcUUID
            let insert = verifiableCredentialTable.insert(verifiableCredentialExpression <- verifiableCredential,
                                                          uuidExpression <- vcUUID,
                                                          userIdExpression <- userId,
                                                          stateExpression <- Int64(state.rawValue),
                                                          orgNameExpression <- orgName,
                                                          issExpression <- iss,
                                                          displayNameExpression <- displayName ?? "",
                                                          createDataExpression <- Date(),
                                                          cardImageExpression <- cardImage,
                                                          remindExpression <- remind,
                                                          expExpression <- exp
            )
            try db.run(insert)
        } catch {
            
        }
        
    }
    
    func updateVerifiableCredentialStatus(cardId: UUID, state: CardStatus) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let stateExpression = Expression<Int64>( "state")
            let update = verifiableCredentialTable.filter(uuidExpression == cardId)
                .update(stateExpression <- Int64(state.rawValue))
            try db.run(update)
        } catch {
            
        }
    }
    
    func updateVerifiableCredentialRemind(cardId: UUID, remind: Int64) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let remindExpression = Expression<Int64?>("remind")
            let update = verifiableCredentialTable.filter(uuidExpression == cardId)
                .update(remindExpression <- remind)
            try db.run(update)
        } catch {
            
        }
    }
    
    func fetchVerifiableCredentials(userId: UUID, ascending: Bool) -> [UserCard] {
        do {
            let userIdExpression = Expression<UUID>("userId")
            let createDateExpression = Expression<Date>("createDate")

            // 基本查詢
            var query = verifiableCredentialTable.filter(userIdExpression == userId)

            // 排序：ascending = true → 舊到新，false → 新到舊
            if ascending {
                query = query.order(createDateExpression.asc)
            } else {
                query = query.order(createDateExpression.desc)
            }

            let userCards: [UserCard] = try db.prepare(query).map { row in
                return try row.decode()
            }
            return userCards
        } catch {
            return []
        }
    }
    
    func fetchVerifiableCredential(vcUUID: UUID) -> UserCard? {
        do {
            let query = verifiableCredentialTable.filter(Expression<UUID>("uuid") == vcUUID)
            if let row = try db.pluck(query) {
                return try row.decode() as UserCard
            }
        } catch {
            
        }
        return nil
    }
    
    func deleteVerifiableCredential(cardId: UUID) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let delete = verifiableCredentialTable.filter(uuidExpression == cardId).delete()
            try db.run(delete)
        } catch {
            
        }
    }
    
    func updatePinCode(userId: UUID, pinCode: String) throws {
        do {
            let uuidExpression = Expression<UUID>("uuid")
            let pinCodeExpression = Expression<String>("pinCode")
            let update = walletTable.filter(uuidExpression == userId).update(pinCodeExpression <- pinCode)
            try db.run(update)
        } catch {
            
        }
    }
    
    func updateWalletName(userId: UUID, name: String) throws -> Bool {
        let uuidExpression = Expression<UUID>("uuid")
        let nameExpression = Expression<String>("name")
        let update = walletTable.filter(uuidExpression == userId).update(nameExpression <- name)
        try db.run(update)
        return true
    }
    
    func updateWalletAutoLogout(userId: UUID, autoLogout: Int16) throws -> Bool {
        let uuidExpression = Expression<UUID>("uuid")
        let autoLogoutExpression = Expression<Int64?>("autoLogout")
        let update = walletTable.filter(uuidExpression == userId).update(autoLogoutExpression <- Int64(autoLogout))
        try db.run(update)
        return true
    }
    
    func saveCardRecord(vcUUID: UUID,
                        cardType: CardType,
                        recordMessage: String,
                        client: String?,
                        purpose: String?,
                        applyInfos: String?,
                        createDate: Date) {
        do {
            let vcUUIDExpression = Expression<UUID>("vcUUID")
            let cardRecordTypeExpression = Expression<Int64>("cardRecordType")
            let recordUUIDExpression = Expression<UUID>("recordUUID")
            let recordMessageExpression = Expression<String>("recordMessage")
            let clientExpression = Expression<String?>("client")
            let purposeExpression = Expression<String?>("purpose")
            let applyInfosExpression = Expression<String?>("applyInfos")
            let createDateExpression = Expression<Date>("createDate")
            
            let insert = cardRecordTable.insert(vcUUIDExpression <- vcUUID,
                                                recordUUIDExpression <- UUID(),
                                                recordMessageExpression <- recordMessage,
                                                clientExpression <- client,
                                                purposeExpression <- purpose,
                                                applyInfosExpression <- applyInfos,
                                                createDateExpression <- createDate,
                                                cardRecordTypeExpression <- Int64(cardType.rawValue))
            try db.run(insert)
        } catch {
            
        }
    }
    
    func saveCredentialRecord(uid: UUID, walletId: UUID, vcId: UUID, text: String, authorizationUnit: String, authorizationPurpose: String, authorizationField: String, datetime: Date) {
        do {
            let uidExpression = Expression<UUID>("uid")
            let walletIdExpression = Expression<UUID>("walletId")
            let vcIdExpression = Expression<UUID>("vcId")
            let textExpression = Expression<String>("text")
            let authorizationUnitExpression = Expression<String>("authorizationUnit")
            let authorizationPurposeExpression = Expression<String>("authorizationPurpose")
            let authorizationFieldExpression = Expression<String>("authorizationField")
            let datetimeExpression = Expression<Date>("datetime")
            
            let insert = credentialRecordTable.insert(
                uidExpression <- uid,
                walletIdExpression <- walletId,
                vcIdExpression <- vcId,
                textExpression <- text,
                authorizationUnitExpression <- authorizationUnit,
                authorizationPurposeExpression <- authorizationPurpose,
                authorizationFieldExpression <- authorizationField,
                datetimeExpression <- datetime
            )
            
            try db.run(insert)
        } catch {
            
        }
    }
    
    func saveOperationRecord(uid: UUID, walletId: UUID, vcId: UUID, text: String, status: Int64, datetime: Date) {
        do {
            let uidExpression = Expression<UUID>("uid")
            let walletIdExpression = Expression<UUID>("walletId")
            let vcIdExpression = Expression<UUID>("vcId")
            let textExpression = Expression<String>("text")
            let statusExpression = Expression<Int64>("status")
            let datetimeExpression = Expression<Date>("datetime")
            
            let insert = operationRecordTable.insert(
                uidExpression <- uid,
                walletIdExpression <- walletId,
                vcIdExpression <- vcId,
                textExpression <- text,
                statusExpression <- status,
                datetimeExpression <- datetime
            )
            
            try db.run(insert)
        } catch {
        }
    }
    
    func savePresentationRecord(uid: UUID, walletId: UUID, text: String, vcIds: String, vcNames: String, authorizationUnit: String, authorizationPurpose: String, authorizationFields: String, datetime: Date) {
        do {
            let uidExpression = Expression<UUID>("uid")
            let walletIdExpression = Expression<UUID>("walletId")
            let textExpression = Expression<String>("text")
            let vcIdsExpression = Expression<String>("vcIds")
            let vcNamesExpression = Expression<String>("vcNames")
            let authorizationUnitExpression = Expression<String>("authorizationUnit")
            let authorizationPurposeExpression = Expression<String>("authorizationPurpose")
            let authorizationFieldsExpression = Expression<String>("authorizationFields")
            let datetimeExpression = Expression<Date>("datetime")
            
            let insert = presentationRecordTable.insert(
                uidExpression <- uid,
                walletIdExpression <- walletId,
                textExpression <- text,
                vcIdsExpression <- vcIds,
                vcNamesExpression <- vcNames,
                authorizationUnitExpression <- authorizationUnit,
                authorizationPurposeExpression <- authorizationPurpose,
                authorizationFieldsExpression <- authorizationFields,
                datetimeExpression <- datetime
            )
            try db.run(insert)
        } catch {
        }
    }
    
    private func fetchOldCardRecords() -> [CardRecord] {
        do {
            let recordUUIDExpression = Expression<UUID>("recordUUID")
            let vcUUIDExpression = Expression<UUID>("vcUUID")
            let recordMessageExpression = Expression<String>("recordMessage")
            let purposeExpression = Expression<String?>("purpose")
            let clientExpression = Expression<String?>("client")
            let applyInfosExpression = Expression<String?>("applyInfos")
            let createDateExpression = Expression<Date>("createDate")
            let cardRecordTypeExpression = Expression<Int64>("cardRecordType")
            
            let cardRecords: [CardRecord] = try db.prepare(cardRecordTable).map { row in
                return CardRecord(recordUUID: row[recordUUIDExpression],
                                  vcUUID: row[vcUUIDExpression],
                                  cardRecordType: row[cardRecordTypeExpression],
                                  recordMessage: row[recordMessageExpression],
                                  createDate: row[createDateExpression],
                                  client: row[clientExpression],
                                  purpose: row[purposeExpression],
                                  applyInfos: row[applyInfosExpression],
                                  vcNames: nil)
            }
            return cardRecords
        } catch {
            return []
        }
    }
    
    func deleteWallet(uuid: UUID) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let delete = walletTable.filter(uuidExpression == uuid).delete()
            try db.run(delete)
            
            let userCards = fetchVerifiableCredentials(userId: uuid, ascending: false)
            for userCard in userCards {
                guard let cardUUID = userCard.uuid else {
                    continue
                }
                let vcUUIDExpression = Expression<UUID>("vcUUID")
                let deleteCardRecord = cardRecordTable.filter(vcUUIDExpression == cardUUID).delete()
                try db.run(deleteCardRecord)
                
                let userIdExpression = Expression<UUID>("userId")
                let deleteCard = verifiableCredentialTable.filter(userIdExpression == uuid).delete()
                try db.run(deleteCard)
            }
        } catch {
        }
    }
    
    func columnExists(in tableName: String, columnName: String) throws -> Bool {
        let result = try db.prepare("PRAGMA table_info(\(tableName))")
        for row in result {
            if let name = row[1] as? String, name == columnName {
                return true
            }
        }
        return false
    }
    
    func saveVerifiableCredentialUpdateDate(vcUUID: UUID,
                                            remind: Int64,
                                            updateDate: Date) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let updateDateExpression = Expression<Date?>("updateDate")
            let remindExpression = Expression<Int64?>("remind")
            let update = verifiableCredentialTable.filter(uuidExpression == vcUUID)
                .update(updateDateExpression <- updateDate,
                        remindExpression <- remind)
            try db.run(update)
        } catch {
        }
    }
    
    func setAutoUpdate(walletUUID: UUID, isAutoUpdate: Bool) {
        do {
            let uuidExpression = Expression<UUID>("uuid")
            let autoUpdateExpression = Expression<Bool?>("autoUpdate")
            let update = walletTable.filter(uuidExpression == walletUUID)
                .update(autoUpdateExpression <- isAutoUpdate)
            try db.run(update)
        } catch {
        }
    }
    
    func getIsAutoUpdate(walletUUID: UUID) -> Bool {
        let uuidExpression = Expression<UUID>("uuid")
        let autoUpdateExpression = Expression<Bool?>("autoUpdate")
        let query = walletTable.filter(uuidExpression == walletUUID).select(autoUpdateExpression)
        if let row = try? db.pluck(query) {
            return row[autoUpdateExpression] ?? true
        } else {
            return true
        }
    }
    
    /// 儲存信任藍勾勾欄位
    /// - Parameters:
    ///   - vcUUID: 卡片`UUID`
    ///   - trustBadge: 是否有藍勾勾
    func saveVerifiableCredentialTrustBadge(vcUUID: UUID, trustBadge: Bool) {
        do {
            let uuidExpression = Expression<UUID>( "uuid")
            let trustBadgeExpression = Expression<Bool?>("trustBadge")
            let update = verifiableCredentialTable.filter(uuidExpression == vcUUID)
                .update(trustBadgeExpression <- trustBadge)
            try db.run(update)
        } catch {
        }
    }
}

// MARK: 卡片記錄相關
extension SQLiteManager {
    func fetchCardRecords(vcId: UUID) -> [CardRecord] {
        let credentialRecords = fetchCredentialRecords(vcId: vcId)
        let operationRecords = fetchOperationRecords(vcId: vcId)
        let cardRecords = credentialRecords + operationRecords
        return cardRecords.sorted { $0.createDate ?? Date() > $1.createDate ?? Date() }
    }
    
    func fetchAuthorizationRecords(
        ascending: Bool = false,
        limit: Int? = nil,
        offset: Int = 0,
        start: Date? = nil,
        end: Date? = nil
    ) -> [CardRecord] {
        do {
            let uid = Expression<UUID>("uid")
            let text = Expression<String>("text")
            let vcNames = Expression<String>("vcNames")
            let authorizationUnit = Expression<String>("authorizationUnit")
            let authorizationPurpose = Expression<String>("authorizationPurpose")
            let authorizationFields = Expression<String>("authorizationFields")
            let datetime = Expression<Date>("datetime")
            
            var query = presentationRecordTable as Table
            if let start { query = query.filter(datetime >= start) }
            if let end   { query = query.filter(datetime <= end) }
            
            query = query.order(ascending ? datetime.asc : datetime.desc)
            
            if let limit { query = query.limit(limit, offset: offset)}
            
            let rows: [CardRecord] = try db.prepare(query).map { row in
                return CardRecord(recordUUID: row[uid],
                                  vcUUID: nil,
                                  cardRecordType: 2,
                                  recordMessage: row[text],
                                  createDate: row[datetime],
                                  client: row[authorizationUnit],
                                  purpose: row[authorizationPurpose],
                                  applyInfos: row[authorizationFields],
                                  vcNames: row[vcNames])
            }
            return rows
        } catch {
            return []
        }
    }
    
    func fetchChangeRecords(
        walletId: UUID,
        ascending: Bool = false,
        limit: Int? = nil,
        offset: Int = 0,
        start: Date? = nil,
        end: Date? = nil
    ) -> [CardRecord] {
        do {
            let uidExpression = Expression<UUID>("uid")
            let walletIdExpression = Expression<UUID>("walletId")
            let vcIdExpression = Expression<UUID>("vcId")
            let textExpression = Expression<String>("text")
            let statusExpression = Expression<Int64>("status")
            let datetimeExpression = Expression<Date>("datetime")
            
            var query = operationRecordTable.filter(walletIdExpression == walletId)
            
            if let start {
                query = query.filter(datetimeExpression >= start)
            }
            if let end {
                query = query.filter(datetimeExpression <= end)
            }
            
            query = query.order(ascending ? datetimeExpression.asc : datetimeExpression.desc)
            
            if let limit {
                query = query.limit(limit, offset: offset)
            }
            
            let rows: [CardRecord] = try db.prepare(query).map { row in
                return CardRecord(recordUUID: row[uidExpression],
                                  vcUUID: row[vcIdExpression],
                                  cardRecordType: row[statusExpression],
                                  recordMessage: row[textExpression],
                                  createDate: row[datetimeExpression],
                                  client: nil,
                                  purpose: nil,
                                  applyInfos: nil,
                                  vcNames: nil)
            }
            return rows
        } catch {
            return []
        }
    }
    
    private func fetchCredentialRecords(vcId: UUID) -> [CardRecord] {
        do {
            let uidExpression = Expression<UUID>("uid")
            let vcIdExpression = Expression<UUID>("vcId")
            let textExpression = Expression<String>("text")
            let authorizationUnitExpression = Expression<String>("authorizationUnit")
            let authorizationPurposeExpression = Expression<String>("authorizationPurpose")
            let authorizationFieldExpression = Expression<String>("authorizationField")
            let datetimeExpression = Expression<Date>("datetime")
            
            let credentialRecords: [CardRecord] = try db.prepare(credentialRecordTable.filter(vcIdExpression == vcId)).map { row in
                return CardRecord(recordUUID: row[uidExpression],
                                  vcUUID: row[vcIdExpression],
                                  cardRecordType: 2,
                                  recordMessage: row[textExpression],
                                  createDate: row[datetimeExpression],
                                  client: row[authorizationUnitExpression],
                                  purpose: row[authorizationPurposeExpression],
                                  applyInfos: row[authorizationFieldExpression],
                                  vcNames: nil)
            }
            return credentialRecords
        } catch {
        }
        return []
    }
    
    private func fetchOperationRecords(vcId: UUID) -> [CardRecord] {
        do {
            let vcIdExpression = Expression<UUID>("vcId")
            
            let operationRecords: [CardRecord] = try db.prepare(operationRecordTable.filter(vcIdExpression == vcId)).map { row in
                return try row.decode()
            }
            return operationRecords
        } catch {
        }
        return []
    }
}

// MARK: 我的最愛相關處理
extension SQLiteManager {
    /// 新增我的最愛清單
    /// - Parameters:
    ///   - walletId: 當前皮夾
    ///   - favoriteCertificateId: 我的最愛UUID
    ///   - vpUid: 統編_模板代碼
    ///   - name: VP情境名稱
    ///   - verifierModuleUrl: 驗證端模組URL
    ///   - logoUrl: 組織 Logo URL
    ///   - creatDate: 建立時間
    ///   - updateDate: 更新時間
    func saveFavoriteCertificate(walletId: String,
                                 favoriteCertificateId: UUID,
                                 vpItem: VPItems,
                                 creatDate: Date,
                                 updateDate: Date) -> AnyPublisher<Void, Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let favoriteCertificateIdExpression = Expression<UUID>("favoriteCertificateUUID")
                    let vpUidExpression = Expression<String>("vpUid")
                    let nameExpression = Expression<String>("name")
                    let verifierModuleUrlExpression = Expression<String>("verifierModuleUrl")
                    let logoUrlExpression = Expression<String>("logoUrl")
                    let createDateExpression = Expression<Date>("creatDate")
                    let updateDateExpression = Expression<Date>("updateDate")
                    let insert = favoriteCertificateTable.insert(walletIdExpression <- walletId,
                                                                 favoriteCertificateIdExpression <- favoriteCertificateId,
                                                                 vpUidExpression <- vpItem.vpUid ?? "",
                                                                 nameExpression <- vpItem.name ?? "",
                                                                 verifierModuleUrlExpression <- vpItem.verifierModuleUrl ?? "",
                                                                 logoUrlExpression <- vpItem.logoUrl ?? "",
                                                                 createDateExpression <- creatDate,
                                                                 updateDateExpression <- updateDate)
                    try db.run(insert)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
        
        
        
    }
    
    /// 修改我的最愛清單
    /// - Parameters:
    ///   - favoriteCertificateId: 我的最愛UUID
    ///   - vpUid: 統編_模板代碼
    ///   - name: VP情境名稱
    ///   - verifierModuleUrl: 驗證端模組URL
    ///   - logoUrl: 組織 Logo URL
    ///   - creatDate: 建立時間
    ///   - updateDate: 更新時間
    func updateFavorite(favoriteCertificateId: UUID,
                        vpItem: VPItems,
                        update: Date) throws {
        let favoriteCertificateIdExpression = Expression<UUID>("favoriteCertificateUUID")
        let vpUidExpression = Expression<String>("vpUid")
        let nameExpression = Expression<String>("name")
        let verifierModuleUrlExpression = Expression<String>("verifierModuleUrl")
        let logoUrlExpression = Expression<String>("logoUrl")
        let updateDateExpression = Expression<Date>("updateDate")
        let update = favoriteCertificateTable.filter(favoriteCertificateIdExpression == favoriteCertificateId).update(vpUidExpression <- vpItem.vpUid ?? "",
                                                                                                                      nameExpression <- vpItem.name ?? "",
                                                                                                                      verifierModuleUrlExpression <- vpItem.verifierModuleUrl ?? "",
                                                                                                                      logoUrlExpression <- vpItem.logoUrl ?? "",
                                                                                                                      updateDateExpression <- update)
        try db.run(update)
    }
    
    
    /// 刪除我的最愛清單
    /// - Parameter favoriteCertificateId: 我的最愛UUID
    func deleteFavoriteCertificate(favoriteCertificateId: UUID) -> AnyPublisher<Void, Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let favoriteCertificateIdExpression = Expression<UUID>("favoriteCertificateUUID")
                    let delete = favoriteCertificateTable.filter(favoriteCertificateIdExpression == favoriteCertificateId).delete()
                    try db.run(delete)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    /// 獲得我的最愛
    func getFavoriteCertificates(walletId: String) -> AnyPublisher<[MyFavoriteList]?, Never> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let favoriteList: [MyFavoriteList] = try db.prepare(favoriteCertificateTable.filter(walletIdExpression == walletId)).map { row in
                        return try row.decode()
                    }
                    promise(.success(favoriteList))
                } catch {
                    promise(.success(nil))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
}

// MARK: 搜尋憑證相關處理
extension SQLiteManager {
    
    
    
    
    /// 新增搜尋憑證紀錄
    /// - Parameters:
    ///   - walletId: 皮夾ID
    ///   - searchCertificateLogId: 當筆UUID
    ///   - searchCertificateLogName: 當筆名稱
    ///   - creatDate: 新增時間
    ///   - updateDate: 更新時間
    func saveSearchCertificatesLog(walletId: String,
                                   searchCertificateLogId: UUID,
                                   searchCertificateLogName: String,
                                   creatDate: Date,
                                   updateDate: Date) -> AnyPublisher<Void, Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let searchCertificateLogUUIDExpression = Expression<UUID>("searchCertificatesLogUUID")
                    let nameExpression = Expression<String>("name")
                    let createDataExpression = Expression<Date>("creatDate")
                    let updateDateExpression = Expression<Date>("updateDate")
                    let insert = searchCertificatesTable.insert(
                        walletIdExpression <- walletId,
                        searchCertificateLogUUIDExpression <- searchCertificateLogId,
                        nameExpression <- searchCertificateLogName,
                        createDataExpression <- creatDate,
                        updateDateExpression <- updateDate
                    )
                    try db.run(insert)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    
    /// 刪除搜尋紀錄
    /// - Parameter searchCertificateLogUUID: 當筆UUID
    func deleteSearchCertificateLog(searchCertificateLogUUID: UUID) -> AnyPublisher<Void, Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let searchCertificateLogUUIDExpression = Expression<UUID>("searchCertificatesLogUUID")
                    let delete = searchCertificatesTable.filter(searchCertificateLogUUIDExpression == searchCertificateLogUUID).delete()
                    try db.run(delete)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    
    /// 獲得搜尋紀錄
    /// - Parameter walletId: 皮夾ID
    func getSearchCertificatesLog(walletId: String) -> AnyPublisher<[SearchCertificateLog]?, Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let searchCertificatesLogList: [SearchCertificateLog] = try db.prepare(searchCertificatesTable.filter(walletIdExpression == walletId).limit(5)).map { row in
                        return try row.decode()
                    }
                    promise(.success(searchCertificatesLogList))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    /// 新增搜尋VC紀錄
    /// - Parameters:
    ///   - walletId: 皮夾ID
    ///   - searchVCLogId: 當筆UUID
    ///   - searchVCLogName: 當筆名稱
    ///   - creatDate: 新增時間
    ///   - updateDate: 更新時間
    func saveSearchVCLog(walletId: String, searchVCLogId: UUID, searchVCLogName: String, creatDate: Date, updateDate: Date) -> AnyPublisher<Void, any Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let searchVCLogUUIDExpression = Expression<UUID>("searchVCsLogUUID")
                    let nameExpression = Expression<String>("name")
                    let createDataExpression = Expression<Date>("creatDate")
                    let updateDateExpression = Expression<Date>("updateDate")
                    let insert = searchVCsTable.insert(
                        walletIdExpression <- walletId,
                        searchVCLogUUIDExpression <- searchVCLogId,
                        nameExpression <- searchVCLogName,
                        createDataExpression <- creatDate,
                        updateDateExpression <- updateDate
                    )
                    try db.run(insert)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    /// 刪除VC搜尋紀錄
    /// - Parameter searchVCLogUUID: 當筆UUID
    func deleteSearchVCLog(searchVCLogUUID: UUID) -> AnyPublisher<Void, any Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let searchVCLogUUIDExpression = Expression<UUID>("searchVCsLogUUID")
                    let delete = searchVCsTable.filter(searchVCLogUUIDExpression == searchVCLogUUID).delete()
                    try db.run(delete)
                    promise(.success(()))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
    
    /// 獲得搜尋紀錄
    /// - Parameter walletId: 皮夾ID
    func getSearchVCsLog(walletId: String) -> AnyPublisher<[SearchVCLog]?, any Error> {
        return Deferred {
            Future { [weak self] promise in
                guard let self = self else { return }
                do {
                    let walletIdExpression = Expression<String>("walletId")
                    let searchVCsLogList: [SearchVCLog] = try db.prepare(searchVCsTable.filter(walletIdExpression == walletId).limit(5)).map { row in
                        return try row.decode()
                    }
                    promise(.success(searchVCsLogList))
                } catch {
                    promise(.failure(error))
                }
            }
        }
        .subscribe(on: DispatchQueue.global(qos: .userInitiated))
        .receive(on: DispatchQueue.main)
        .eraseToAnyPublisher()
    }
}

// MARK: Key相關處理
extension SQLiteManager {
    
    // 取得或產生 SQLCipher Key
    func getOrCreateEncryptionKey() -> String {
        // 1. 從 Keychain 嘗試讀取
        if let existingKey = loadKeyFromKeychain() {
            return existingKey
        }
        
        // 2. 產生隨機金鑰
        let newKey = generateRandomHexKey()
        
        // 3. 儲存到 Keychain
        saveKeyToKeychain(hexKey: newKey)
        
        return newKey
    }
    
    // 產生隨機 32-byte（64 hex 字元）金鑰
    func generateRandomHexKey() -> String {
        var keyData = Data(count: 32)
        let result = keyData.withUnsafeMutableBytes {
            SecRandomCopyBytes(kSecRandomDefault, 32, $0.baseAddress!)
        }
        
        guard result == errSecSuccess else {
            fatalError("❌ 金鑰產生失敗")
        }
        
        return "x'" + keyData.map { String(format: "%02hhx", $0) }.joined() + "'"
    }
    
    // 儲存到 Keychain
    func saveKeyToKeychain(hexKey: String) {
        guard let data = hexKey.data(using: .utf8) else { return }
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: keychainKey,
            kSecValueData as String: data,
            kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlockedThisDeviceOnly
        ]
        
        SecItemDelete(query as CFDictionary) // 清除舊的
        SecItemAdd(query as CFDictionary, nil)
        
    }
    
    // 從 Keychain 讀取
    func loadKeyFromKeychain() -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: keychainKey,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        if status == errSecSuccess, let data = result as? Data {
            return String(data: data, encoding: .utf8)
        }
        
        return nil
    }
    
}
