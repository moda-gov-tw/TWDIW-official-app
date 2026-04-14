//
//  SetWalletNameViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine

class SetWalletNameViewModel {
    let walletNameSubject = PassthroughSubject<String, Never>()
    let walletNameErrorSubject = PassthroughSubject<Void, Never>()
    let walletNameIllegalSubject = PassthroughSubject<Void, Never>()
    let createWalletRepository: CreateWalletRepositoryProtocol
    let repository: UserRepositoryProtocol
    let hasSameWalletNameSubject = PassthroughSubject<Void, Never>()
    let updateWalletNameSubject = PassthroughSubject<UserRepositoryProtocol, Never>()
    let updateWalletNameResultSubject = PassthroughSubject<Bool, Never>()
    let isLoadingSubject = PassthroughSubject<Bool, Never>()
    var cancelSet = Set<AnyCancellable>()
    
    init(repository: UserRepositoryProtocol, createWalletRepository: CreateWalletRepositoryProtocol) {
        self.repository = repository
        self.createWalletRepository = createWalletRepository
        
        initBinding()
    }
    
    func initBinding(){
        createWalletRepository.isSameWalletNameSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] isSameName, name in
                guard let self else { return }
                
                if isSameName {
                    hasSameWalletNameSubject.send()
                } else {
                    updateNameData(walletName: name)
                }
            }.store(in: &cancelSet)
    }
    
    /**更新皮夾名稱*/
    func updateWalletName(walletName: String?) {
        // 1. 檢查名稱是否為空
        guard
            let walletName = walletName,
            !walletName.isEmpty
        else {
            walletNameErrorSubject.send()
            return
        }
        
        // 2. 檢查是否有錯誤內容
        if isEnglishChineseOrNumber(walletName) {
            createWalletRepository.setWalletName(walletName: walletName)
        } else {
            walletNameIllegalSubject.send()
        }
    }
    
    /**更新DB皮夾名稱*/
    private func updateNameData(walletName: String){
        Task{
            isLoadingSubject.send(true)
            
            do{
                let isSuccess = try repository.databaseManager.updateWalletName(userId: repository.wallet.uuid, name: walletName)
                isLoadingSubject.send(false)
                
                updateWalletNameResultSubject.send(isSuccess)
            }catch{
                isLoadingSubject.send(false)
            }
        }
    }
    
    private func isEnglishChineseOrNumber(_ string: String) -> Bool {
        // 正則表達式：僅允許英文、中文和數字
        let pattern = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$"
        
        // 使用 NSPredicate 檢查字串是否符合正則表達式
        let predicate = NSPredicate(format: "SELF MATCHES %@", pattern)
        return predicate.evaluate(with: string)
    }
}
