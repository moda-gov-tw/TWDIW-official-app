//
//  SecureExportError.swift
//  DigitalWallet
//

enum SecureURLValidationError: Error {
    case invalidFilename
    case parentNotExists
    case parentNotDirectory
    case parentIsSymlink
    case outsideSandbox
    case existingIsSymlink
}
