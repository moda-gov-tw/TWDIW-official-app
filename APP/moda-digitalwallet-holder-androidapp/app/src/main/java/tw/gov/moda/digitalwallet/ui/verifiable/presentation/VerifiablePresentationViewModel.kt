package tw.gov.moda.digitalwallet.ui.verifiable.presentation

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.gov.moda.digitalwallet.core.db.DigitalWalletDB
import tw.gov.moda.digitalwallet.core.identifier.IdentifierManager
import tw.gov.moda.digitalwallet.core.network.NetworkManager
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.repository.wallet.WalletRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.core.verifiable.VerifiableManager
import tw.gov.moda.digitalwallet.data.db.CredentialRecord
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.digitalwallet.data.element.CardStatusEnum
import tw.gov.moda.digitalwallet.data.element.SDKErrorEnum
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupEnum
import tw.gov.moda.digitalwallet.data.element.VerifiableCredentialGroupRuleEnum
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.element.VerificationSourceEnum
import tw.gov.moda.digitalwallet.data.model.Description
import tw.gov.moda.digitalwallet.data.model.Purpose
import tw.gov.moda.digitalwallet.data.model.RequestToken
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialField
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialGroup
import tw.gov.moda.digitalwallet.data.model.RequireVerifiablePresentationField
import tw.gov.moda.digitalwallet.data.model.VerifiablePresentationResultData
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk401i
import tw.gov.moda.digitalwallet.data.model.dwsdk.Dwsdk402i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr402i
import tw.gov.moda.digitalwallet.ui.base.LoginBaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

