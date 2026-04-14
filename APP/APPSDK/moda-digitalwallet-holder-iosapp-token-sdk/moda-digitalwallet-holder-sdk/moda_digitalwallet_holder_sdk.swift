import Foundation
import Security

public class ModaDigitalWalletHolderSDK {
    
    public init() {
        let fullVersion = "moda-digitalwallet-holder-sdk version:\(ModaDigitalWalletHolderSDK.version) (\(ModaDigitalWalletHolderSDK.build))"
        PrintPro.print(fullVersion)
    }
    
    //MARK: - Version
    private final class BundleToken {}
    private static var bundle: Bundle {
        return Bundle(for: BundleToken.self)
    }
    
    public static var version: String {
        return bundle.infoDictionary?["CFBundleShortVersionString"] as? String ?? ""
    }
    
    public static var build: String {
        return bundle.infoDictionary?["CFBundleVersion"] as? String ?? ""
    }
    
    public static var fullVersion: String {
        return "\(version)(\(build))"
    }
}
