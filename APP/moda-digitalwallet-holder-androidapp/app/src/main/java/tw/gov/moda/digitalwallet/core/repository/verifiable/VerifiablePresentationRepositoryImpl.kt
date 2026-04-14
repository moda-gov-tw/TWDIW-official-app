package tw.gov.moda.digitalwallet.core.repository.verifiable

import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialGroup
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr402i

class VerifiablePresentationRepositoryImpl : VerifiablePresentationRepository {

    private var mContractTitle: String? = null
    private var mContractContent: String? = null
    private var mGroup: RequireVerifiableCredentialGroup? = null
    private var mCardList: List<RequireVerifiableCredentialCard>? = null
    private var mNewGroup: RequireVerifiableCredentialGroup? = null
    private var mAuthorizedCredentialList: List<RequireVerifiableCredentialCard>? = null
    private var mShowCredentialList: List<DwModa401i.Response.VPItem>? = null
    private var mVerifiablePresentationEnum = VerifiablePresentationEnum.NORMAL
    private var mResponseOfDwVerifierMgr401i: DwVerifierMgr401i.Response? = null
    private var mResponseOfDwVerifierMgr402i: DwVerifierMgr402i.Response? = null
    private var mSelectedShowCredential: DwModa401i.Response.VPItem? = null
    private var mIsRetryGenerateBarcode = false
    private var mSelectVerifiableCredentialCard: RequireVerifiableCredentialCard? = null

    override fun setContractTitle(title: String) {
        mContractTitle = title
    }

    override fun getContractTitle(): String? {
        return mContractTitle
    }

    override fun setContractContent(content: String) {
        mContractContent = content
    }

    override fun getContractContent(): String? {
        return mContractContent
    }

    override fun setVerifiablePresentationGroup(group: RequireVerifiableCredentialGroup) {
        mGroup = group
    }

    override fun getVerifiablePresentationGroup(): RequireVerifiableCredentialGroup? {
        return mGroup
    }

    override fun setCurrVerifiablePresentationCardList(cardList: List<RequireVerifiableCredentialCard>) {
        mCardList = cardList
    }

    override fun getCurrVerifiablePresentationCardList(): List<RequireVerifiableCredentialCard>? {
        return mCardList
    }

    override fun setSelectVerifiablePresentationCard(card: RequireVerifiableCredentialCard?) {
        mSelectVerifiableCredentialCard = card
    }

    override fun getSelectVerifiablePresentationCard(): RequireVerifiableCredentialCard? {
        return mSelectVerifiableCredentialCard
    }

    override fun setNewVerifiablePresentationGroup(group: RequireVerifiableCredentialGroup?) {
        mNewGroup = group
    }

    override fun getNewVerifiablePresentationGroup(): RequireVerifiableCredentialGroup? {
        return mNewGroup
    }

    override fun setAuthorizedCredentialList(list: List<RequireVerifiableCredentialCard>) {
        mAuthorizedCredentialList = list
    }

    override fun getAuthorizedCredentialList(): List<RequireVerifiableCredentialCard>? {
        return mAuthorizedCredentialList
    }

    override fun setShowCredentialList(list: List<DwModa401i.Response.VPItem>) {
        mShowCredentialList = list
    }

    override fun getShowCredentialList(): List<DwModa401i.Response.VPItem>? {
        return mShowCredentialList
    }

    override fun setVerifiablePresentationEnum(type: VerifiablePresentationEnum) {
        this.mVerifiablePresentationEnum = type
    }

    override fun getVerifiablePresentationEnum(): VerifiablePresentationEnum {
        return this.mVerifiablePresentationEnum
    }

    override fun setResponseOfDwVerifierMgr401i(data: DwVerifierMgr401i.Response?) {
        this.mResponseOfDwVerifierMgr401i = data
    }

    override fun getResponseOfDwVerifierMgr401i(): DwVerifierMgr401i.Response? {
        return this.mResponseOfDwVerifierMgr401i
    }

    override fun setResponseOfDwVerifierMgr402i(data: DwVerifierMgr402i.Response?) {
        this.mResponseOfDwVerifierMgr402i = data
    }

    override fun getResponseOfDwVerifierMgr402i(): DwVerifierMgr402i.Response? {
        return this.mResponseOfDwVerifierMgr402i
    }

    override fun setSelectedShowCredential(item: DwModa401i.Response.VPItem?) {
        this.mSelectedShowCredential = item
    }

    override fun getSelectedShowCredential(): DwModa401i.Response.VPItem? {
        return this.mSelectedShowCredential
    }

    override fun isRetryGenerateBarcode(isRetry: Boolean) {
        this.mIsRetryGenerateBarcode = isRetry
    }

    override fun isRetryGenerateBarcode(): Boolean {
        return this.mIsRetryGenerateBarcode
    }
}