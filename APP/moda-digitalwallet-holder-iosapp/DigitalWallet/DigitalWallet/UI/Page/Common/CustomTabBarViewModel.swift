//
//  CustomTabBarViewModel.swift
//  DigitalWallet
//

import Foundation
import Combine
import AVFoundation

protocol CallTabbarProtocol {
    /// 顯示ScanVC
    func showScanVC()
    
    /// 重新產出VP05
    func regenerateVP05()
}

class CustomTabBarViewModel: BaseViewModel, CallTabbarProtocol {
    
    let repository: UserRepositoryProtocol
    let parseLinkManager: ParseLinkProtocol
    
    let isLoadingSubject = PassthroughSubject<Bool, Never>()
    let parseQRCodeErrorSubject = PassthroughSubject<String, Never>()
    let verifiableCredentialErrorSubject = PassthroughSubject<(String, Bool), Never>()
    let verifiableCredentialSuccessSubject = PassthroughSubject<(ApplyVCDataResponse, String, Data?, String, VerifiableCredentailResult?, Bool), Never>()
    let verifiablePresentaionSuccessSubject = PassthroughSubject<(VerifiableData, ParseVPDataResponse, [DwModa201iCustomField], VerifiablePresentationResult), Never>()
    let showScanVCPassthroughSubject = PassthroughSubject<Void, Never>()
    let showAppSettingAlertPassthroughSubject = PassthroughSubject<Void, Never>()
    let regeneratePassthroughSubject = PassthroughSubject<Void, Never>()
    let showWebViewPassthroughSubject = PassthroughSubject<(title: String, url: String, isInside: Bool), Never>()
    
    private let parsePassthroughSubject = PassthroughSubject<(ParseLinkResult, CrossAppStatus), Never>()
    private var cancelSet = Set<AnyCancellable>()
    
    init(respository: UserRepositoryProtocol,
         parseLinkManager: ParseLinkProtocol) {
        self.repository = respository
        self.parseLinkManager = parseLinkManager
        super.init()
        initBinding()
    }
    
    private func initBinding() {
        parseLinkManager.crossAppCurrentValueSubject
            .filter({ $0 != .idle })
            .receive(on: DispatchQueue.main)
            .sink { [weak self] result in
                guard let self else { return }
                switch result {
                case .idle:
                    break
                case .crossAppActive(url: let url, isShowFullScreen: let isShowFullScreen):
                    handleURL(url: url, isShowFullScreen: isShowFullScreen)
                    break
                }
                parseLinkManager.crossAppCurrentValueSubject.send(.idle)
            }.store(in: &cancelSet)
    }
    
    private func handleURL(url: URL, isShowFullScreen: Bool) {
        let result = parseLinkManager.parseLink(url)
        switch result {
        case .parseError:
            break
        case .parseVC(let qrcode):
            getVerifiableCredential(qrcode: qrcode,
                                    otp: "\(Int.random(in: 0...9))\(Int.random(in: 0...9))",
                                    isShowFullScreen: isShowFullScreen)
        case .parseVP(let qrcode):
            getVerifiablePresentation(qrcode: qrcode,
                                      verifiablePresentationResult: isShowFullScreen ? .showQRCode : .showResult)
        case .staticVC(let vcUID):
            getStaticVerifiableCredential(vcUID: vcUID,
                                          isShowFullScreen: isShowFullScreen)
        case .staticVP(let vpUID):
            getStaticVerifiablePresentation(vpUID: vpUID,
                                            isShowFullScreen: isShowFullScreen)
        }
    }
    
    private func getVerifiableCredential(qrcode: String, otp: String, isShowFullScreen: Bool) {
        Task { [weak self] in
            guard let self else { return }

            await MainActor.run { self.isLoadingSubject.send(true) }
            do {
                let verifiableCredential = try await repository.getVerifiableCredential(qrcode: qrcode, otp: otp)
                
                let bgURI = verifiableCredential.credentialDefinition.display.first?.backgroundImage?.uri ?? ""

                // 併發子工作用的結果容器
                var verifyResult: VerifiableCredentailResult?
                var decodeVCData: DecodeVCDataResponse?
                var cardImageData: Data?

                // 併發：verify、decode、載圖
                enum TaskOutput {
                    case verify(VerifiableCredentailResult?)
                    case decode(DecodeVCDataResponse?)
                    case downloadVCList
                    case image(Data?)
                }

                await withTaskGroup(of: TaskOutput.self) { group in
                    group.addTask {
                        // 其它工作一律 try? -> 失敗為 nil
                        let res = try? await self.repository.getVerifyVCResult(applyVCData: verifiableCredential)
                        return .verify(res)
                    }
                    group.addTask {
                        let dec = await self.repository.getDecodeVCData(applyVCData: verifiableCredential)
                        return .decode(dec)
                    }
                    group.addTask {
                        await self.repository.updateVCList(credential: verifiableCredential.credential)
                        return .downloadVCList
                    }
                    
                    if !bgURI.isEmpty {
                        group.addTask {
                            let uiimg = await FunctionUtil.shared.loadImageAsync(from: bgURI)
                            return .image(uiimg?.pngData())
                        }
                    }

                    // 收集結果
                    for await out in group {
                        switch out {
                        case .verify(let v): verifyResult = v
                        case .decode(let d): decodeVCData = d
                        case .downloadVCList: break
                        case .image(let data): cardImageData = data
                        }
                    }
                }

                // 從 decode 取欄位
                let iss = decodeVCData?.iss ?? ""
                let displayName = decodeVCData?.vc?.credentialSubject?.field?.data.first?.first?.value ?? ""
                let exp = decodeVCData?.exp ?? 0

                // issList
                let issList = UserDefaultManager.shared.getObject(
                    key: .IssList601i,
                    type: [DownloadIssListResponse].self
                ) ?? []
                let orgName = repository.getOrgName(iss: iss, issList: issList)

                // 存 DB
                repository.saveVerifiableCredential(
                    verifiableCredential: verifiableCredential,
                    state: verifyResult?.status ?? .unverified,
                    iss: iss,
                    orgName: orgName,
                    cardImage: cardImageData,
                    remind: VerifyCardRemind.effective.rawValue,
                    exp: exp,
                    displayName: displayName,
                    trustBadge: verifyResult?.trustBadge ?? false
                )
                
                self.isLoadingSubject.send(false)
                self.verifiableCredentialSuccessSubject.send(
                    (verifiableCredential, orgName, cardImageData, displayName, verifyResult, isShowFullScreen)
                )
            } catch {
                await MainActor.run {
                    if let didError = error as? DIDError {
                        self.isLoadingSubject.send(false)
                        self.verifiableCredentialErrorSubject.send((didError.message, isShowFullScreen))
                    }
                }
            }
        }
    }
    
