//
//  VerifiablePresentationCustomTableViewCell.swift
//  DigitalWallet
//

import UIKit

class VerifiablePresentationCustomTableViewCell: UITableViewCell {

    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var customTextField: CustomTextField!
    @IBOutlet weak var hintLabel: UILabel!
    
    var customField: DwModa201iCustomField?
    
    /// 結束編輯
    var onFinishEdit: (() -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupUI()
    }

    private func setupUI() {
        customTextField.delegate = self
        
        hintLabel.isHidden = true
    }
    
    func setupCell(_ data: DwModa201iCustomField, eyeOpen: Bool) {
        customField = data
        titleLabel.text = data.cname
        customTextField.isSecureTextEntry = !eyeOpen
        customTextField.text = data.value
        hintLabel.text = data.description
        
        updateErrorHint(isHidden: data.isLegal)
    }
    
    /// 更新錯誤提示
    /// - Parameter isHidden: 隱藏錯誤提示
    private func updateErrorHint(isHidden: Bool) {
        let cgColor = isHidden ? UIColor.DEDEDE.cgColor : UIColor.EF_2_A_1_C.cgColor
        customTextField.layer.borderColor = cgColor
        hintLabel.isHidden = isHidden
    }
    
    
}

extension VerifiablePresentationCustomTableViewCell: UITextFieldDelegate {    
    func textFieldDidEndEditing(_ textField: UITextField) {
        customField?.value = textField.text
        onFinishEdit?()
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.endEditing(true)
        return true
    }
}
