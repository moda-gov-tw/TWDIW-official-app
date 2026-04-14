package tw.gov.moda.digitalwallet.ui.home.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ResultPoint
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.Size
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.extension.hashTag
import tw.gov.moda.digitalwallet.extension.showMessageDialog
import tw.gov.moda.digitalwallet.ui.base.BaseFragment
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.digitalwallet.ui.main.MainViewModel
import tw.gov.moda.digitalwallet.ui.verifiable.credential.ApplyCompletedBottomSheetDialogFragment
import tw.gov.moda.digitalwallet.util.CustomViewfinderView
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.FragmentScanBinding
import kotlin.math.max
import kotlin.math.min

/**
 * 掃描頁
 */
@AndroidEntryPoint
class ScanFragment : BaseFragment<FragmentScanBinding>(), BarcodeCallback {

    companion object {
        fun newInstance() = ScanFragment()
    }

    override fun initViewBinding(container: ViewGroup?): FragmentScanBinding = FragmentScanBinding.inflate(layoutInflater, container, false)

    override fun getViewModel(): BaseViewModel = mViewModel

    private val mViewModel: ScanViewModel by viewModels()

    private val mActivityViewModel: MainViewModel by activityViewModels<MainViewModel>()

    private var mAreaRect = Rect(0, 0, 0, 0)

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            binding.viewDecorateBarcode.isVisible = true
            startCamera()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            binding.viewDecorateBarcode.pause()
        } else {
            binding.viewDecorateBarcode.resume()
            askCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.viewDecorateBarcode.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.viewDecorateBarcode.resume()
    }

    override fun initView() {
        super.initView()
        askCameraPermission()
        setInsets(mActivityViewModel, binding.viewTop, binding.viewBottom)
        (binding.viewDecorateBarcode.viewFinder as? CustomViewfinderView)?.setAnchorView(binding.layScanner)
        binding.layScanner.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mAreaRect = Rect(binding.layScanner.left, binding.layScanner.top, binding.layScanner.right, binding.layScanner.bottom)
                binding.layScanner.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        binding.imgFlashlight.isSelected = false
        binding.imgFlashlight.setOnClickListener {
            binding.imgFlashlight.isSelected = !binding.imgFlashlight.isSelected
            if (binding.imgFlashlight.isSelected) {
                binding.imgFlashlight.setImageResource(R.drawable.ic_flashlight_on)
                binding.viewDecorateBarcode.setTorchOn()
            } else {
                binding.imgFlashlight.setImageResource(R.drawable.ic_flashlight_off)
                binding.viewDecorateBarcode.setTorchOff()
            }
        }

        binding.imgBack.setOnClickListener {
            mActivityViewModel.pageController.popBackStack()
        }
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        mViewModel.launchVerifiablePresentationFragment.observe(viewLifecycleOwner) { isShow ->
            if (isShow) {
                mActivityViewModel.pageController.launchVerifiablePresentationFragment()
            }
        }
        mViewModel.alertParsingQRCodeError.observe(viewLifecycleOwner) { isShow ->
            activity?.apply {
                if (isShow && !isFinishing) {
                    mProgressDialog?.dismiss()
                    showMessageDialog(childFragmentManager, getString(R.string.msg_qrcode_parsing_failed), { binding.viewDecorateBarcode.resume() })
                }
            }
        }
        mViewModel.alertEmptyVerifiableCredential.observe(viewLifecycleOwner) { isShow ->
            activity?.apply {
                if (isShow && !isFinishing) {
                    mProgressDialog?.dismiss()
                    showMessageDialog(childFragmentManager, getString(R.string.msg_qrcode_parsing_failed_by_vc), { binding.viewDecorateBarcode.resume() })
                }
            }
        }
        mViewModel.addVerifiableCredentialSuccessful.observe(viewLifecycleOwner) { name ->
            activity?.apply {
                if (name.isNotBlank() && !isFinishing) {
                    ApplyCompletedBottomSheetDialogFragment.newInstance()
                        .show(parentFragmentManager, ApplyCompletedBottomSheetDialogFragment::class.hashTag())
                }
            }
        }
        mActivityViewModel.scanStatus.observe(viewLifecycleOwner) { isEnable ->
            activity?.apply {
                if (!isFinishing) {
                    if (isEnable) {
                        binding.viewDecorateBarcode.resume()
                    } else {
                        binding.viewDecorateBarcode.pause()
                    }
                }
            }
        }
    }

    override fun getExceptionAlertAction(): (() -> Unit)? = {
        binding.viewDecorateBarcode.resume()
    }

    override fun getAlertMessageAction(): (() -> Unit)? = {
        binding.viewDecorateBarcode.resume()
    }

    override fun barcodeResult(result: BarcodeResult?) {
        // 創建 Rect 物件
        result?.also { data ->
            val resultRect = createRectFromPoints(data.resultPoints)
            if (mAreaRect.contains(resultRect)) {
                binding.viewDecorateBarcode.pause()
                mViewModel.parseQRCode(data.result.text)
            }
        }
    }

    private fun askCameraPermission() {
        context?.apply {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                binding.viewDecorateBarcode.isVisible = true
                startCamera()
            } else {
                requestMultiplePermissions.launch(arrayOf(Manifest.permission.CAMERA))
            }
        }
    }

    private fun startCamera() {
        // 設定只掃描 QR Code 格式
        val displayMetrics: DisplayMetrics = resources.displayMetrics
        binding.viewDecorateBarcode.decoderFactory = DefaultDecoderFactory(listOf(BarcodeFormat.QR_CODE))
        binding.viewDecorateBarcode.barcodeView.framingRectSize = Size(displayMetrics.widthPixels, displayMetrics.heightPixels)


        binding.viewDecorateBarcode.cameraSettings.isAutoFocusEnabled = true
        binding.viewDecorateBarcode.cameraSettings.isContinuousFocusEnabled = true
        binding.viewDecorateBarcode.decodeContinuous(this)
        binding.viewDecorateBarcode.resume()
    }

    /**
     * 根據四個點創建矩形
     * @param points 四個點
     * @return 生成的矩形
     */
    private fun createRectFromPoints(points: Array<ResultPoint>): Rect {
        if (points.isEmpty()) {
            throw IllegalArgumentException("Point array is empty")
        }

        var minX: Int = Int.MAX_VALUE
        var maxX: Int = Int.MIN_VALUE
        var minY: Int = Int.MAX_VALUE
        var maxY: Int = Int.MIN_VALUE

        // 找到最小和最大的 x 和 y 值
        for (point in points) {
            minX = min(minX, point.x.toInt())
            maxX = max(maxX, point.x.toInt())
            minY = min(minY, point.y.toInt())
            maxY = max(maxY, point.y.toInt())
        }

        // 使用最小和最大值創建 Rect
        return Rect(minX, minY, maxX, maxY)
    }

    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        if (uri == null) {
            return null
        }
        context?.apply {
            val source = ImageDecoder.createSource(contentResolver, uri)
            return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // 避免硬體加速 bug
            }
        }

        return null
    }

    private fun decodeQRCodeFromMLKit(bitmap: Bitmap, coroutine: CancellableContinuation<String?>) {
        context?.apply {
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)  // 限定 QR Code
                .build()
            val scanner = BarcodeScanning.getClient(options)
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            scanner.process(inputImage)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        for (barcode in it.result) {
                            if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                coroutine.resumeWith(Result.success(barcode.rawValue))
                                return@addOnCompleteListener
                            }
                        }
                    }
                    coroutine.resumeWith(Result.success(""))
                }
        }
    }

    private suspend fun preprocessBitmap(original: Bitmap, maxSize: Int = 512): Bitmap = withContext(Dispatchers.IO) {
        // Step 1: 縮小圖片尺寸
        val scale = maxSize.toFloat() / maxOf(original.width, original.height)
        val scaledBitmap = Bitmap.createScaledBitmap(
            original,
            (original.width * scale).toInt(),
            (original.height * scale).toInt(),
            true
        )

        // Step 2: 灰階處理
        val grayscaleBitmap = Bitmap.createBitmap(
            scaledBitmap.width, scaledBitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(grayscaleBitmap)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = filter
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        return@withContext grayscaleBitmap
    }


    private fun decodeQRCodeFromBitmap(bitmap: Bitmap): String? {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        val hints = mapOf<DecodeHintType, Any>(
            DecodeHintType.TRY_HARDER to true,                  // 嘗試更強力解析（更慢但準）
            DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE), // 限定只掃 QR Code
            DecodeHintType.CHARACTER_SET to "UTF-8"             // 指定字元編碼
        )


        val reader = MultiFormatReader().apply {
            setHints(hints)
        }

        return try {
            val result = reader.decode(binaryBitmap)
            result.text
        } catch (e: NotFoundException) {
            null // 沒有找到 QR code
        }
    }

    private suspend fun parseBitmap(bitmap: Bitmap): String? = withContext(Dispatchers.IO) {
        return@withContext suspendCancellableCoroutine { coroutine ->
            val text = decodeQRCodeFromBitmap(bitmap)
            if (text.isNullOrBlank()) {
                decodeQRCodeFromMLKit(bitmap, coroutine)
            } else {
                coroutine.resumeWith(Result.success(text))
            }
        }
    }
}