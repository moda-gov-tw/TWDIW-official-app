//
//  ImageLoadProtocol.swift
//  DigitalWallet
//

import Foundation
import UIKit

protocol ImageLoadProtocol {
    func loadImage(from url: URL) async throws -> UIImage
}
