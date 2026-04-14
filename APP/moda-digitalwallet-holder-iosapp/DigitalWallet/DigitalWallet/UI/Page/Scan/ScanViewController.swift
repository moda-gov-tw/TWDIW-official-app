//
//  ScanViewController.swift
//  DigitalWallet
//

import UIKit
import AVFoundation
import Combine

class ScanViewController: BaseViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate, UIAdaptivePresentationControllerDelegate{
    static let crossAppSubject = PassthroughSubject<Void, Never>()
    
    @IBOutlet weak var maskView: UIView!
    @IBOutlet weak var scannerImageView: UIImageView!
    @IBOutlet weak var cameraView: UIView!
    @IBOutlet weak var titleLabel: UILabel!
    
    private var captureSession: AVCaptureSession!
    private var previewLayer: AVCaptureVideoPreviewLayer!
    private var captureDevice: AVCaptureDevice!
    private var cancelSet = Set<AnyCancellable>()
    private let viewModel: ScanViewModel
    
    init(viewModel: ScanViewModel) {
        self.viewModel = viewModel
        super.init(nibName: "ScanViewController", bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        initView()
        initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        viewModel.isViewControllerWillDisappeared = false
        startCapture()
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()

    }
    
    // 當視圖消失時停止相機擷取
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        viewModel.isViewControllerWillDisappeared = true
        stopCapture()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
 
        let maskLayer = CAShapeLayer()
        // 全部區域
        let fullPath = UIBezierPath(rect: maskView.bounds)

        let path = UIBezierPath(roundedRect: scannerImageView.frame, cornerRadius: 30.0)
        fullPath.append(path)

        // 使用 even-odd 模式反轉遮罩
        maskLayer.path = fullPath.cgPath
        maskLayer.fillRule = .evenOdd

        // 套用遮罩
        maskView.layer.mask = maskLayer
    }
    
    private func initView() {
        isHideNavigationBar = true
    }
    
    private func initBinding() {
        viewModel.parseQRCodeErrorSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] message in
                guard let self = self else {
                    return
                }
                
                self.showAlert(content: message,complete: {
                    self.viewModel.isViewControllerWillDisappeared = false
                    self.startCapture()
                })
            }.store(in: &cancelSet)
        
        viewModel.openURLPassthroughtSubject
            .receive(on: DispatchQueue.main)
            .sink { [weak self] url in
                guard let self else { return }
                if let url = URL(string: url) {
                    UIApplication.shared.open(url)
                }
                startCapture()
            }.store(in: &cancelSet)
    }
    
    private func setupCature() {
        captureSession = AVCaptureSession()
        captureSession.beginConfiguration()
        captureDevice = AVCaptureDevice.default(for: .video)
        
        guard let videoDeviceInput = try? AVCaptureDeviceInput(device: captureDevice),
              captureSession.canAddInput(videoDeviceInput) else {
            return
        }
        
        captureSession.addInput(videoDeviceInput)
        
        let metadataOutput = AVCaptureMetadataOutput()
        guard captureSession.canAddOutput(metadataOutput) else { return }
        captureSession.addOutput(metadataOutput)
        metadataOutput.setMetadataObjectsDelegate(self, queue: .main)
        metadataOutput.metadataObjectTypes = [.qr]
        captureSession.commitConfiguration()
    }
    
    private func setupMaskAndCameraPreview() {
        previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
        previewLayer.frame = UIScreen.main.bounds
        previewLayer.videoGravity = .resizeAspectFill
        
        cameraView.layer.addSublayer(previewLayer)
    }
    
    private func startCapture() {
        self.setupCature()
        self.setupMaskAndCameraPreview()
        
        DispatchQueue.global(qos: .background).async {
            self.captureSession.startRunning()
        }
    }
    
    private func stopCapture() {
        DispatchQueue.global(qos: .background).async {
            guard self.captureSession != nil else{
                return
            }
            
            if self.captureSession.isRunning {
                self.captureSession.stopRunning()
                self.captureSession = nil
            }
        }
    }
    @IBAction func dismissScan(_ sender: Any) {
        navigationController?.popViewController(animated: true)
    }
}

extension ScanViewController: AVCaptureMetadataOutputObjectsDelegate {
    /**當辨識到 QRCode 時呼叫此方法*/
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if viewModel.isViewControllerWillDisappeared {
            return
        }
        
        captureSession.stopRunning()
        
        if let metadataObject = metadataObjects.first {
            guard let readableObject = metadataObject as? AVMetadataMachineReadableCodeObject else { return }
            guard let stringValue = readableObject.stringValue else { return }
            
            // 振動回饋
            AudioServicesPlaySystemSound(SystemSoundID(kSystemSoundID_Vibrate))
            
            viewModel.parseQRCode(text: stringValue)
        }
    }
}
