//
//  SearchLogTableViewCell.swift
//  DigitalWallet
//

import UIKit

class SearchCertificatesLogTableViewCell: UITableViewCell {
    @IBOutlet weak var searchLogCell: UILabel!
    @IBOutlet weak var cleanButton: UIButton!
    /// 當筆資料
    var data: SearchCertificateLog?
    var vcLogData: SearchVCLog?
    /// 點擊刪除
    var onClickDelete: ((SearchCertificateLog) -> Void)?
    var onClickVCLogDelete: ((SearchVCLog) -> Void)?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setupView()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
    }
    
    private func setupView() {
        selectionStyle = .none
        searchLogCell.font = FontName.NotoSansTC_Regular.font.withSize(16)
    }
    
    @IBAction func clickDelete(_ sender: Any) {
        if let data = data {
            onClickDelete?(data)
        }
        if let vcLogData = vcLogData {
            onClickVCLogDelete?(vcLogData)
        }
    }
}

extension SearchCertificatesLogTableViewCell {
    func setupCell(data: SearchCertificateLog) {
        self.data = data
        searchLogCell.text = data.name
    }
    
    func setupCell(data: SearchVCLog) {
        self.vcLogData = data
        searchLogCell.text = data.name
    }
}

