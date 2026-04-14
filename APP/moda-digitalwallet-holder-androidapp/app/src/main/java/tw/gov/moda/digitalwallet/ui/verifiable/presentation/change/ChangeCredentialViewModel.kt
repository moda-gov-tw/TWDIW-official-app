package tw.gov.moda.digitalwallet.ui.verifiable.presentation.change

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import tw.gov.moda.digitalwallet.core.repository.verifiable.VerifiablePresentationRepository
import tw.gov.moda.digitalwallet.core.resource.ResourceProvider
import tw.gov.moda.digitalwallet.data.element.SelectVerifiableCredentialStatusEnum
import tw.gov.moda.digitalwallet.data.model.MultiChangeVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.ChangeVerifiableCredentialCardGroup
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialField
import tw.gov.moda.digitalwallet.ui.base.BaseViewModel
import tw.gov.moda.diw.R
import javax.inject.Inject

@HiltViewModel
class ChangeCredentialViewModel @Inject constructor(
    private val mVerifiablePresentationRepository: VerifiablePresentationRepository,
    private val mResourceProvider: ResourceProvider
) : BaseViewModel() {

    private val mVerifiablePresentationCardList = MutableLiveData<List<ChangeVerifiableCredentialCardGroup>>()
    val verifiablePresentationCardList: LiveData<List<ChangeVerifiableCredentialCardGroup>> get() = mVerifiablePresentationCardList
    private val mSelectCountTitle = MutableLiveData<String>()
    val selectCountTitle: LiveData<String> get() = mSelectCountTitle
    private val mSelectCountMax = MutableLiveData<String>()
    val selectCountMax: LiveData<String> get() = mSelectCountMax
    private val mIsHiddenContent = MutableLiveData(false)
    val isHiddenContent: LiveData<Boolean> get() = mIsHiddenContent
    private val mIsExpandAll = MutableLiveData<Boolean>(false)
    val isExpandAll: LiveData<Boolean> get() = mIsExpandAll


    fun getData() {
        val group = mVerifiablePresentationRepository.getVerifiablePresentationGroup() ?: return
        // 前一頁畫面上全部VC
        val currentCardList = mVerifiablePresentationRepository.getCurrVerifiablePresentationCardList() ?: return

        val max = group.count
        val currCount = currentCardList.size
        mSelectCountTitle.postValue(mResourceProvider.getString(R.string.credentials_select_count).format(currCount))
        mSelectCountMax.postValue(mResourceProvider.getString(R.string.credentials_select_max).format(max))

        val list = group.cardList
            .filter { it.isValid }
            .groupBy { it.card }
            .toList()
            .mapIndexed { index, (card, items) ->
                val cardList = items.map { item ->
                    val fieldList = item.fields.map { fields ->
                        VerifiableCredentialField(title = fields.title, field = fields.field, value = fields.value)
                    }
                    val uid = item.verifiableCredential?.uid ?: 0L
                    val isCheck = getIsChecked(item, currentCardList)
                    MultiChangeVerifiableCredentialCard(uid = uid, isChecked = isCheck, fieldList = fieldList)
                }
                val groupClickStatus = if (cardList.any { it.isChecked }) {
                    SelectVerifiableCredentialStatusEnum.ReadyToClick
                } else {
                    if (currentCardList.size < max) {
                        SelectVerifiableCredentialStatusEnum.ReadyToClick
                    } else {
                        SelectVerifiableCredentialStatusEnum.NotClickable
                    }
                }
                ChangeVerifiableCredentialCardGroup(
                    id = index,
                    card = card,
                    title = items[0].title,
                    issuer = items[0].issuer,
                    isExpand = false,
                    isHidden = false,
                    clickStatus = groupClickStatus,
                    cardList = cardList
                )
            }
        mVerifiablePresentationCardList.postValue(list)
    }

    fun setSelectCard(index: Int, uid: Long, isCheck: Boolean) {
        val groupList = mVerifiablePresentationCardList.value?.toMutableList() ?: return
        // 找出卡片
        val targetCardList = groupList[index].cardList.toMutableList()
        val targetCard = targetCardList.withIndex().find { it.value.uid == uid } ?: return
        // 無法點擊狀態直接出去
        if (groupList[index].clickStatus == SelectVerifiableCredentialStatusEnum.NotClickable) {
            return
        }
        if (isCheck) {
            // step1. 找出有被點擊的卡片 並且是可點擊的
            val oldCard = targetCardList.withIndex().firstOrNull { it.value.isChecked }
            // step2. 如果有被點擊的卡片，更新那張卡
            oldCard?.index?.let {
                targetCardList[it] = targetCardList[it].copy(isChecked = false)
            }
            // step3. 更新現在的卡片
            targetCardList[targetCard.index] = targetCardList[targetCard.index].copy(isChecked = true)
            groupList[index].cardList = targetCardList
            // step4-1. 找出目前有點擊的卡片
            val sum = groupList.flatMap { it.cardList }.count { it.isChecked }
            // step4-2. 檢查點擊的數量有沒有符合VP要求
            val group = mVerifiablePresentationRepository.getVerifiablePresentationGroup() ?: return
            if (sum >= group.count) {
                // step4-2. 把其他卡片設為無法點擊
                groupList.forEachIndexed { groupIndex, group ->
                    if (index != groupIndex && group.cardList.any { it.isChecked }.not()) {
                        groupList[groupIndex] = groupList[groupIndex].copy(clickStatus = SelectVerifiableCredentialStatusEnum.NotClickable)
                    }
                }
            }
        } else {
            // step1. 更改狀態
            targetCardList[targetCard.index] = targetCardList[targetCard.index].copy(isChecked = false)
            groupList[index].cardList = targetCardList
            // step2. 更改其他卡片狀態
            groupList.forEachIndexed { groupIndex, group ->
                if (group.clickStatus == SelectVerifiableCredentialStatusEnum.NotClickable) {
                    groupList[groupIndex] = groupList[groupIndex].copy(clickStatus = SelectVerifiableCredentialStatusEnum.ReadyToClick)
                }
            }
        }
        val max = mVerifiablePresentationRepository.getVerifiablePresentationGroup()?.count ?: return
        val currCount = groupList.flatMap { it.cardList }.count { it.isChecked }
        // 更新畫面
        mSelectCountTitle.postValue(mResourceProvider.getString(R.string.credentials_select_count).format(currCount))
        mVerifiablePresentationCardList.postValue(groupList)
    }

    fun setSelectCardList() {
        val group = mVerifiablePresentationRepository.getVerifiablePresentationGroup() ?: return
        val cardList = mVerifiablePresentationCardList.value?.flatMap { it.cardList }?.filter { it.isChecked }
        val newList = group.cardList.filter { card ->
            cardList?.any { it.uid == card.verifiableCredential?.uid && it.isChecked } == true
        }
        val newGroup = group.copy(cardList = newList)
        mVerifiablePresentationRepository.setNewVerifiablePresentationGroup(newGroup)
    }

    fun toggleExpandContent(index: Int, isExpand: Boolean) {
        val cardList = mVerifiablePresentationCardList.value?.toMutableList() ?: return
        cardList[index].isExpand = isExpand
        // 可以點選的卡片全部展開的狀態下就顯示收合全部
        val isExpandAll = cardList
            .filter { it.clickStatus == SelectVerifiableCredentialStatusEnum.ReadyToClick }
            .all { it.isExpand }
        mIsExpandAll.postValue(isExpandAll)
        mVerifiablePresentationCardList.postValue(cardList)
    }

    fun toggleExpandAllList() {
        val isExpand = mIsExpandAll.value?.not() ?: return
        val cardList = mVerifiablePresentationCardList.value?.toMutableList() ?: return
        cardList.forEach {
            it.isExpand = isExpand
        }
        mIsExpandAll.postValue(isExpand)
        mVerifiablePresentationCardList.postValue(cardList)
    }

    fun toggleHideContent() {
        val isShow = mIsHiddenContent.value?.not() ?: false
        mIsHiddenContent.postValue(isShow)
    }

    private fun getIsChecked(card: RequireVerifiableCredentialCard, currList: List<RequireVerifiableCredentialCard>): Boolean {
        return currList.any { it.card == card.card && it.verifiableCredential?.uid == card.verifiableCredential?.uid }
    }
}