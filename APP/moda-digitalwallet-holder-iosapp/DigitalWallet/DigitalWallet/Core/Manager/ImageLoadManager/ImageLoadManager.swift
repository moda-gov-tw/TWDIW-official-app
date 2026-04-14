//
//  ImageLoadManager.swift
//  DigitalWallet
//

import Foundation
import UIKit

class ImageLoadManager: NSObject, ImageLoadProtocol {
    private let cache: ImageCache
    
    init(cache: ImageCache) {
        self.cache = cache
    }
    
    func loadImage(from url: URL) async throws -> UIImage {
        // 1. 先檢查快取
        if let cachedImage = cache.image(for: url) {
            return cachedImage
        }
        let configuration = URLSessionConfiguration.default
        configuration.tlsMinimumSupportedProtocolVersion = .TLSv12
        configuration.timeoutIntervalForRequest = 10
        let urlSession = URLSession(configuration: configuration, delegate: self, delegateQueue: nil)
        

        // 2. 沒有就下載
        let (data, response) = try await urlSession.data(from: url)

        guard let httpResponse = response as? HTTPURLResponse,
              (200...299).contains(httpResponse.statusCode),
              let imageString = String(data: data, encoding: .utf8),
              let base64String = imageString.components(separatedBy: ",").last,
              let imageData = Data(base64Encoded: base64String),
              let image = UIImage(data: imageData) else {
            throw URLError(.badServerResponse)
        }

        // 3. 儲存快取
        cache.insertImage(image, for: url)

        return image
    }
}

class ImageCache {
    private let cache: NSCache<NSURL, UIImage>
    
    init() {
        self.cache = NSCache<NSURL, UIImage>()
        let cacheLimit = ProcessInfo.processInfo.physicalMemory / 8
        cache.totalCostLimit = Int(cacheLimit)
    }
    
    func image(for url: URL) -> UIImage? {
        return cache.object(forKey: url as NSURL)
    }
    
    func insertImage(_ image:UIImage, for url: URL) {
        cache.setObject(image, forKey: url as NSURL)
    }
}

extension ImageLoadManager: URLSessionDataDelegate {
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, willCacheResponse proposedResponse: CachedURLResponse, completionHandler: @escaping (CachedURLResponse?) -> Void) {
        completionHandler(nil)
    }
}
