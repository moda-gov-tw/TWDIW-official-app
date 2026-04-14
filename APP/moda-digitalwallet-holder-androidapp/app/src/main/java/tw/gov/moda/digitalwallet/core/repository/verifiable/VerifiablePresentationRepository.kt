package tw.gov.moda.digitalwallet.core.repository.verifiable

import tw.gov.moda.digitalwallet.data.db.FavoriteShowCredential
import tw.gov.moda.digitalwallet.data.element.VerifiablePresentationEnum
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialCard
import tw.gov.moda.digitalwallet.data.model.RequireVerifiableCredentialGroup
import tw.gov.moda.digitalwallet.data.model.dwmoda.DwModa401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr401i
import tw.gov.moda.digitalwallet.data.model.dwverifier.DwVerifierMgr402i

interface VerifiablePresentationRepository {

    fun setContractTitle(title: String)

    fun getContractTitle(): String?

    fun setContractContent(content: String)

    fun getContractContent(): String?

    fun setVerifiablePresentationGroup(group: RequireVerifiableCredentialGroup)

    fun getVerifiablePresentationGroup(): RequireVerifiableCredentialGroup?

    fun setCurrVerifiablePresentationCardList(cardList: List<RequireVerifiableCredentialCard>)

    fun getCurrVerifiablePresentationCardList(): List<RequireVerifiableCredentialCard>?

    fun setSelectVerifiablePresentationCard(card: RequireVerifiableCredentialCard?)

    fun getSelectVerifiablePresentationCard(): RequireVerifiableCredentialCard?

    fun setNewVerifiablePresentationGroup(group: RequireVerifiableCredentialGroup?)

    fun getNewVerifiablePresentationGroup(): RequireVerifiableCredentialGroup?

    fun setAuthorizedCredentialList(list: List<RequireVerifiableCredentialCard>)

    fun getAuthorizedCredentialList(): List<RequireVerifiableCredentialCard>?

    fun setShowCredentialList(list: List<DwModa401i.Response.VPItem>)

    fun getShowCredentialList(): List<DwModa401i.Response.VPItem>?

    fun setVerifiablePresentationEnum(type: VerifiablePresentationEnum)

    fun getVerifiablePresentationEnum(): VerifiablePresentationEnum

    fun setResponseOfDwVerifierMgr401i(data: DwVerifierMgr401i.Response?)

    fun getResponseOfDwVerifierMgr401i(): DwVerifierMgr401i.Response?

    fun setResponseOfDwVerifierMgr402i(data: DwVerifierMgr402i.Response?)

    fun getResponseOfDwVerifierMgr402i(): DwVerifierMgr402i.Response?

    fun setSelectedShowCredential(item: DwModa401i.Response.VPItem?)

    fun getSelectedShowCredential(): DwModa401i.Response.VPItem?

    fun isRetryGenerateBarcode(isRetry: Boolean)

    fun isRetryGenerateBarcode(): Boolean
}