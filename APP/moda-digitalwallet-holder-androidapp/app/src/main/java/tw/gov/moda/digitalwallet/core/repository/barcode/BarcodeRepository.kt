package tw.gov.moda.digitalwallet.core.repository.barcode

import tw.gov.moda.digitalwallet.ui.barcode.BarcodeScaleViewModel

interface BarcodeRepository {
    fun setBarcodeScaleModel(model: BarcodeScaleViewModel.BarcodeScaleModel?)

    fun getBarcodeScaleModel(): BarcodeScaleViewModel.BarcodeScaleModel?
}