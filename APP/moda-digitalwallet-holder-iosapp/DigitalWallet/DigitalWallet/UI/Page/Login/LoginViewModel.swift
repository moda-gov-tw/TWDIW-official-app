//
//  SelectWalletViewModel.swift
//  DigitalWallet
//


import Foundation
import Combine
import LocalAuthentication

class LoginViewModel {
    
    enum LoginActionType: Equatable {
        case normal
        case createWallet
        case reLogin
    }
    
    let loginRepository: LoginRepositoryProtocol
    var loginAction: LoginActionType
    
    @Published var pickerItems: [CustomPickerItem] = []
    @Published var selectedItem: CustomPickerItem?
    let loginSuccesSubject = PassthroughSubject<MyWallet, Never>()
    let pinCodeLoginSubject = PassthroughSubject<MyWallet, Never>()
    let noPinCodeSubject = PassthroughSubject<Void, Never>()
    private var cancelSet = Set<AnyCancellable>()
    
    init(loginRepository: LoginRepositoryProtocol, loginAction: LoginActionType) {
        self.loginRepository = loginRepository
        self.loginAction = loginAction
        self.initBinding()
    }
    
    private func initBinding() {
        loginRepository.loginSuccessSubject
            .delay(for: .seconds(1), scheduler: RunLoop.main) /// 為了生物辨識的delay
            .receive(on: DispatchQueue.main)
            .sink { [weak self] wallet in
                guard let self = self else { return }
                loginSuccesSubject.send(wallet)
            }.store(in: &cancelSet)
    }
    
    /**登入皮夾*/
    func doLogin() {
        guard let walletName = selectedItem?.name,
              let wallet = loginRepository.getWallet(name: walletName)
        else {
            return
        }
        
        if LAContext().canEvaluatePolicy(.deviceOwnerAuthentication, error: nil) {
            loginRepository.doLoginWithBiometric(wallet: wallet)
        }
        else {
            /*該皮夾是用生物辨識註冊，沒有pinCode*/
            if wallet.pinCode.isEmpty {
                noPinCodeSubject.send()
            }else{
                pinCodeLoginSubject.send(wallet)
            }
        }
    }
    
    /**取得皮夾*/
    func getWallets() {
        let wallets: Array<MyWallet> = loginRepository.getWallets()
        
        pickerItems = wallets.compactMap({ wallet in
            return CustomPickerItem(name: wallet.name, tag: nil)
        })
    }
}