@HiltViewModel
class VerifiablePresentationViewModel @Inject constructor(
    override val mWalletRepository: WalletRepository,
    private val mDatabase: DigitalWalletDB,
    private val mVerifiableManager: VerifiableManager,
    override val mResourceProvider: ResourceProvider,
    private val mGson: Gson,
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mIdentifierManager: IdentifierManager,
    private val mNetworkManager: NetworkManager
) : LoginBaseViewModel(mWalletRepository, mResourceProvider) {

    private val mEnabledConfirm = MediatorLiveData<Boolean>()
    val enabledConfirm: LiveData<Boolean> get() = mEnabledConfirm
    private val mRequireVerifiableCredentialList = MutableLiveData<List<RequireVerifiableCredentialGroup>>()
    val requireVerifiableCredentialList: LiveData<List<RequireVerifiableCredentialGroup>> get() = mRequireVerifiableCredentialList
    private val mVPCustomFieldList = MutableLiveData<List<RequireVerifiablePresentationField>>()
    val vpCustomFieldList: LiveData<List<RequireVerifiablePresentationField>> get() = mVPCustomFieldList
    private val mVerifyProgressBar = MutableLiveData<Boolean>()
    val verifyProgressBar: LiveData<Boolean> get() = mVerifyProgressBar
    private val mLaunchVerificationResultFragment = MutableLiveData<Boolean>()
    val launchVerificationResultFragment: LiveData<Boolean> get() = mLaunchVerificationResultFragment
    private val mLaunchShowCredentialBarCodeFragment = MutableLiveData<Boolean>()
    val launchShowCredentialBarCodeFragment: LiveData<Boolean> get() = mLaunchShowCredentialBarCodeFragment
    private val mVerifiablePresentationType = MutableLiveData<VerifiablePresentationEnum>()
    val verifiablePresentationType: LiveData<VerifiablePresentationEnum> = mVerifiablePresentationType

    private val mVerifiablePresentationUnit = MutableLiveData<String>()
    val verifiablePresentationUnit: LiveData<String> get() = mVerifiablePresentationUnit
    private val mVerifiablePresentationName = MutableLiveData<String>()
    val verifiablePresentationName: LiveData<String> get() = mVerifiablePresentationName
    private val mVerifiablePresentationPurpose = MutableLiveData<String>()
    val verifiablePresentationPurpose: LiveData<String> get() = mVerifiablePresentationPurpose

    private val mShowChangeVerifiablePresentationDialog = MediatorLiveData<Boolean>()
    val showChangeVerifiablePresentationDialog: LiveData<Boolean> get() = mShowChangeVerifiablePresentationDialog
    private val mShowNonAuthorizedCredentialDialog = MutableLiveData<Boolean>()
    val showNonAuthorizedCredentialDialog: LiveData<Boolean> get() = mShowNonAuthorizedCredentialDialog
    private val mShowNotSelectorAllDialog = MutableLiveData<Boolean>()
    val showNotSelectorAllDialog: LiveData<Boolean> get() = mShowNotSelectorAllDialog
    private val mShowDeleteCredentialDialog = MutableLiveData<String>()
    val showDeleteCredentialDialog: LiveData<String> get() = mShowDeleteCredentialDialog
    private val mShowNetworkErrorAlert = MutableLiveData<Boolean>()
    val showNetworkErrorAlert: LiveData<Boolean> get() = mShowNetworkErrorAlert

    private val mShowCannotSendDataDialog = MutableLiveData<Boolean>()
    val showCannotSendDataDialog: LiveData<Boolean> get() = mShowCannotSendDataDialog
    private val mLaunchAuthorizedCredentialFragment = MutableLiveData<Boolean>()
    val launchAuthorizedCredentialFragment: LiveData<Boolean> get() = mLaunchAuthorizedCredentialFragment
    private val mLaunchContractDetailFragment = MutableLiveData<Boolean>()
    val launchContractDetailFragment: LiveData<Boolean> get() = mLaunchContractDetailFragment

    private val mIsCheckField = MutableLiveData<Boolean>()

    private val mIsHidden = MutableLiveData<Boolean>(false)
    val isHidden: LiveData<Boolean> get() = mIsHidden

    private var defaultVerifiableCredentialList: List<RequireVerifiableCredentialGroup> = emptyList()
    private var mDeleteGroupName = ""
    private var mDeleteUId: Long? = null

    init {
        mVerifiablePresentationType.postValue(mVerifiablePresentationRepository.getVerifiablePresentationEnum())

        viewModelScope.launch(getExceptionHandler()) {
            mWalletRepository.getContractAgreeLiveData().value = false

            // 取得VerifiablePresentation 請求資料
            val dwsdk401i = mWalletRepository.getParseVerifiablePresentation() ?: return@launch
            val parseVPData = dwsdk401i.data ?: return@launch
            // 取得當前 Wallet
            val wallet = mWalletRepository.getWallet() ?: return@launch

            // 顯示驗證目的
            mVerifiablePresentationRepository.setContractTitle("")
            mVerifiablePresentationRepository.setContractContent("")
            mVerifiablePresentationUnit.postValue("")
            mVerifiablePresentationName.postValue("")
            mVerifiablePresentationPurpose.postValue("")

            parseVPData.requestToken?.split(".")?.getOrNull(1)?.let { Base64.decode(it, Base64.URL_SAFE) }?.let { String(it, Charsets.UTF_8) }?.also { json ->
                mGson.fromJson(json, RequestToken::class.java)?.also { requestToken ->
                    requestToken.presentationDefinition?.purpose?.let { mGson.fromJson(it, Purpose::class.java) }?.also { purpose: Purpose ->
                        mVerifiablePresentationRepository.setContractTitle(mResourceProvider.getString(R.string.format_known_agree_contract).format(purpose.scenario ?: ""))
                        mVerifiablePresentationRepository.setContractContent(purpose.termsUri ?: "")
                        mVerifiablePresentationUnit.postValue(purpose.client ?: "")
                        mVerifiablePresentationName.postValue(purpose.scenario ?: "")
                        mVerifiablePresentationPurpose.postValue(purpose.purpose ?: "")
                    }
                }
            }

            // 初始化授權請求清單
            val vcs = mDatabase.verifiableCredentialDao().getAll(wallet.uid)
            var groupList = arrayListOf<RequireVerifiableCredentialGroup>()

            // 建立 Group
            parseVPData.requestDataArray?.forEach { requestData ->
                run breaking@{
                    val cardList = arrayListOf<RequireVerifiableCredentialCard>()
                    var nonCardNum = 0
                    // 建立 Card
                    requestData.cards.forEach card@{ card ->
                        // step1. 找出符合的全部VC以及參數
                        val verifiableCredentialList = vcs.filter { it.status == CardStatusEnum.Valid && it.types.contains(card.card) }
                        // step2. 建立出VC清單
                        if (verifiableCredentialList.isEmpty()) {
                            // 不在Wallet裡面的VC
                            val name = RequireVerifiableCredentialCard(card = card.card ?: "", cardId = card.cardId ?: "", name = card.name ?: "",)
                            cardList.add(name)
                            nonCardNum++
                            return@card
                        }
                        verifiableCredentialList.forEach { verifiableCredential ->
                            // 選出 VP 需要的Field
                            val data = mVerifiableManager.getVerifiableCredential(verifiableCredential.credential)?.vc?.credentialSubject?.field?.data
                            val fieldList = card.fields?.map { field ->
                                val title = verifiableCredential.credentialSubject[field]?.displayArray?.firstOrNull()?.name ?: field
                                val value = data?.get(field) ?: ""
                                RequireVerifiableCredentialField(field = field, title = title, value = value)
                            } ?: emptyList()

                            // 取得VC的IAL等級
                            val ialLevel = try {
                                mGson.fromJson(verifiableCredential.description, Description::class.java)?.ial?.takeIf { it.isNotBlank() } ?: "0"
                            } catch (e: Exception) {
                                "0"
                            }

                            // 建立卡片
                            val name = RequireVerifiableCredentialCard(
                                title = verifiableCredential.display,
                                card = card.card ?: "",
                                cardId = card.cardId ?: "",
                                name = card.name ?: "",
                                issuer = verifiableCredential.issuingUnit,
                                fields = fieldList,
                                verifiableCredential = verifiableCredential,
                                isValid = true,
                                ial = ialLevel,
                            )
                            cardList.add(name)
                        }
                    }

                    // Card 根據 IAL 排序
                    cardList.sortByDescending { it.ial.substringAfter("IAL ").toIntOrNull() ?: Int.MIN_VALUE }

                    val groupType = getGroupType(requestData, nonCardNum)
                    val isShowAuthorizedVC = getShowAuthorizedVC(requestData)
                    // 根據rule來判斷要幾張卡
                    val count = if (requestData.rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                        requestData.cards.size
                    } else {
                        requestData.count ?: requestData.cards.size
                    }

                    val group = RequireVerifiableCredentialGroup(
                        cardList = cardList,
                        title = (requestData.name ?: mVerifiablePresentationName.value) ?: "",
                        count = count,
                        // 預設先0 後面判斷完顯示的數量再更新
                        enoughCount = 0,
                        group = requestData.group,
                        rule = requestData.rule,
                        type = groupType,
                        // 預設先false 後面判斷完再更新
                        isShowAuthorizedVCTitle = false,
                        isShowAuthorizedVC = isShowAuthorizedVC,
                        // 預設先false 後面判斷完顯示的數量再更新
                        isShowChangeVC = false,
                        isHidden = false
                    )
                    groupList.add(group)
                }
            }
            defaultVerifiableCredentialList = groupList

            // 選出要顯示的授權請求清單
            // 需要考慮數量以及重複的卡
            groupList = arrayListOf()
            defaultVerifiableCredentialList.forEach { group ->
                val cardList = arrayListOf<RequireVerifiableCredentialCard>()
                val count = group.count
                for (i in 0 until group.cardList.size) {
                    // 檢查有沒有達到上限 或者 無效
                    if (cardList.size >= count || group.cardList[i].isValid.not()) {
                        continue
                    }
                    // 檢查該VC有沒有已經被加入過了
                    if (cardList.any { it.card == group.cardList[i].card }) {
                        continue
                    }
                    cardList.add(group.cardList[i])
                }

                // 使用者能提供的VC上限
                val enoughCount = cardList.size
                val isShowChangeVC = getShowChangeVC(group.rule, group.count, cardList.size, enoughCount)
                val isShowAuthorizedVCTitle = getShowAuthorizedVCTitle(group.count, cardList.size)
                group.isShowChangeVC = isShowChangeVC
                group.isShowAuthorizedVCTitle = isShowAuthorizedVCTitle
                group.enoughCount = enoughCount

                val newGroup = group.copy(cardList = cardList)
                groupList.add(newGroup)
            }
            mRequireVerifiableCredentialList.value = groupList

            // 建立Custom輸入框
            val customFieldList = mWalletRepository.getVerifiablePresentationCustom()?.fields?.mapIndexed { index, field ->
                RequireVerifiablePresentationField(
                    index = index,
                    ename = field.ename ?: "",
                    cname = field.cname ?: "",
                    description = field.description ?: "",
                    regex = field.regex ?: "",
                    value = field.value ?: "")
            } ?: emptyList()
            mVPCustomFieldList.postValue(customFieldList)

            // 更新開關狀態
            updateBtnConfirm(groupList, customFieldList)

            // 如果And的Group缺少可授權的憑證顯示Dialog
            groupList.forEach { group ->
                if (group.rule == VerifiableCredentialGroupRuleEnum.ALL.code && group.isShowAuthorizedVCTitle) {
                    mShowCannotSendDataDialog.postValue(true)
                    return@forEach
                }
            }
            // 檢查錯誤代碼
            if (dwsdk401i.code == SDKErrorEnum.ERROR_4012.code) {
                mShowNonAuthorizedCredentialDialog.postValue(true)
            }
        }

        mEnabledConfirm.apply {
            fun integration() {
                value = (mIsCheckField.value == true)
            }
            addSource(mIsCheckField) {
                integration()
            }
        }
    }

    fun setShowCannotSendDataDialog(isShow: Boolean) {
        mShowCannotSendDataDialog.postValue(isShow)
    }

    /**
     * 設定資料隱藏顯示
     */
    fun setIsGroupVisible() {
        val isHidden = mIsHidden.value?.not() ?: return
        // 隱藏VC資料
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: mutableListOf()
        for (i in 0 until groupList.size) {
            groupList[i] = groupList[i].copy(isHidden = isHidden)
        }
        mRequireVerifiableCredentialList.value = groupList
        // 隱藏Custom資料
        val customFieldList = mVPCustomFieldList.value?.toMutableList() ?: mutableListOf()
        for (i in 0 until customFieldList.size) {
            customFieldList[i] = customFieldList[i].copy(isHidden = isHidden)
        }
        mVPCustomFieldList.value = customFieldList
        // 紀錄Hidden狀態
        mIsHidden.postValue(isHidden)
    }

    /**
     * 記錄輸入資料
     */
    fun setCustomText(index: Int, value: String) {
        val customFieldList = mVPCustomFieldList.value?.toMutableList() ?: mutableListOf()
        customFieldList[index] = customFieldList[index].copy(value = value)
        mVPCustomFieldList.value = customFieldList
        updateBtnConfirm(mRequireVerifiableCredentialList.value ?: return, customFieldList)
    }

    /**
     * 展開/折疊
     */
    fun setExpand(groupName: String, card: String, isExpand: Boolean) {
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: return
        val index = groupList.withIndex().find { it.value.title == groupName }?.index ?: return
        val cardList = groupList[index].cardList.toMutableList()
        val cardIndex = cardList.withIndex().firstOrNull { it.value.card == card }?.index ?: return
        // 如果是要展開
        if (isExpand.not()) {
            // 取得沒勾的數量
            val count = cardList[cardIndex].fields.count { field -> field.isCheck.not() }
            // 完全沒勾走刪除
            if (count == cardList[cardIndex].fields.count()) {
                // 跳刪除訊息
                mDeleteGroupName = groupName
                mDeleteUId = cardList[cardIndex].verifiableCredential?.uid
                mShowDeleteCredentialDialog.postValue(mResourceProvider.getString(R.string.delete_select_credentials).format(cardList[cardIndex].title))
                return
            }
        }
        // 更新資料
        cardList[cardIndex] = cardList[cardIndex].copy(isExpand = isExpand)
        groupList[index] = groupList[index].copy(cardList = cardList)
        mRequireVerifiableCredentialList.value = groupList
    }

    /**
     * 選擇全部
     */
    fun setIsCheckAll(groupName: String, card: String, isChecked: Boolean) {
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: return
        val index = groupList.withIndex().firstOrNull { it.value.title == groupName }?.index ?: return
        val cardList = groupList[index].cardList.toMutableList()
        val cardIndex = cardList.withIndex().firstOrNull { it.value.card == card }?.index ?: return
        val fieldList = groupList[index].cardList[cardIndex].fields.toMutableList()
        for (i in 0 until fieldList.size) {
            fieldList[i] = fieldList[i].copy(isCheck = isChecked)
        }
        cardList[cardIndex] = cardList[cardIndex].copy(isCheckAll = isChecked, fields = fieldList)
        groupList[index] = groupList[index].copy(cardList = cardList)
        // 檢查資料能否足夠點擊送出按鈕
        updateBtnConfirm(groupList, mVPCustomFieldList.value ?: emptyList())
        mRequireVerifiableCredentialList.value = groupList
    }

    /**
     * 單選
     */
    fun setIsCheck(groupName: String, card: String, field: String, isChecked: Boolean) {
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: return
        val index = groupList.withIndex().firstOrNull { it.value.title == groupName }?.index ?: return
        val cardList = groupList[index].cardList.toMutableList()
        val cardIndex = cardList.withIndex().firstOrNull { it.value.card == card }?.index ?: return
        val fieldList = groupList[index].cardList[cardIndex].fields.toMutableList()
        val fieldIndex = fieldList.withIndex().firstOrNull { it.value.field == field }?.index ?: return
        fieldList[fieldIndex] = fieldList[fieldIndex].copy(isCheck = isChecked)
        // 檢查 全選 開關狀態
        val count = fieldList.count { it.isCheck }
        val isAllCheck = count == fieldList.size
        cardList[cardIndex] = cardList[cardIndex].copy(isCheckAll = isAllCheck, fields = fieldList)
        groupList[index] = groupList[index].copy(cardList = cardList)
        // 檢查資料能否足夠點擊送出按鈕
        updateBtnConfirm(groupList, mVPCustomFieldList.value ?: emptyList())
        mRequireVerifiableCredentialList.value = groupList
    }

    /**
     * 前往更改憑證頁
     */
    fun showChangeCredential(groupName: String, selectCard: RequireVerifiableCredentialCard?) {
        val group = defaultVerifiableCredentialList.find { it.title == groupName } ?: return
        val cardList = mRequireVerifiableCredentialList.value?.find { it.title == groupName }?.cardList ?: return
        mVerifiablePresentationRepository.setVerifiablePresentationGroup(group)
        mVerifiablePresentationRepository.setCurrVerifiablePresentationCardList(cardList)
        mVerifiablePresentationRepository.setSelectVerifiablePresentationCard(selectCard)
        mShowChangeVerifiablePresentationDialog.postValue(true)
    }

    /**
     * 更改憑證後返回刷新畫面
     */
    fun setSelectCredentialList() {
        val newGroup = mVerifiablePresentationRepository.getNewVerifiablePresentationGroup() ?: return
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: return
        val index = groupList.withIndex().firstOrNull { it.value.group == newGroup.group }?.index ?: return
        // 本來如果沒資料 就不進行更新
        if (groupList[index].enoughCount == 0) {
            return
        }
        val type = if (newGroup.cardList.isEmpty()) {
            VerifiableCredentialGroupEnum.Empty
        } else {
            VerifiableCredentialGroupEnum.Default
        }

        val isShowChangeVC = if (type == VerifiableCredentialGroupEnum.Empty) {
            true
        } else {
            newGroup.cardList.size < newGroup.enoughCount
        }
        groupList[index] = groupList[index].copy(cardList = newGroup.cardList, type = type, isShowChangeVC = isShowChangeVC)
        // 檢查資料能否足夠點擊送出按鈕
        updateBtnConfirm(groupList, mVPCustomFieldList.value ?: emptyList())
        mRequireVerifiableCredentialList.value = groupList
        mVerifiablePresentationRepository.setNewVerifiablePresentationGroup(null)
    }

    /**
     * 刪除憑證
     */
    fun deleteCredential() {
        mShowDeleteCredentialDialog.postValue("")
        val groupList = mRequireVerifiableCredentialList.value?.toMutableList() ?: return
        val index = groupList.withIndex().firstOrNull { it.value.title == mDeleteGroupName }?.index ?: return
        val cardList = groupList[index].cardList.toMutableList()
        cardList.removeIf { it.verifiableCredential?.uid == mDeleteUId }
        val type = if (cardList.isEmpty()) {
            VerifiableCredentialGroupEnum.Empty
        } else {
            VerifiableCredentialGroupEnum.Default
        }

        val isShowChangeVC = if (type == VerifiableCredentialGroupEnum.Empty) {
            true
        } else {
            if (groupList[index].rule == null || groupList[index].rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                // 如果是ALL 判斷數量對不對
                cardList.size != groupList[index].count
            } else {
                // 如果使用者可以加入的卡片大於目前顯示的卡片就顯示 加入卡片
                groupList[index].enoughCount > cardList.size
            }
        }
        groupList[index] = groupList[index].copy(cardList = cardList, type = type, isShowChangeVC = isShowChangeVC)
        mRequireVerifiableCredentialList.value = groupList
        mDeleteUId = null
        mDeleteGroupName = ""
    }

    /**
     * 前往可授權憑證頁
     * @see tw.gov.moda.digitalwallet.ui.verifiable.authorized.AuthorizedCredentialFragment
     */
    fun launchAuthorizedCredentialList(groupName: String) {
        val group = defaultVerifiableCredentialList.find { it.title == groupName } ?: return
        mVerifiablePresentationRepository.setAuthorizedCredentialList(group.cardList)
        mLaunchAuthorizedCredentialFragment.postValue(true)
    }

    /**
     * 檢查有沒有缺少欄位或者少選項目
     */
    fun checkSelectItem() {
        // 檢查Custom欄位有沒有符合正規表示式
        var isCustomFieldError = false
        val customFieldList = mVPCustomFieldList.value?.toMutableList() ?: mutableListOf()
        for (i in 0 until customFieldList.size) {
            customFieldList[i].regex?.let {
                val regex = Regex(it)
                if (customFieldList[i].value.matches(regex).not()) {
                    customFieldList[i] = customFieldList[i].copy(isError = true)
                    isCustomFieldError = true
                } else {
                    customFieldList[i] = customFieldList[i].copy(isError = false)
                }
            }
        }
        mVPCustomFieldList.value = customFieldList
        // 全部比對完如果有不符合的就直接出去
        if (isCustomFieldError) {
            return
        }
        // 檢查VC有沒有缺少選項
        mRequireVerifiableCredentialList.value?.forEach { group ->
            if (group.type == VerifiableCredentialGroupEnum.Empty) {
                mShowNotSelectorAllDialog.postValue(true)
                return
            }
            if (group.cardList.size < group.count) {
                mShowNotSelectorAllDialog.postValue(true)
                return
            }
            group.cardList.forEach { card ->
                card.fields.forEach { field ->
                    if (field.isCheck.not()) {
                        mShowNotSelectorAllDialog.postValue(true)
                        return
                    }
                }
            }
        }
        requireVerification(VerificationSourceEnum.VerifiablePresentation)
    }

    /**
     * 進行 VerifiablePresentation 授權
     */
    fun verify() {
        viewModelScope.launch(getExceptionHandler { _, throwable ->
            mVerifyProgressBar.postValue(false)
            mWalletRepository.setVerifiablePresentationResult(false)
            mLaunchVerificationResultFragment.postValue(true)
        }) {
            mVerifyProgressBar.postValue(true)
            mWalletRepository.setVerifiablePresentationResult(false)
            val groupList = filterDataGroup(mRequireVerifiableCredentialList.value ?: return@launch)
            val vpDataList = getVPDataList(ArrayList(), groupList, 0)
            val vpToken = mWalletRepository.getParseVerifiablePresentation()?.data?.requestToken ?: ""
            val customData = getCustomField(mVPCustomFieldList.value)
            mWalletRepository.getWallet()?.also { wallet ->
                mVerifiableManager.verifyVerifiablePresentation(wallet, vpToken, vpDataList, customData)
                val time = System.currentTimeMillis()
                val fieldSet = mutableSetOf<String>()
                val vcIdSet = mutableSetOf<String>()
                val vcNameSet = mutableSetOf<String>()
                // 憑證紀錄
                withContext(Dispatchers.Default) {
                    groupList.forEach { group ->
                        group.cardList.forEach card@{ card ->
                            if (card.verifiableCredential == null) {
                                return@card
                            }
                            vcIdSet.add(card.verifiableCredential.uid.toString())
                            vcNameSet.add(card.verifiableCredential.display)
                            val requireFields = StringBuilder()
                            card.fields.forEach { field ->
                                fieldSet.add(field.title)
                                requireFields.append(field.title)
                                requireFields.append("、")
                            }
                            val fields = requireFields.toString().takeIf { it.last().toString() == "、" }?.dropLast(1) ?: requireFields.toString()
                            val credentialRecord = CredentialRecord(
                                walletId = wallet.uid,
                                vcId = card.verifiableCredential.uid,
                                text = mVerifiablePresentationName.value ?: "",
                                authorizationUnit = mVerifiablePresentationUnit.value ?: "",
                                authorizationPurpose = mVerifiablePresentationPurpose.value ?: "",
                                authorizationField = fields,
                                datetime = time
                            )
                            mDatabase.credentialRecordDao().insert(credentialRecord)
                        }
                    }
                }
                val customDataSet = mVPCustomFieldList.value?.map { it.cname }?.toSet() ?: emptySet()
                // 授權紀錄
                withContext(Dispatchers.Default) {
                    val fields = mutableSetOf<String>().apply {
                        addAll(fieldSet)
                        addAll(customDataSet)
                    }.joinToString("、")
                    val vcNames = vcNameSet.joinToString("、")
                    val vcIds = vcIdSet.joinToString(",")
                    val presentationRecord = PresentationRecord(
                        walletId = wallet.uid,
                        text = mVerifiablePresentationName.value ?: "",
                        authorizationUnit = mVerifiablePresentationUnit.value ?: "",
                        authorizationPurpose = mVerifiablePresentationPurpose.value ?: "",
                        authorizationFields = fields,
                        vcIds = vcIds,
                        vcNames = vcNames,
                        datetime = System.currentTimeMillis()
                    )
                    mDatabase.presentationRecordDao().insert(presentationRecord)
                }
                mWalletRepository.setVerifiablePresentationResult(true)
                val result = VerifiablePresentationResultData(
                    resultData = groupList,
                    datetime = time,
                    unit = mVerifiablePresentationUnit.value,
                    customData = mVPCustomFieldList.value
                )
                mWalletRepository.setVerifiablePresentationResultData(result)
            }

            when (mVerifiablePresentationRepository.getVerifiablePresentationEnum()) {
                VerifiablePresentationEnum.NORMAL -> mLaunchVerificationResultFragment.postValue(true)
                VerifiablePresentationEnum.BARCODE -> generateBarcode()
            }

            mVerifyProgressBar.postValue(false)
        }
    }

    /**
     * 前往同意書頁
     */
    fun launchContractDetailFragment(isShow: Boolean) {
        if (isShow) {
            if (mNetworkManager.isNetworkAvailable().not()) {
                mShowNetworkErrorAlert.postValue(true)
                return
            }
            mLaunchContractDetailFragment.postValue(true)
        } else {
            mLaunchContractDetailFragment.postValue(false)
        }
    }

    fun dismissNetworkErrorDialog() {
        mShowNetworkErrorAlert.postValue(false)
    }

    override fun verifySuccessful(sourceEnum: VerificationSourceEnum) {
        if (sourceEnum == VerificationSourceEnum.VerifiablePresentation) {
            verify()
        }
    }

    private suspend fun generateBarcode() = withContext(Dispatchers.Default) {
        mWalletRepository.getWallet()?.also { wallet ->
            mVerifiablePresentationRepository.getSelectedShowCredential()?.also { selectedCredential ->
                mVerifiablePresentationRepository.getResponseOfDwVerifierMgr401i()?.also { response ->
                    val request = HashMap<String, String>()
                    request["transactionId"] = response.transactionId

                    val typeToken = object : TypeToken<DwVerifierMgr402i.Response>() {}
                    val responseOfDwVerifierMgr402i = mIdentifierManager.dwsdkModa201i(selectedCredential.verifierModuleUrl + DwVerifierMgr402i.URL_PATH, request, wallet.did, typeToken)
                    mVerifiablePresentationRepository.setResponseOfDwVerifierMgr402i(responseOfDwVerifierMgr402i?.data)
                    mLaunchShowCredentialBarCodeFragment.postValue(true)
                }
            }
        }
    }

    private suspend fun getVPDataList(listVP: ArrayList<Dwsdk402i.Request.VPData>, listVC: List<RequireVerifiableCredentialGroup>, index: Int): List<Dwsdk402i.Request.VPData> {
        val requireVerifiableCredentialGroup = listVC.getOrNull(index)
        if (requireVerifiableCredentialGroup == null) {
            return listVP
        }

        mWalletRepository.getWallet()?.also { wallet ->
            requireVerifiableCredentialGroup.cardList.forEach { card ->
                if (card.verifiableCredential == null) {
                    return@forEach
                }
                // 驗證VC狀態
                var verifyVC = mVerifiableManager.verifyVerifiableCredential(wallet, card.verifiableCredential)

                if (verifyVC == null || (verifyVC.code != "0" && verifyVC.code != "3")) {
                    // 離線驗證
                    verifyVC = mVerifiableManager.verifyVerifiableCredentialOffline(wallet, card.verifiableCredential)
                }

                // VC狀態驗證成功則使用 dwsdk-301i 或者 dwsdk-302i 的回傳狀態，否則直接帶入已選擇卡片的狀態
                val status = if (verifyVC?.code == "0") {
                    mVerifiableManager.detectVerifiableCredentialStatus(wallet, card.verifiableCredential, verifyVC).status
                } else {
                    card.verifiableCredential.status
                }
                if (status == CardStatusEnum.Valid) {
                    val credential = card.verifiableCredential
                    val jwtVC = credential.credential
                    val fields = getField(card)
                    val cardId = card.cardId
                    val vpData = Dwsdk402i.Request.VPData(jwtVC, fields, cardId)
                    listVP.add(vpData)
                }
            }
        }
        return getVPDataList(listVP, listVC, index + 1)
    }

    private fun getCustomField(customFieldList: List<RequireVerifiablePresentationField>?): String {
        if (customFieldList.isNullOrEmpty()) {
            return ""
        }
        val list = mutableListOf<Dwsdk402i.CustomData>()
        customFieldList.forEach { field ->
            val data = Dwsdk402i.CustomData(
                cname = field.cname,
                ename = field.ename,
                value = field.value,
            )
            list.add(data)
        }
        return mGson.toJson(Dwsdk402i.CustomDataList(list))
    }

    private fun getField(card: RequireVerifiableCredentialCard): Array<String> {
        val fieldList = mutableListOf<String>()
        card.fields.forEach { field ->
            if (field.isCheck) {
                fieldList.add(field.field)
            }
        }
        return fieldList.toTypedArray()
    }

    private fun getShowAuthorizedVCTitle(count: Int, cardListSize: Int): Boolean {
        // 只要卡片數量少於VP要求的話就顯示
        return cardListSize < count
    }

    /**
     * 判斷能不能顯示申請憑證
     */
    private fun getShowAuthorizedVC(requestData: Dwsdk401i.Response.RequestDataGroup): Boolean {
        requestData.cards.forEach { card ->
            if (card.name?.isNotBlank() == true && card.name.startsWith("{")) {
                return true
            }
        }
        return false
    }

    /**
     * 判斷能不能顯示選擇憑證
     */
    private fun getShowChangeVC(rule: String?, count: Int, cardListSize: Int, enoughCount: Int): Boolean {
        if (rule == null || rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
            // 顯示的VC數量有符合VP要的數量
            if (cardListSize >= count) {
                return false
            }
            // 畫面上完全沒VC並且使用者持有的符合VC數量也沒有
            if (cardListSize == 0 && enoughCount == 0) {
                return false
            }
            // 持有的VC數量小於count
            if (enoughCount < count) {
                return false
            }
            // 其餘狀況都要顯示
            return true
        } else {
            // 顯示的VC數量有符合數量
            if (cardListSize >= count) {
                return false
            }
            // 如果目前顯示的數量小於使用者能提供的最大上限
            if (cardListSize != enoughCount) {
                return true
            }
            return false
        }
    }

    /**
     * 取得Group類型
     */
    private fun getGroupType(requestData: Dwsdk401i.Response.RequestDataGroup, nonCardNum: Int): VerifiableCredentialGroupEnum {
        // 缺少全部卡片
        return if (nonCardNum == requestData.cards.size) {
            VerifiableCredentialGroupEnum.Empty
        } else {
            VerifiableCredentialGroupEnum.Default
        }
    }

    /**
     * 針對畫面上有勾的VC重新建立Group給後端
     */
    private fun filterDataGroup(groupList: List<RequireVerifiableCredentialGroup>): List<RequireVerifiableCredentialGroup> {
        val newGroupList = mutableListOf<RequireVerifiableCredentialGroup>()
        groupList.forEach { group ->
            val newCardList = mutableListOf<RequireVerifiableCredentialCard>()
            group.cardList.forEach card@{ card ->
                val newFields = card.fields.filter { it.isCheck }
                if (newFields.isEmpty()) {
                    return@card
                }
                val newCard = card.copy(fields = newFields)
                newCardList.add(newCard)
            }
            if (newCardList.isEmpty()) {
                return@forEach
            }
            val newGroup = group.copy(cardList = newCardList)
            newGroupList.add(newGroup)
        }
        return newGroupList
    }

    /**
     * 更新送出資料按鈕狀態
     */
    private fun updateBtnConfirm(groupList: List<RequireVerifiableCredentialGroup>, customFieldList: List<RequireVerifiablePresentationField>) {
        var isEnable = false
        groupList.forEach { group ->
            // 計算CheckBox打勾的數量
            val groupCount = group.cardList.sumOf { card ->
                card.fields.filter { it.isCheck }.size
            }
            // 檢查能不能點擊送出
            // 一個VP只要有一張VC就可以過，但如果說有 Group 是 ALL 的情況那個的 Group 的 VC 就要全選
            if (group.rule == null || group.rule == VerifiableCredentialGroupRuleEnum.ALL.code) {
                // 計算目前使用者VC選了幾張
                val vcSize = mWalletRepository.getParseVerifiablePresentation()?.data?.requestDataArray?.find { it.group == group.group }?.cards?.size
                if (group.cardList.size != vcSize) {
                    // 如果少選卡後面都不用判斷了
                    mIsCheckField.postValue(false)
                    return
                }
                // 計算有沒有少選欄位
                val groupSize = group.cardList.sumOf { it.fields.size }
                if (groupCount == groupSize) {
                    isEnable = true
                } else {
                    // ALL 如果有沒勾的後面都不用判斷了
                    mIsCheckField.postValue(false)
                    return
                }
            } else {
                // 如果有選卡則更新狀態
                if (groupCount != 0) {
                    isEnable = true
                } else {
                    // 如果沒選卡 先看目前按鈕的狀態
                    // 如果是True 就不改為False 如果為False 則保持False
                    if (!isEnable) {
                        isEnable = false
                    }
                }
            }
        }
        customFieldList.forEach { field ->
            // 沒輸入就不用判斷了
            if (field.value.isEmpty()) {
                mIsCheckField.postValue(false)
                return
            }
        }
        mIsCheckField.postValue(isEnable)
    }
}