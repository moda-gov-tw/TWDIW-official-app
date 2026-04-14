package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.domain.Rel;
import gov.moda.dw.manager.domain.Res;
import gov.moda.dw.manager.domain.Role;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.RelRepository;
import gov.moda.dw.manager.repository.custom.CustomRelRepository;
import gov.moda.dw.manager.repository.custom.CustomResRepository;
import gov.moda.dw.manager.repository.custom.CustomRoleRepository;
import gov.moda.dw.manager.service.RelService;
import gov.moda.dw.manager.service.RoleService;
import gov.moda.dw.manager.service.criteria.AuthObjCriteria;
import gov.moda.dw.manager.service.criteria.RelCriteria;
import gov.moda.dw.manager.service.criteria.ResCriteria;
import gov.moda.dw.manager.service.criteria.RoleCriteria;
import gov.moda.dw.manager.service.dto.AuthObjDTO;
import gov.moda.dw.manager.service.dto.RelDTO;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wConferRes;
import gov.moda.dw.manager.service.dto.custom.Ams351wRelReqDTO;
import gov.moda.dw.manager.service.mapper.RelMapper;
import gov.moda.dw.manager.service.mapper.ResMapper;
import gov.moda.dw.manager.service.mapper.RoleMapper;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.RelType;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.RoleType;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.StringFilterUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Service Implementation for managing {@link Rel}.
 */
@Service
@Transactional
public class CustomRelService {

