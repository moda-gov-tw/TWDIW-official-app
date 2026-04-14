package gov.moda.dw.manager.service.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.domain.VPItem;
import gov.moda.dw.manager.domain.VPItemField;
import gov.moda.dw.manager.domain.VPVerifyResult;
import gov.moda.dw.manager.domain.outside.VcManagerVCItem;
import gov.moda.dw.manager.domain.outside.vdr.vcitem.VCItemVdr;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemFieldRepository;
import gov.moda.dw.manager.repository.custom.CustomVPItemRepository;
import gov.moda.dw.manager.repository.custom.CustomVPVerifyResultRepository;
import gov.moda.dw.manager.repository.outside.VcManagerUserRepository;
import gov.moda.dw.manager.repository.outside.VcManagerVCItemRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.criteria.VPItemCriteria;
import gov.moda.dw.manager.service.criteria.VPItemFieldCriteria;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.type.AuthorityAction;
import lombok.Getter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

@Service
@Transactional
public class CustomVPItemAuthHelperService {

    @Getter
    @Value("${sandbox.privileged-account}")
    String privilegedAccount;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    private String commonAccountOrgId;
    private final CustomVPItemRepository customVPItemRepository;
    private final CustomVPItemFieldRepository customVpItemFieldRepository;
    private final CustomVPVerifyResultRepository customVPVerifyResultRepository;
    private final CustomUserRepository customUserRepository;
    private final VcManagerVCItemRepository vcManagerVCItemRepository;
    private final VcManagerUserRepository vcManagerUserRepository;

    private final CustomVdrHelperService customVdrHelperService;
    private final CustomRelService customRelService;

    public CustomVPItemAuthHelperService(CustomVdrHelperService customVdrHelperService, CustomVPItemRepository customVPItemRepository, CustomVPItemFieldRepository customVpItemFieldRepository, CustomVPVerifyResultRepository customVPVerifyResultRepository, CustomUserRepository customUserRepository, VcManagerVCItemRepository vcManagerVCItemRepository, VcManagerUserRepository vcManagerUserRepository, CustomRelService customRelService) {
        this.customVdrHelperService = customVdrHelperService;
        this.customVPItemRepository = customVPItemRepository;
        this.customVpItemFieldRepository = customVpItemFieldRepository;
        this.customVPVerifyResultRepository = customVPVerifyResultRepository;
        this.customUserRepository = customUserRepository;
        this.vcManagerVCItemRepository = vcManagerVCItemRepository;
        this.vcManagerUserRepository = vcManagerUserRepository;
        this.customRelService = customRelService;
    }

    /**
     *
     * @param login jhi_user表的login => UserName
     * @return
     */
    public Long findUserId(String login){
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }

    /**
     *
     * @return jhi_user表的id => LoginUserId
     */
    public long getLoginUserId(){
        String loginId = null;
        Long userId = 0L;
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if(first.isPresent()){
            loginId = first.get().getUserId();
            userId = findUserId(loginId);
        }
        return userId;
    }

    public String getBusinessId(){
        String orgId = "";
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if(first.isPresent()){
            orgId = first.get().getOrgId();
        }
        return orgId;
    }

