//
//  FunctionUtil.swift
//  DigitalWallet
//

import UIKit

class FunctionUtil: NSObject {
    static let shared = FunctionUtil()
    
    let YYMMDDHHMMSS:String = "yyyy-MM-dd HH:mm:ss"
    let yyyyMMddHHMMSS:String = "yyyy/MM/dd HH:mm:ss"
    let YYMMDD:String = "yyyy/MM/dd"
    
    /**url取得image圖片*/
    func loadImageAsync(from url: String) async -> UIImage? {
        guard let imageUrl = URL(string: url) else { return nil }

        do {
            let configuration = URLSessionConfiguration.default
            configuration.tlsMinimumSupportedProtocolVersion = .TLSv12
            let urlSession = URLSession(configuration: configuration, delegate: self, delegateQueue: nil)
            let (data, _) = try await urlSession.data(from: imageUrl)
            
            guard let base64String = String(data: data, encoding: .utf8) else {
                return nil
            }
            
            // 解析 Base64
            if let image = decodeBase64Image(base64String) {
                return image
            } else {
                return nil
            }
        } catch {
            return nil
        }
    }
    
    /**日期轉字串*/
    func dateFormat(format: String, dateTime: Date) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = format
        let date = dateFormatter.string(from: dateTime)
        
        return date
    }
    
    /// 解析照片QRCode內容
    /// - Parameter image: QRCode照片
    /// - Returns: 解析後內容
    func detectQRCode(in image: UIImage) -> Array<String> {
        guard let ciImage = CIImage(image: image) else { return [] }

        let detector = CIDetector(
            ofType: CIDetectorTypeQRCode,
            context: nil,
            options: [CIDetectorAccuracy: CIDetectorAccuracyHigh]
        )

        let features = detector?.features(in: ciImage) as? [CIQRCodeFeature] ?? []
        let codes = features.compactMap { $0.messageString }

        return codes
    }
    
    /**登出*/
    func logout(reLogin: Bool = false){
        let loginRepository = AppDelegate.shared.container.resolve(LoginRepository.self)!
        let loginViewModel = LoginViewModel(loginRepository: loginRepository, loginAction: reLogin ? .reLogin : .normal)
        let loginVC = LoginViewController(viewModel: loginViewModel)
        let navi = CustomNavigationController(rootViewController: loginVC)
        
        UIApplication.shared.windows.first?.rootViewController = navi
        UIApplication.shared.windows.first?.makeKeyAndVisible()
    }
    
    /**取得當前VC*/
    func getTopViewController(base: UIViewController? = {
        // 取得目前啟用中的 UIWindowScene
        let scene = UIApplication.shared.connectedScenes
            .filter { $0.activationState == .foregroundActive }
            .first(where: { $0 is UIWindowScene }) as? UIWindowScene

        // 取得該 scene 的 key window
        return scene?.windows
            .first(where: { $0.isKeyWindow })?
            .rootViewController
    }()) -> UIViewController? {
        
        if let nav = base as? UINavigationController {
            return getTopViewController(base: nav.visibleViewController)
        }
        
        if let tab = base as? UITabBarController {
            if let selected = tab.selectedViewController {
                return getTopViewController(base: selected)
            }
        }
        
        if let presented = base?.presentedViewController {
            return getTopViewController(base: presented)
        }
        
        return base
    }
}

extension FunctionUtil{
    /**Base64轉UIImage*/
    private func decodeBase64Image(_ base64String: String) -> UIImage? {
        guard let range = base64String.range(of: "base64,") else { return nil }
        
        let base64Data = String(base64String[range.upperBound...]) // 取得純 Base64 字串
        
        /*base64解碼成Data*/
        guard let imageData = Data(base64Encoded: base64Data),
              let originImage = UIImage(data: imageData) else {
            return nil
        }
        
        let picWidth: CGFloat = originImage.size.width//取得原圖寬度
        let picHeight: CGFloat = originImage.size.height//取得原圖高度
        let maxWidth: CGFloat = 500//新圖寬度(2倍寬度為圖片實際的寬度pixels值)
        
        /*寬度大於限制，進行壓縮*/
        if picWidth > maxWidth{
            let newHeight: CGFloat = (picHeight/picWidth) * maxWidth//等比例縮放圖片
            
            /*縮小尺寸*/
            let resized = resizeImage(originImage, targetSize: CGSize.init(width: maxWidth, height: newHeight))
            
            /*壓縮品質，compressionQuality建議設在0.4~0.8之間(低於0.3畫質通常會明顯變差)*/
            guard let compressedData = resized.jpegData(compressionQuality: 0.6) else {
                return nil
            }
            
            /*轉回base64*/
            let compressionBase64: String = compressedData.base64EncodedString()
            
            guard let imageData = Data(base64Encoded: compressionBase64),
                  let newImage = UIImage(data: imageData) else {
                return nil
            }
            
            return newImage
        }
        /*使用原始圖片*/
        else{
            return originImage
        }
    }
    
    private func resizeImage(_ image: UIImage, targetSize: CGSize) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: targetSize)
        
        return renderer.image { _ in
            image.draw(in: CGRect(origin: .zero, size: targetSize))
        }
    }
}

extension FunctionUtil: URLSessionDataDelegate {
    
    func urlSession(_ session: URLSession, dataTask: URLSessionDataTask, willCacheResponse proposedResponse: CachedURLResponse, completionHandler: @escaping (CachedURLResponse?) -> Void) {
        completionHandler(nil)
    }
}