    private final Logger log = LoggerFactory.getLogger(CustomRelService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final RelRepository relRepository;

    private final CustomRelQueryService relQueryService;

    private final RelMapper relMapper;

    private final AuthoritiesService authoritiesService;

    private final SyncUserAuthoritiesService syncUserAuthoritiesService;

    private final CustomResRepository resRepository;

    private final ResMapper resMapper;

    private final CustomRoleQueryService roleQueryService;

    private final RoleService roleService;

    private final CustomRoleRepository roleRepository;

    private final RoleMapper roleMapper;
    private final CustomAuthObjQueryService authObjQueryService;
    private final RelService relService;
    private final CustomResQueryService customResQueryService;
    private final CustomRelRepository customRelRepository;

    public CustomRelService(
        RelRepository relRepository,
        RelMapper relMapper,
        CustomRelQueryService relQueryService,
        AuthoritiesService authoritiesService,
        SyncUserAuthoritiesService syncUserAuthoritiesService,
        CustomResRepository resRepository,
        ResMapper resMapper,
        CustomRoleQueryService roleQueryService,
        RoleService roleService,
        CustomRoleRepository roleRepository,
        RoleMapper roleMapper,
        CustomAuthObjQueryService authObjQueryService,
        RelService relService,
        CustomResQueryService customResQueryService,
        CustomRelRepository customRelRepository
    ) {
        this.relRepository = relRepository;
        this.relMapper = relMapper;
        this.relQueryService = relQueryService;
        this.authoritiesService = authoritiesService;
        this.syncUserAuthoritiesService = syncUserAuthoritiesService;
        this.resRepository = resRepository;
        this.resMapper = resMapper;
        this.roleQueryService = roleQueryService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.authObjQueryService = authObjQueryService;
        this.relService = relService;
        this.customResQueryService = customResQueryService;
        this.customRelRepository = customRelRepository;
    }

    /**
     * 根據角色不同動作, 執行流程
     *
     * @param roleDTO
     * @param newResList
     * @param authorityAction
     * @return
     */
    public List<ResDTO> roleAction(RoleDTO roleDTO, List<Ams321wConferRes> newResList, AuthorityAction authorityAction) {
        log.info(
            "CustomRelService-roleAction 執行功能 rel表動作 AuthorityAction code={} name={}",
            authorityAction.getCode(),
            authorityAction.getName()
        );

        List<ResDTO> result = null;
        switch (authorityAction) {
            case ROLE_DELETE:
                log.info("CustomRelService-roleAction in ROLE_DELETE, ROLE_DISABLED 動作");
                result = this.roleDelete(roleDTO, authorityAction);
                break;
            case ROLE_DISABLED:
            case ROLE_ENABLE:
                log.info("CustomRelService-roleAction in ROLE_ENABLE, ROLE_DISABLED 動作");
                result = this.roleChangeState(roleDTO, authorityAction);
                break;
            case ROLE_CONFER_RES:
                log.info("CustomRelService-roleAction in ROLE_CONFER_RES 動作");
                result = this.roleConferRes(roleDTO, newResList);
                break;
            case ROLE_SEARCH_RES:
                log.info("CustomRelService-roleAction in ROLE_SEARCH_RES 動作");
                result = this.searchRoleRes(roleDTO.getRoleId());
                break;
        }
        return result;
    }

    /**
     * 根據功能不同動作, 執行流程
     *
     * @param resDTO
     * @param authorityAction
     */
    public boolean resAction(ResDTO resDTO, AuthorityAction authorityAction) {
        log.info(
            "CustomRelService-resAction 執行功能 rel表動作 AuthorityAction code={} name={}",
            authorityAction.getCode(),
            authorityAction.getName()
        );
        boolean result = false;
        switch (authorityAction) {
            case RES_ENABLED:
            case RES_DISABLED:
                result = this.changeRelStateFromRes(resDTO.getResId(), authorityAction);
                break;
        }

        return result;
    }

    /**
     * 根據帳號不同動作, 執行流程
     *
     * @param user
     * @param roleDTOList
     * @param authorityAction
     */
    public List<RoleDTO> accountAction(User user, List<RoleDTO> roleDTOList, AuthorityAction authorityAction) {
        log.info(
            "CustomRelService-accountAction 執行帳號與角色 rel表動作 AuthorityAction code={} name={}",
            authorityAction.getCode(),
            authorityAction.getName()
        );

        List<RoleDTO> result = null;
        switch (authorityAction) {
            case ACCOUNT_SET_DEFAULT_ROLE:
                log.info("CustomRelService-accountAction in ACCOUNT_SET_DEFAULT_ROLE動作");
                result = this.setDefaultRole(user);
                if (result == null) {
                    return null;
                }
                entityManager.flush(); //刷新實體管理器, 讓後續同步動作確保是最新資料
                boolean syncOk = syncUserAuthoritiesService.syncUserAuthorities(user.getLogin());
                if (!syncOk) {
                    return null;
                }
                break;
            case ACCOUNT_CONFER_ROLE:
                log.info("CustomRelService-accountAction in ACCOUNT_CONFER_ROLE動作");
                result = this.accountConferRole(user, roleDTOList);

                break;
            case ACCOUNT_SEARCH_ROLE:
                log.info("CustomRelService-accountAction in ACCOUNT_SEARCH_ROLE動作");
                result = this.searchUserRole(user);
                break;
            case ACCOUNT_DELETE_ROLE:
                log.info("CustomRelService-accountAction in ACCOUNT_DELETE_ROLE動作");
                this.deleteUserRole(user);
                break;
        }

        return result;
    }

    /**
     * 根據 AccessToken 不同動作,執行流程.
     *
     * @param actionType
     */
    public StatusCode accessTokenAction(Ams351wRelReqDTO ams351wRelReqDTO, AccessToken accessToken, AuthorityAction actionType) {
        log.info(
            "CustomRelService-accessTokenAction 執行AccessToken與功能 rel表動作 AuthorityAction code={} name={}",
            actionType.getCode(),
            actionType.getName()
        );
        StatusCode result = null;
        switch (actionType) {
            case ACCESS_TOKEN_CREATE:
                log.info("CustomRelService-accessTokenAction in ACCESS_TOKEN_CREATE動作");
                this.accessTokenDeleteRel(accessToken, actionType);
                result = StatusCode.SUCCESS;
                break;
            case ACCESS_TOKEN_UPDATE, ACCESS_TOKEN_CHANGE_ACTIVATED:
                log.info("CustomRelService-accessTokenAction in ACCESS_TOKEN_CREATE動作");
                //        result = this.changeRelStateForAccessToken(accessToken);
                result = StatusCode.SUCCESS;
                break;
            case ACCESS_TOKEN_UPDATE_RES:
                log.info("CustomRelService-accessTokenAction in ACCESS_TOKEN_UPDATE_RES動作");
                this.accessTokenDeleteRel(accessToken, actionType);
                result = this.updateRelForAccessToken(ams351wRelReqDTO, accessToken);
                break;
            case ACCESS_TOKEN_DELETE:
                log.info("CustomRelService-accessTokenAction in ACCESS_TOKEN_DELETE動作");
                this.accessTokenDeleteRel(accessToken, actionType);
                result = StatusCode.SUCCESS;
                break;
        }
        if (result == null) {
            log.error("CustomRelService-accessTokenAction，找不到匹配的actionType，actionType:{}", actionType);
        } else {
            log.info("CustomRelService-accessTokenAction，Rel已完成更新，accessToken:{}", accessToken);
        }
        return result;
    }

    private StatusCode changeRelStateForAccessToken(AccessToken accessToken) {
        List<Rel> relList = this.customRelRepository.findOneByLeftCode(accessToken.getAccessToken());
        List<RelDTO> relDTOList = this.relQueryService.findRelDTOListForAccessTokenStr(accessToken.getAccessToken());
        for (Rel rel : relList) {
            rel.setState(accessToken.getState().equals(StateType.ENABLED.getEnName()) ? "1" : "0");
            this.relRepository.save(rel);
        }
        return StatusCode.SUCCESS;
    }

    private StatusCode updateRelForAccessToken(Ams351wRelReqDTO ams351wRelReqDTO, AccessToken accessToken) {
        // 要確認state
        if (ams351wRelReqDTO.getRelDTOList() != null && !ams351wRelReqDTO.getRelDTOList().isEmpty()) {
            if (StateType.ENABLED.getEnName().equals(accessToken.getState())) {
                for (RelDTO rel : ams351wRelReqDTO.getRelDTOList()) {
                    rel.setLeftTbl("AccessToken");
                    rel.setLeftId(accessToken.getId());
                    rel.setLeftCode(accessToken.getAccessToken());
                    rel.setRightTbl("Res");
                    rel.setState(StateType.ENABLED.getCode());
                    rel.setCreateTime(Instant.now());

                    ResCriteria resCriteria = new ResCriteria();
                    resCriteria.setResId(StringFilterUtils.toEqualStringFilter(rel.getRightCode()));
                    List<ResDTO> resDTOList = customResQueryService.findByCriteria(resCriteria);
                    if (resDTOList.isEmpty()) {
                        continue;
                    } else if (resDTOList.get(0).getState().equals("0")) {
                        continue;
                    }
                    rel.setRightId(resDTOList.get(0).getId());
                    relService.save(rel);
                }
            } else {
                return StatusCode.TOKEN_NOT_ADD_RES_BY_DISABLED;
            }
        }
        return StatusCode.SUCCESS;
    }

    private void accessTokenDeleteRel(AccessToken accessToken, AuthorityAction actionType) {
        if (StateType.DISABLED.getEnName().equals(accessToken.getState())) {
            return;
        }
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftCode(StringFilterUtils.toEqualStringFilter(accessToken.getAccessToken()));
        List<RelDTO> exists = relQueryService.findByCriteria(relCriteria);

        for (RelDTO rel : exists) {
            relService.delete(rel.getId());
        }
    }

    /**
     * 賦予帳號角色權限
     *
     * @param user
     * @param roleDTOList
     * @return
     */
    public List<RoleDTO> accountConferRole(User user, List<RoleDTO> roleDTOList) {
        log.info("CustomRelService-accountConferRole 準備賦予帳號角色權限 賦予角色清單={}", roleDTOList);

        //刪除帳號所有角色
        List<RelDTO> delRoles = this.delUserRoleByUser(user);

        //重新賦予所有角色
        List<RoleDTO> roleDTOS = this.addUserRole(user, roleDTOList);
        if (roleDTOS == null) {
            return null;
        }

        //同步 jhi_user_authority
        entityManager.flush();
        boolean accountConferRoleSyncOk = syncUserAuthoritiesService.syncUserAuthorities(user.getLogin());
        if (!accountConferRoleSyncOk) {
            return null;
        }

        //比較出被刪除的角色
        List<String> delRoleList = new ArrayList<>();
        for (RelDTO delRole : delRoles) {
            //沒有賦予新角色, 直接將所有被刪除的角色存入清單
            if (CollectionUtils.isEmpty(roleDTOS)) {
                delRoleList.add(delRole.getRightCode());
                break;
            }

            boolean addDelList = true;
            for (RoleDTO addRole : roleDTOS) {
                if (delRole.getRightCode().equals(addRole.getRoleId())) {
                    addDelList = false;
                    break;
                }
            }

            if (addDelList) {
                delRoleList.add(delRole.getRightCode());
            }
        }
        log.info("CustomRelService-accountConferRole 被取代要刪除的角色清單={}", delRoleList);

        //找出被刪除角色的所有功能
        List<RelDTO> roleResAllList = new ArrayList<>();
        for (String role : delRoleList) {
            List<RelDTO> roleResList = relQueryService.findByLeftCode(role, null);
            roleResAllList.addAll(roleResList);
        }

        //刪除無使用的 Authority
        this.delAuthority(roleResAllList);

        return roleDTOS;
    }

    /**
     * 取得帳號授予角色
     *
     * @param user
     * @return
     */
    public List<RoleDTO> searchUserRole(User user) {
        log.info("CustomRelService-searchUserRole 開始查詢帳號授權角色資料");

        //rel 從user去查詢, 取得已授權的所有角色 User::Role
        List<RelDTO> relDTOList = relQueryService.findByLeftCodeGetRole(user.getLogin());

        //rel
        List<RoleDTO> responseList = new ArrayList<>();
        for (RelDTO relDTO : relDTOList) {
            Optional<Role> roleOp = roleRepository.findById(relDTO.getRightId());
            if (roleOp.isPresent()) {
                Role role = roleOp.get();

                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setRoleName(role.getRoleName());
                roleDTO.setRoleId(role.getRoleId());
                roleDTO.setDescription(role.getDescription());

                responseList.add(roleDTO);
            } else {
                log.error(
                    "CustomRelService-searchUserRole 查不到該角色資料 role table id ={}, roleId={}",
                    relDTO.getRightId(),
                    relDTO.getRightCode()
                );
            }
        }

        return responseList;
    }

    /**
     * 刪除與帳號關聯的角色紀錄
     * @param user
     */
    private void deleteUserRole(User user) {
        RelCriteria relCriteria = new RelCriteria();
        relCriteria.setLeftTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getLeftTbl()));
        relCriteria.setRightTbl(StringFilterUtils.toEqualStringFilter(RelType.USERTOROLE.getRightTbl()));
        relCriteria.setLeftCode(StringFilterUtils.toEqualStringFilter(user.getLogin()));
        List<RelDTO> exists = relQueryService.findByCriteria(relCriteria);