    private func getVerifiablePresentation(qrcode: String,
                                           fields: [DwModa201iCustomField] = [],
                                           verifiablePresentationResult: VerifiablePresentationResult) {
        Task {
            isLoadingSubject.send(true)
            do {
                let parseVPData = try await repository.getParseVPData(qrcode: qrcode)
                let verifyResp = await repository.getMatchVCForPresentaion(parseVPData: parseVPData)
                isLoadingSubject.send(false)
                if verifyResp.groupList.isEmpty {
                    parseQRCodeErrorSubject.send(NSLocalizedString("NoVerifiableCredential", comment: ""))
                } else {
                    verifiablePresentaionSuccessSubject.send((verifyResp, parseVPData, fields, verifiablePresentationResult))
                }
            } catch {
                isLoadingSubject.send(false)
                if let error = error as? DIDError {
                    parseQRCodeErrorSubject.send(error.message)
                }else{
                    parseQRCodeErrorSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            }
        }
    }
    
    private func getStaticVerifiableCredential(vcUID: String, isShowFullScreen: Bool) {
        Task {
            isLoadingSubject.send(true)
            do {
                let response = try await repository.getStaticVerifiableCredential(bodyModel: DwModa201iRequest(mode: "vc"), uid: vcUID)
                guard let type = response.type,
                      let title = response.name,
                      let issuerServiceUrl = response.issuerServiceUrl else {
                    return
                }
                let isInside = type == 1 ? false : true
                isLoadingSubject.send(false)
                showWebViewPassthroughSubject.send((String(format: NSLocalizedString("addCertificateOpenWebTitle", comment: "%@線上申請"), title),
                                                    issuerServiceUrl, isInside))
            } catch {
                isLoadingSubject.send(false)
                
                if let error = error as? DIDError {
                    switch error {
                        case .responseError(_, let message):
                        verifiableCredentialErrorSubject.send((message ?? NSLocalizedString("AppError", comment: ""), isShowFullScreen))
                        default:
                        verifiableCredentialErrorSubject.send((NSLocalizedString("AppError", comment: ""), isShowFullScreen))
                            break
                    }
                }else{
                    verifiableCredentialErrorSubject.send((NSLocalizedString("AppError", comment: ""), isShowFullScreen))
                }
            }
        }
    }
    
    private func getStaticVerifiablePresentation(vpUID: String, isShowFullScreen: Bool) {
        Task {
            isLoadingSubject.send(true)
            do {
                let dwModa201iResponse = try await repository.getStaticVerifiableCredential(bodyModel: DwModa201iRequest(mode: "vp"), uid: vpUID)
                guard let verifierServiceUrl = dwModa201iResponse.verifierServiceUrl else {
                    return
                }
                let response = try await repository.getVerifiablePresentationDeepLink(bodyModel: DwVerifierMock101iRequest(vpUid: vpUID), verifierServiceUrl: verifierServiceUrl)
                guard let deepLink = response.deepLink else { return }
                isLoadingSubject.send(false)
                getVerifiablePresentation(qrcode: deepLink, fields: dwModa201iResponse.custom?.customData ?? [], verifiablePresentationResult: .showResult)
            } catch {
                isLoadingSubject.send(false)
                
                if let error = error as? DIDError {
                    switch error {
                        case .responseError(_, let message):
                            parseQRCodeErrorSubject.send(message ?? NSLocalizedString("AppError", comment: ""))
                        default:
                            parseQRCodeErrorSubject.send(NSLocalizedString("AppError", comment: ""))
                            break
                    }
                }else{
                    parseQRCodeErrorSubject.send(NSLocalizedString("AppError", comment: ""))
                }
            }
        }
    }
    
    func checkCameraPermissions() {
        let status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .authorized {
            showScanVCPassthroughSubject.send()
        } else if status == .notDetermined {
            requestCameraPermissions()
        } else {
            showAppSettingAlertPassthroughSubject.send()
        }
    }
    
    private func requestCameraPermissions() {
        Task {
            if await AVCaptureDevice.requestAccess(for: .video) {
                showScanVCPassthroughSubject.send()
            }
        }
    }
}

// MARK: CallTabbarProtocol
extension CustomTabBarViewModel {
    func showScanVC() {
        checkCameraPermissions()
    }
    
    func regenerateVP05() {
        regeneratePassthroughSubject.send()
    }
}
