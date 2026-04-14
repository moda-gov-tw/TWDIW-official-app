import Foundation

public struct PrintPro {
    #if DEBUG
    public static var isEnabled: Bool = false
    #else
    public static let isEnabled: Bool = false
    #endif

    public static func print(_ items: Any..., separator: String = " ", terminator: String = "\n") {
        #if DEBUG
        guard isEnabled else { return }
        Swift.print(items.map { "\($0)" }.joined(separator: separator), terminator: terminator)
        #endif
    }
}
