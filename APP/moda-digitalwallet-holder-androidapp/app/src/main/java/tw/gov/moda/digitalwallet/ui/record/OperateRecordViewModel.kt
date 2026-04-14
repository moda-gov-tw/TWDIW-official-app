package tw.gov.moda.digitalwallet.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.digitalwallet.data.element.OperateRecordEnum
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.digitalwallet.data.element.OrderEnum
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class OperateRecordViewModel @Inject constructor(
    private val mDatabase: DigitalWalletDB,
    private val mGson: Gson,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {

    private val mOrderType = MutableLiveData<OrderEnum>(OrderEnum.DESC)
    val orderType: LiveData<OrderEnum> get() = mOrderType

    private val mOperationRecordEnum = MutableLiveData<OperateRecordEnum>(OperateRecordEnum.Presentation)
    val operationRecordEnum: LiveData<OperateRecordEnum> get() = mOperationRecordEnum

    private val mPresentationRecordList = MutableLiveData<List<PresentationRecord>>()
    val presentationRecordList: LiveData<List<PresentationRecord>> get() = mPresentationRecordList

    private val mOperateRecordList = MutableLiveData<List<OperationRecord>>()
    val operateRecordList: LiveData<List<OperationRecord>> get() = mOperateRecordList

    private val mJsonFilesToZip = MutableLiveData<List<Pair<String, String>>>()
    val jsonFilesToZip: LiveData<List<Pair<String, String>>> get() = mJsonFilesToZip

    private var mUpdateTime = MutableLiveData<String>()
    val updateTime: LiveData<String> get() = mUpdateTime

    private val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)

    private var mPageIndex = 0
    private var mIsLastData = false

    fun getData(enum: OperateRecordEnum, orderType: OrderEnum) {
        viewModelScope.launch(getExceptionHandler()) {
            mOperationRecordEnum.value = enum
            mOrderType.value = orderType
            mIsLastData = false
            mPageIndex = 0
            mPresentationRecordList.value = listOf()
            mOperateRecordList.value = listOf()
            loadPageData(mPageIndex)
        }
    }

    fun nextPage() {
        viewModelScope.launch(getExceptionHandler()) {
            if (mIsLastData) {
                return@launch
            }
            mPageIndex = mPageIndex + 1
            loadPageData(mPageIndex)
        }
    }

    private suspend fun loadPageData(index: Int) {
        val pageSize = 10
        val orderType = mOrderType.value ?: OrderEnum.ASC
        // 讀取選取總類的紀錄
        when (mOperationRecordEnum.value) {
            OperateRecordEnum.Presentation -> {
                val items = withContext(Dispatchers.Default) {
                    if (orderType == OrderEnum.ASC) {
                        mDatabase.presentationRecordDao().getItemsAsc(pageSize, index * pageSize)
                    } else {
                        mDatabase.presentationRecordDao().getItemsDesc(pageSize, index * pageSize)
                    }
                }
                mIsLastData = items.count() < pageSize

                val newList = (mPresentationRecordList.value ?: listOf()) + items
                mPresentationRecordList.postValue(newList)
            }

            OperateRecordEnum.Modification -> {
                val items = withContext(Dispatchers.Default) {
                    if (orderType == OrderEnum.ASC) {
                        mDatabase.operationRecordDao().getItemsAsc(pageSize, index * pageSize)
                    } else {
                        mDatabase.operationRecordDao().getItemsDesc(pageSize, index * pageSize)
                    }
                }
                mIsLastData = items.count() < pageSize

                val newList = (mOperateRecordList.value ?: listOf()) + items
                mOperateRecordList.postValue(newList)
            }

            else -> return
        }
        mUpdateTime.postValue(sdf.format(Date()))
    }

    fun exportRecords() {
        viewModelScope.launch(getExceptionHandler()) {
            mProgressBar.postValue(true)
            val presentationRecordList = mDatabase.presentationRecordDao().getAll()
            val jsonPresentationRecord = mGson.toJson(presentationRecordList.map {
                mapOf(
                    "日期" to sdf.format(Date(it.datetime)),
                    "標題" to it.text,
                    "授權憑證" to it.vcNames,
                    "單位" to it.authorizationUnit,
                    "目的" to it.authorizationPurpose,
                    "資料" to it.authorizationFields
                )
            })


            val operationRecordList = mDatabase.operationRecordDao().getAll()
            val jsonOperationRecord = mGson.toJson(operationRecordList.map {
                mapOf(
                    "日期" to sdf.format(Date(it.datetime)),
                    "標題" to it.text,
                    "類型" to when (it.status) {
                        OperationEnum.ADD_CARD -> mResourceProvider.getString(R.string.add_card)
                        OperationEnum.DELETE_CARD -> mResourceProvider.getString(R.string.remove_credential)
                        OperationEnum.INVAILD_CARD -> mResourceProvider.getString(R.string.invalid_card)
                        OperationEnum.EDIT_WALLET_PINCODE -> mResourceProvider.getString(R.string.edit_wallet_password)
                        else -> ""
                    }
                )
            })

            val jsonFiles = listOf(
                "授權紀錄.json" to jsonPresentationRecord,
                "異動紀錄.json" to jsonOperationRecord,
            )

            mJsonFilesToZip.postValue(jsonFiles)
            mProgressBar.postValue(false)
        }
    }
}