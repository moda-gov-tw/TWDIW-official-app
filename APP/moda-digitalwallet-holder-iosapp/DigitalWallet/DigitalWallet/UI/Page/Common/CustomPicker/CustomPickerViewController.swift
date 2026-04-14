//
//  CustomPickerViewController.swift
//  DigitalWallet
//

import UIKit
import Combine

class CustomPickerViewController: UIViewController {

    /// 背景
    @IBOutlet weak var backgroundView: UIView!
    /// `Picker`背景
    @IBOutlet weak var pickerBackgroundView: UIView!

    @IBOutlet weak var tableView: UITableView!
    /// 寬
    @IBOutlet weak var widthLayoutConstraint: NSLayoutConstraint!
    /// 高
    @IBOutlet weak var heightLayoutConstraint: NSLayoutConstraint!
    /// 左邊界
    @IBOutlet weak var leadingLayoutConstraint: NSLayoutConstraint!
    /// 上邊界
    @IBOutlet weak var topLayoutConstraint: NSLayoutConstraint!
    
    /// 選項列表
    private var nameList: [CustomPickerItem] = []
    /// 選中選項
    private var strSelectName: String? = nil
    /// 錨點位置及大小
    private var targetView: UIView = UIView()
    /// 選項高度
    private let kNameHeight: CGFloat = 37.0
    /// 與錨點間距
    private let kPadding: CGFloat = 10
    /// 列表的間距
    private let kListPadding: CGFloat = 8
    
    let selectSubject = PassthroughSubject<CustomPickerItem, Never>()
    
    private lazy var tapClose = UITapGestureRecognizer()
    
    var cancelSet = Set<AnyCancellable>()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        initView()
        initBinding()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        tableView.reloadData()
    }

    private func initView() {
        
        // 背景
        pickerBackgroundView.layer.cornerRadius = 10
        pickerBackgroundView.layer.shadowColor = UIColor._929292.cgColor
        pickerBackgroundView.layer.shadowOpacity = 0.25
        pickerBackgroundView.layer.shadowRadius = 14
        
        // TableView設定
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerWithNib(CustomPickerTableViewCell.self)
        tableView.sectionHeaderHeight = 0
        tableView.sectionFooterHeight = 0
        
        // 位置設定
        setPickerFrame()
        
        // 關閉點擊事件
        self.backgroundView.addGestureRecognizer(tapClose)
    }
    
    private func initBinding() {
        tapClose.gesturePublisher
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                guard let self = self else { return }
                dismiss(animated: false)
            }.store(in: &cancelSet)
    }
    
    /// 設定位置及是否可滑動
    private func setPickerFrame() {
        guard let window = UIApplication.shared.windows.first(where: { $0.isKeyWindow }) else { return }
        let targetRect = targetView.convert(targetView.bounds, to: window)
        
        // 基本大小
        let minWidth = self.view.frame.width * 0.5
        
        // 1. 計算寬度
        let width = max(minWidth, targetRect.size.width)
        widthLayoutConstraint.constant = width
        
        // 2. 計算左邊邊界位置
        let fLeading = (targetRect.minX + targetRect.size.width) - width
        leadingLayoutConstraint.constant = min(targetRect.minX, fLeading)
        
        // 3. 計算上方邊界位置
        let fTop = (targetRect.maxY + kPadding)
        topLayoutConstraint.constant = fTop
        
        // 4. 計算高度
        /// 列表高度
        let listHeight = CGFloat(nameList.count) * kNameHeight + (kListPadding * 2)
        /// 底部安全邊際高度
        let fbottomSafeArea = window.safeAreaInsets.bottom
        /// 剩餘高度
        let laveHeight = self.view.frame.height - (targetRect.maxY + kPadding + fbottomSafeArea)

        heightLayoutConstraint.constant = min(listHeight, laveHeight)
        
        // 5. 是否可以滑動
        tableView.isScrollEnabled = min(listHeight, laveHeight) < listHeight
    }
    
    /// 設定`Picker`
    /// - Parameters:
    ///   - list: 選項列表
    ///   - selectName: 選中選項
    ///   - targetRect: 目標位置及大小
    func setupPicker(list: [CustomPickerItem],
                     selectName: String?,
                     targetView: UIView) {
        self.nameList = list
        self.strSelectName = selectName
        self.targetView = targetView
    }
    
    func setupSelecedName(selectName: String?) {
        self.strSelectName = selectName
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}

extension CustomPickerViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return nameList.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return kNameHeight
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(CustomPickerTableViewCell.self, forIndexPath: indexPath)
        
        let cellData = nameList[indexPath.row]
        let isSelect: Bool = cellData.name == strSelectName
        
        cell.setupCell(pickerItem: cellData, isSelect: isSelect)
        
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let selectItem = nameList[indexPath.row]
        
        dismiss(animated: false) { [weak self] in
            guard let self = self else {
                return
            }
            
            selectSubject.send(selectItem)
        }
    }
}