        for (RelDTO rel : exists) {
            relService.delete(rel.getId());
        }
    }

    /**
     * 儲存帳號授權的角色 User::Role
     *
     * @param user
     * @param roleDTOList
     */
    public List<RoleDTO> addUserRole(User user, List<RoleDTO> roleDTOList) {
        log.info("CustomRelService-addUserRole 開始儲存帳號授權的角色 User::Role roleDTOList={}", roleDTOList);
        List<RoleDTO> roleList = new ArrayList<>();
        boolean addDefaultRole = true;

        for (RoleDTO roleDTO : roleDTOList) {
            Optional<RoleDTO> roleOp = roleService.findOne(roleDTO.getId());
            if (roleOp.isPresent()) {
                RoleDTO role = roleOp.get();
                //狀態為啟用才可以授予角色
                if (StateType.ENABLED.getCode().equals(role.getState())) {
                    RelDTO relDTO = this.saveToRelDTO(user, role);
                    roleList.add(role);
                    log.info("CustomRelService-addUserRole 賦予角色 relDTO={}", relDTO);

                    // 帳號必有預設角色, 此處檢查授權的角色是否含有預設角色, 如果沒有, 後續流程補上
                    if (RoleType.DEFAULT_ROLE.getCode().equals(roleDTO.getRoleId())) {
                        addDefaultRole = false;
                    }
                } else {
                    log.warn("CustomRelService-addUserRole 角色狀態為停用,不可賦予該角色 role={}", roleDTO);
                }
            } else {
                log.warn("CustomRelService-addUserRole Role表找不到該角色, 無法賦予角色, role={}", roleDTO);
                return null;
            }
        }

        // 補上帳號預設角色
        if (addDefaultRole) {
            Optional<Role> roleOp = roleRepository.findByRoleId(RoleType.DEFAULT_ROLE.getCode());
            if (roleOp.isPresent()) {
                RoleDTO roleDTO = roleMapper.toDto(roleOp.get());
                RelDTO relDTO = this.saveToRelDTO(user, roleDTO);
                roleList.add(roleDTO);
                log.info("CustomRelService-addUserRole 賦予角色 relDTO={}", relDTO);
            } else {
                log.warn("CustomRelService-addUserRole Role表找不到預設角色, 無法賦予角色, role={}", RoleType.DEFAULT_ROLE);
                return null;
            }
        }

        return roleList;
    }

    /**
     * 存入預設角色  User::Role
     * 並同步權限到 jhi_user_authority
     *
     * @param user
     */
    public List<RoleDTO> setDefaultRole(User user) {
        log.info("CustomRelService-setDefaultRole 存入預設帳號");
        //找到預設角色的資料, 並將帳號與預設角色存在rel中
        RoleCriteria roleCriteria = new RoleCriteria();
        roleCriteria.setRoleId(StringFilterUtils.toEqualStringFilter(RoleType.DEFAULT_ROLE.getCode()));
        List<RoleDTO> getDefaultRole = roleQueryService.findByCriteria(roleCriteria);
        if (CollectionUtils.isNotEmpty(getDefaultRole)) {
            this.saveToRelDTO(user, getDefaultRole.get(0));
            return getDefaultRole;
        } else {
            log.error("CustomRelService-setDefaultRole 查詢不到該角色資訊,無法設定預設角色 roleId={}", "default_role");
            return null;
        }
    }

    /**
     * 功能啟停時, rel更改狀態, 並同步 jhi_user_authority ; 功能停用時, 刪除 Authority 內的功能代碼
     *
     * @param resId
     * @param authorityAction
     * @return
     */
    public boolean changeRelStateFromRes(String resId, AuthorityAction authorityAction) {
        log.info("CustomRelService-changeRelStateFromRes 開始執行{} rel相關動作", authorityAction);
        boolean changeOk = false;

        String changeState = StateType.DISABLED.getCode();
        if (AuthorityAction.RES_ENABLED == authorityAction) {
            changeState = StateType.ENABLED.getCode();
        }
        log.info("CustomRelService-changeRelStateFromRes changeState={}", changeState);

        //查詢出所有使用該功能的資料 Role::Res or AccessToken::Res
        List<RelDTO> useResList = relQueryService.findByRightCode(resId);
        boolean typeIdIsWeb = true;
        for (RelDTO useRes : useResList) {
            useRes.setState(changeState);
            this.save(useRes);

            // 類型為 AccessToken::Res 的關聯不需繼續執行下去, 跳至下一筆
            if (RelType.ACCESSTOKENTORES.getLeftTbl().equals(useRes.getLeftTbl())) {
                typeIdIsWeb = false;
                continue;
            }

            // 再從這些角色身上取得帳號 User::Role, 同步至 jhi_user_authority, AccessToken 不綁定user所以不用查詢
            List<RelDTO> userList = relQueryService.findByRightCodeGetUser(useRes.getLeftCode());
            entityManager.flush();
            this.syncUserAuthorities(userList);
        }

        //功能停用 移除 Authority
        if (AuthorityAction.RES_DISABLED == authorityAction && typeIdIsWeb) {
            authoritiesService.delAuthority(resId);
        }

        log.info("CustomRelService-changeRelStateFromRes 結束執行{} rel相關動作", authorityAction);
        changeOk = true;

        return changeOk;
    }

    /**
     * 賦予角色功能
     *
     * @param roleDTO
     * @param newResList
     * @return
     */
    public List<ResDTO> roleConferRes(RoleDTO roleDTO, List<Ams321wConferRes> newResList) {
        log.info("CustomRelService-roleConferRes 開始執行賦予角色功能 roleId={}, 功能清單={}", roleDTO.getRoleId(), newResList);

        //刪除 Role::Res 資料
        List<RelDTO> resList = this.delRole(roleDTO.getRoleId());

        //重新賦予角色功能權限 Role::Res 資料
        List<ResDTO> newResResult = this.addRoleRes(newResList, roleDTO.getId(), roleDTO.getRoleId());

        // 找出使用此角色的帳號 User::Role 資料, 同步 jhi_user_authority
        List<RelDTO> userList = relQueryService.findByRightCodeGetUser(roleDTO.getRoleId());
        entityManager.flush();
        boolean isAllOk = this.syncUserAuthorities(userList);

        //刪除無使用的 Authority
        this.delAuthority(resList);

        if (resList != null && newResResult != null && isAllOk) {
            log.info("CustomRelService-roleConferRes 賦予角色功能完成 roleId={}", roleDTO.getRoleId());
            return newResResult;
        } else {
            log.error(
                "CustomRelService-roleConferRes 賦予角色功能發生異常 roleId={}, resList={}, newResResult={},isAllOk={}",
                roleDTO.getRoleId(),
                resList,
                newResResult,
                isAllOk
            );
            return null;
        }
    }

    /**
     * 刪除角色
     *
     * @param roleDTO
     * @param authorityAction
     * @return
     */
    public List<ResDTO> roleDelete(RoleDTO roleDTO, AuthorityAction authorityAction) {
        log.info("CustomRelService-roleDelete 開始執行刪除角色 roleId={}", roleDTO.getRoleId());

        //刪除 User::Role 資料, 刪除與該角色關聯的帳號
        //    List<RelDTO> userList = this.userRoleByRole(roleDTO.getRoleId(), authorityAction);
        List<RelDTO> userList = this.delUserRoleByRole(roleDTO.getRoleId());

        //刪除 Role::Res 資料, 刪除與該角色關聯的功能
        List<RelDTO> resList = this.delRole(roleDTO.getRoleId());

        //同步 jhi_user_authority
        entityManager.flush();
        boolean isAllOk = this.syncUserAuthorities(userList);

        //刪除無使用的 Authority
        this.delAuthority(resList);

        if (userList != null && resList != null && isAllOk) {
            log.info("CustomRelService-roleDelete 刪除角色完成 roleId={}", roleDTO.getRoleId());
            return new ArrayList<>();
        } else {
            log.error(
                "CustomRelService-roleDelete 刪除角色發生異常 roleId={}, resList={}, userList={},isAllOk={}",
                roleDTO.getRoleId(),
                resList,
                userList,
                isAllOk
            );
            return null;
        }
    }

    /**
     * 角色啟停
     * 角色停用的話要刪除帳號權限(User::Role), 但rel表 Role::Res的關係依然保留
     * 角色啟用 不更動任何關聯, 因Role::Res的關係依然保留, 不須重新設定; User::Role依照需求重新關聯於帳號(web帳號管理操作)
     * @param roleDTO
     * @param authorityAction
     * @return
     */
    public List<ResDTO> roleChangeState(RoleDTO roleDTO, AuthorityAction authorityAction) {
        log.info(
            "CustomRelService-roleChangeState 開始執行角色啟停rel相關流程, 角色代碼:{}, 動作:{}",
            roleDTO.getRoleId(),
            authorityAction.getCode()
        );

        List<RelDTO> relData = null;
        List<RelDTO> roleResList = null;
        boolean isAllOk = false;
        // 角色停用流程
        if (AuthorityAction.ROLE_DISABLED == authorityAction) {
            //刪除rel User::Role
            relData = this.delUserRoleByRole(roleDTO.getRoleId());

            //同步 jhi_user_authority
            entityManager.flush();
            isAllOk = this.syncUserAuthorities(relData);

            //刪除無使用的 Authority
            roleResList = relQueryService.findByLeftCode(roleDTO.getRoleId(), null);
            this.delAuthority(roleResList);
        } else {
            // 角色啟用流程
            log.debug("CustomRelService-roleChangeState 角色啟用, 目前無需更新關聯.");
            relData = new ArrayList<>();
            roleResList = new ArrayList<>();
            isAllOk = true;
        }

        if (roleResList != null && relData != null && isAllOk) {
            log.info("CustomRelService-roleChangeState rel表,執行角色啟停所需流程完成 roleId={}", roleDTO.getRoleId());
            return new ArrayList<>();
        } else {
            log.error(
                "CustomRelService-roleChangeState rel表,執行角色啟停所需流程時發生異常 roleId={}, roleResList={}, relData={},isAllOk={}",
                roleDTO.getRoleId(),
                roleResList,
                relData,
                isAllOk
            );
            return null;
        }
    }

    /**
     * 刪除角色在rel原有權限 從user刪除
     * User::Role
     *
     * @param user
     */
    public List<RelDTO> delUserRoleByUser(User user) {
        log.info("CustomRelService-delUserRoleByUser 刪除角色在rel權限 由user刪除 User::Role");
        List<RelDTO> roles = relQueryService.findByLeftCodeGetRole(user.getLogin());
        for (RelDTO rel : roles) {
            this.delete(rel.getId());
        }
        return roles;
    }

    /**
     * 刪除使用該角色的帳號 從role刪除
     * User::Role
     *
     * @param roleId
     * @return
     */
    public List<RelDTO> delUserRoleByRole(String roleId) {
        log.info("CustomRelService-delUserRoleByRole 刪除使用該角色的帳號 由RoleId刪除 User::Role roleId={},", roleId);

        List<RelDTO> userList = relQueryService.findByRightCodeGetUser(roleId);
        for (RelDTO user : userList) {
            this.delete(user.getId());
        }

        return userList;
    }

    /**
     * 角色啟停時, 更改rel狀態 Role::Res
     *
     * @param roleDTO
     * @return
     */
    public List<RelDTO> roleResChangeState(RoleDTO roleDTO, AuthorityAction authorityAction) {
        log.info("CustomRelService-roleEnable 角色啟用 修改rel狀態並同步jhi_user_authority roleDTO={}", roleDTO);

        String changeState = StateType.DISABLED.getCode();
        if (AuthorityAction.ROLE_ENABLE == authorityAction) {
            changeState = StateType.ENABLED.getCode();
        }

        //更改 rel state狀態 Role::Res
        List<RelDTO> getRoleAllRes = relQueryService.findByLeftCode(roleDTO.getRoleId(), null);
        for (RelDTO res : getRoleAllRes) {
            res.setState(changeState);
            this.save(res);
        }

        return getRoleAllRes;
    }

    /**
     * auth_obj(view表)結果同步到 jhi_user_authority
     *
     * @param userList
     */
    public boolean syncUserAuthorities(List<RelDTO> userList) {
        log.info("CustomRelService-syncUserAuthorities 準備同步 jhi_user_authority, userList={}", userList);
        boolean allOk = true;
        for (RelDTO user : userList) {
            boolean isOK = syncUserAuthoritiesService.syncUserAuthorities(user.getLeftCode());
            if (!isOK) {
                allOk = false;
                log.warn("CustomRelService-syncUserAuthorities 同步 jhi_user_authority 有失敗 RelDTO user={}", user);
            }
        }
        return allOk;
    }

    /**
     * 刪除 Authority
     *
     * @param roleResList
     */
    public void delAuthority(List<RelDTO> roleResList) {
        log.info("CustomRelService-delAuthority 準備刪除無使用的Authority");
        for (RelDTO res : roleResList) {
            // 檢查 authObj table 現有 Authority 內容, 如果 Authority 查詢出來是空的, 代表該 Authority 沒有使用, 刪除掉.
            AuthObjCriteria authObjCriteria = this.queryConditions(res.getRightCode());
            List<AuthObjDTO> authList = authObjQueryService.findByCriteria(authObjCriteria);
            if (CollectionUtils.isEmpty(authList)) {
                authoritiesService.delAuthority(res.getRightCode());
            }
        }
    }

    /**
     * 刪除與該角色關聯的功能
     * Role::Res 從role去刪
     * @param roleId
     * @return
     */
    public List<RelDTO> delRole(String roleId) {
        List<RelDTO> resList = relQueryService.findByLeftCode(roleId, null);
        for (RelDTO res : resList) {
            this.delete(res.getId());
            log.debug("CustomRelService-delRole 刪除與該角色關聯的功能 Role::Res , rel table id={}", res.getId());
        }

        return resList;
    }

    /**
     * 賦予角色功能, 排除停用與不存在及TypeId = api的
     *
     * @param newResList
     * @param roleId
     * @param roleCode
     * @return
     */
    public List<ResDTO> addRoleRes(List<Ams321wConferRes> newResList, Long roleId, String roleCode) {
        log.info("CustomRelService-addRoleRes 執行賦予角色功能, roleId={}", roleId);
        //賦予角色功能, 排除停用與不存在及TypeId = api的
        List<ResDTO> resultResDTOList = new ArrayList<>();
        for (Ams321wConferRes newRes : newResList) {
            Optional<Res> opRes = resRepository.findByResId(newRes.getResId());
            if (opRes.isPresent()) {
                Res res = opRes.get();
                if ("0".equals(res.getState()) || !ResType.WEB.getCode().equals(res.getTypeId())) {
                    log.debug("CustomRelService-addRoleRes 功能狀態為不可賦予,跳過不執行, res={}", res);
                    continue;
                }

                RelDTO relDTO = this.toRelDTO(roleId, roleCode, res);
                this.save(relDTO);

                // 回傳顯示已賦予的功能
                resultResDTOList.add(resMapper.toDto(res));
            } else {
                log.warn("CustomRelService-addRoleRes 查詢不到欲賦予的功能, resId={}", newRes.getResId());
            }
        }

        return resultResDTOList;
    }

    /**
     * 查詢角色已授權的功能列 Role::Res
     *
     * @param roleId
     * @return
     */
    public List<ResDTO> searchRoleRes(String roleId) {
        log.info("CustomRelService-searchRoleRes 查詢角色已授權的功能列, roleId={}", roleId);

        //查詢角色已授權的功能列 Role::Res
        List<RelDTO> relList = relQueryService.findByLeftCode(roleId, StateType.ENABLED.getCode());

        //取得授權功能id
        List<Long> resIdList = new ArrayList<>();
        for (RelDTO relDTO : relList) {
            resIdList.add(relDTO.getRightId());
        }

        //從res table 取得功能資訊
        List<Res> resList = resRepository.findAllById(resIdList);
        List<ResDTO> resDTOList = resMapper.toDto(resList);

        return resDTOList;
    }

    /**
     * Save a rel.
     *
     * @param relDTO the entity to save.
     * @return the persisted entity.
     */
    public RelDTO save(RelDTO relDTO) {
        log.debug("Request to save Rel : {}", relDTO);
        Rel rel = relMapper.toEntity(relDTO);
        rel = relRepository.save(rel);
        return relMapper.toDto(rel);
    }

    /**
     * Update a rel.
     *
     * @param relDTO the entity to save.
     * @return the persisted entity.
     */
    public RelDTO update(RelDTO relDTO) {
        log.debug("Request to update Rel : {}", relDTO);
        Rel rel = relMapper.toEntity(relDTO);
        rel = relRepository.save(rel);
        return relMapper.toDto(rel);
    }

    /**
     * Partially update a rel.
     *
     * @param relDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RelDTO> partialUpdate(RelDTO relDTO) {
        log.debug("Request to partially update Rel : {}", relDTO);

        return relRepository
            .findById(relDTO.getId())
            .map(existingRel -> {
                relMapper.partialUpdate(existingRel, relDTO);

                return existingRel;
            })
            .map(relRepository::save)
            .map(relMapper::toDto);
    }

    /**
     * Get one rel by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RelDTO> findOne(Long id) {
        log.debug("Request to get Rel : {}", id);
        return relRepository.findById(id).map(relMapper::toDto);
    }

    /**
     * Delete the rel by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Rel : {}", id);
        relRepository.deleteById(id);
    }

    private RelDTO toRelDTO(Long roleId, String roleCode, Res res) {
        RelDTO result = new RelDTO();
        //左半邊的Role資料
        result.setLeftTbl(RelType.ROLETORES.getLeftTbl());
        result.setLeftId(roleId);
        result.setLeftCode(roleCode);

        //右半邊的Res資料
        result.setRightTbl(RelType.ROLETORES.getRightTbl());
        result.setRightId(res.getId());
        result.setRightCode(res.getResId());

        result.setState(StateType.ENABLED.getCode());
        result.setCreateTime(Instant.now());

        return result;
    }

    private RelDTO saveToRelDTO(User user, RoleDTO roleDTO) {
        RelDTO saveRel = new RelDTO();
        saveRel.setLeftTbl(RelType.USERTOROLE.getLeftTbl());
        saveRel.setLeftId(user.getId());
        saveRel.setRightTbl(RelType.USERTOROLE.getRightTbl());
        saveRel.setRightId(roleDTO.getId());
        saveRel.setLeftCode(user.getLogin());
        saveRel.setRightCode(roleDTO.getRoleId());
        saveRel.setState(StateType.ENABLED.getCode());
        saveRel.setCreateTime(Instant.now());
        this.save(saveRel);

        return saveRel;
    }

    /**
     * AuthObj table 查詢條件設定
     *
     * @param res
     * @return
     */
    private AuthObjCriteria queryConditions(String res) {
        AuthObjCriteria authObjCriteria = new AuthObjCriteria();

        if (StringUtils.isNotEmpty(res)) {
            authObjCriteria.setResCode(StringFilterUtils.toEqualStringFilter(res));
        }

        return authObjCriteria;
    }
}
