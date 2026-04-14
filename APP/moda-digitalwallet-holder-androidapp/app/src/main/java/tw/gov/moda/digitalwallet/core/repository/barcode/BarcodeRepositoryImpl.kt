package tw.gov.moda.digitalwallet.core.repository.barcode

import tw.gov.moda.digitalwallet.ui.barcode.BarcodeScaleViewModel

class BarcodeRepositoryImpl : BarcodeRepository {
    private var mBarcodeScaleModel: BarcodeScaleViewModel.BarcodeScaleModel? = null
    override fun setBarcodeScaleModel(model: BarcodeScaleViewModel.BarcodeScaleModel?) {
        this.mBarcodeScaleModel = model
    }

    override fun getBarcodeScaleModel(): BarcodeScaleViewModel.BarcodeScaleModel? {
        return this.mBarcodeScaleModel
    }
}