    public List<RoleDTO> getRoles(String login){
        User user = customUserRepository.findOneByLogin(login).orElseThrow();
        return customRelService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);
    }

    /**
     * 取得當前帳號在vc_manager對應的userId
     * 
     * @return
     */
    public Long findUserIdFromVcManager() {
        Long userId = 0L;
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if (first.isPresent()) {
            String loginId = first.get().getUserId();
            userId = vcManagerUserRepository.findOneByLogin(loginId).orElseThrow().getId();
        }
        return userId;
    }

    /**
     * VP 清單
     * @param criteria
     * @return
     */
    public VPItemCriteria queryVPFilterByAuth(VPItemCriteria criteria) {
        String loginName = SecurityUtils.getJwtUserObject().get(0).getUserId();

        // admin角色 可以看全部
        if (!isNotAdmin(loginName)) {
            return criteria;
        } else if (isCommonAccount()) { // 一般帳號只能看自己的
            Long loginUserId = findUserId(loginName);
            LongFilter crUserFilter = new LongFilter();
            crUserFilter.setEquals(loginUserId);
            criteria.setCrUser(crUserFilter);
        } else if (isNotAdmin(loginName)) { // 進階帳號只能看到自己組織的
            StringFilter businessIdFilter = new StringFilter();
            businessIdFilter.setEquals(getBusinessId());
            criteria.setBusinessId(businessIdFilter);
        }
        return criteria;
    }

    /**
     * VP Filed
     * @param criteria
     * @return
     */
    public VPItemFieldCriteria queryVPFieldFilterByAuth(VPItemFieldCriteria criteria) {
        // admin角色 可以看全部
        if (checkLoginIsNotAdmin()) {
            StringFilter businessIdFilter = new StringFilter();
            businessIdFilter.setEquals(getBusinessId());
            criteria.setVpBusinessId(businessIdFilter);
        }
        return criteria;
    }

    /**
     * 當前登入者是不是非Admin身份
     * @return
     */
    public boolean checkLoginIsNotAdmin() {
        String loginName = SecurityUtils.getJwtUserObject().get(0).getUserId();
        return isNotAdmin(loginName);
    }

    /**
     * 是不是非Admin身份
     * @param loginName
     * @return
     */
    public boolean isNotAdmin(String loginName) {
        List<RoleDTO> roles = getRoles(loginName);
        return roles.stream().map(RoleDTO::getRoleId).noneMatch(e -> getPrivilegedAccount().equals(e));
    }

    /**
     * 是不是一般帳號
     * @return
     */
    public boolean isCommonAccount() {
        return commonAccountOrgId.equals(getBusinessId());
    }

    /**
     * VC 下拉列表
     * @param vcItemVdrs
     * @return
     */
    public List<VCItemVdr> queryVCFilterByAuth(String taxId, List<VCItemVdr> vcItemVdrs) {
        List<VCItemVdr> result = null;
        // 沙盒模式下 且為 一般帳號 只顯示公開VC
        if (customVdrHelperService.getInSandbox()) {
            Map<String, VcManagerVCItem> exposeVcItem;
            if(isCommonAccount()) {
                exposeVcItem = vcManagerVCItemRepository
                        .findByBusinessIdAndExposeOrCrUser(taxId, true, findUserIdFromVcManager()).stream()
                        .collect(Collectors.toMap(vcItem -> vcItem.getBusinessId() + "_" + vcItem.getSerialNo(), e -> e));
            } else {
                exposeVcItem = vcManagerVCItemRepository
                        .findByBusinessIdAndExposeOrBusinessId(taxId, true, getBusinessId()).stream()
                        .collect(Collectors.toMap(vcItem -> vcItem.getBusinessId() + "_" + vcItem.getSerialNo(), e -> e));
            }
            result = vcItemVdrs.stream().filter(
                    vcItemVdr -> exposeVcItem.containsKey(vcItemVdr.getBusinessId() + "_" + vcItemVdr.getSerialNo()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            result = vcItemVdrs;
        }
        return result;
    }

    public void checkVpItemAuthByVpId(Long vpId, String actionMsg) {
        JwtUserObject jwtUserObject = SecurityUtils.getJwtUserObject().get(0);
        String currentLoginUser = jwtUserObject.getUserId();
        String currentLoginOrgId = jwtUserObject.getOrgId();
        Optional<VPItem> vpByIdOpt = customVPItemRepository.findById(vpId);

        if (vpByIdOpt.isPresent()) {
            // admin角色 可以看全部
            if (!isNotAdmin(currentLoginUser)) {
                return;
            }
            if (isCommonAccount()) {
                if (!vpByIdOpt.get().getCrUser().equals(findUserId(currentLoginUser))) {
                    throw new AccessDeniedException(
                            "You do not have permission to access this resource ( when " + actionMsg + " ). ");
                }
            } else if (isNotAdmin(currentLoginUser) && !vpByIdOpt.get().getBusinessId().equals(currentLoginOrgId)) {
                throw new AccessDeniedException(
                        "You do not have permission to access this resource ( when " + actionMsg + " ). ");
            }
        }
    }

    public void checkVpItemAuthByTxId(String txId, String actionMsg) {
        Optional<VPVerifyResult> verifyResultOpt = customVPVerifyResultRepository.findByTransactionId(txId).stream()
                .findFirst();
        if (verifyResultOpt.isPresent()) {
            checkVpItemAuthByVpId(verifyResultOpt.get().getVpItemId(), actionMsg);
        }
    }

    public void checkVpItemAuthByVpFieldId(long vpFieldId, String actionMsg) {
        Optional<VPItemField> vpItemFieldByIdOpt = customVpItemFieldRepository.findById(vpFieldId);
        if (vpItemFieldByIdOpt.isPresent()){
            checkVpItemAuthByVpId(vpItemFieldByIdOpt.get().getVpItemId(), actionMsg);
        }
    }
}
