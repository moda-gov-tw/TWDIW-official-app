package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.Role;
import gov.moda.dw.manager.repository.RoleRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.service.RoleLogService;
import gov.moda.dw.manager.service.RoleService;
import gov.moda.dw.manager.service.criteria.RoleCriteria;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.RoleLogDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleConferRultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wRoleResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wTreeNodeDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wUpdateResultDTO;
import gov.moda.dw.manager.service.dto.custom.MenuTreeDTO;
import gov.moda.dw.manager.service.mapper.RoleMapper;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.LogType;
import gov.moda.dw.manager.type.RelType;
import gov.moda.dw.manager.type.ResType;
import gov.moda.dw.manager.type.RoleType;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class Ams321wService {

    private final CustomRoleQueryService roleQueryService;
    private final RoleService roleService;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final CustomRelService relService;
    private final MenuTreeService menuTreeService;
    private final RoleLogService roleLogService;

    public Ams321wService(
        CustomRoleQueryService roleQueryService,
        RoleService roleService,
        RoleRepository roleRepository,
        RoleMapper roleMapper,
        CustomRelService relService,
        MenuTreeService menuTreeService,
        RoleLogService roleLogService
    ) {
        this.roleQueryService = roleQueryService;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.relService = relService;
        this.menuTreeService = menuTreeService;
        this.roleLogService = roleLogService;
    }

    /**
     * 查詢角色, 沒有塞入條件就查全部
     *
     * @param ams321ReqDTO
     * @param pageable
     * @return
     */
    public Page<RoleDTO> getAllRole(Ams321wReqDTO ams321ReqDTO, Pageable pageable) {
        log.info("Ams321wService-getAllRole 開始查詢角色 ams321ReqDTO={}", ams321ReqDTO);
        try {
            RoleCriteria roleCriteria = new RoleCriteria();

            if (StringUtils.isNotEmpty(ams321ReqDTO.getCategoryId())) {
                roleCriteria.setRoleId(StringFilterUtils.toContainStringFilter(ams321ReqDTO.getCategoryId()));
            }

            if (StringUtils.isNotEmpty(ams321ReqDTO.getCategoryName())) {
                roleCriteria.setRoleName(StringFilterUtils.toContainStringFilter(ams321ReqDTO.getCategoryName()));
            }

            if (StringUtils.isNotEmpty(ams321ReqDTO.getState())) {
                roleCriteria.setState(StringFilterUtils.toEqualStringFilter(ams321ReqDTO.getState()));
            }

            Page<RoleDTO> findAll = roleQueryService.findByCriteria(roleCriteria, pageable);
            return findAll;
        } catch (Exception ex) {
            log.error("Ams321wService-getAllRole 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 更新角色啟停狀態
     *
     * @param ams321ReqDTO
     * @return
     */
    public Ams321wUpdateResultDTO updateState(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams331wService-updateState 進入角色啟停狀態 Ams321wReqDTO={}", ams321ReqDTO);
        Ams321wUpdateResultDTO ams321WRoleResponseDTO = new Ams321wUpdateResultDTO();
        try {
            //查詢要更改的角色是否存在
            Optional<Role> opRole = roleRepository.findById(ams321ReqDTO.getId());
            RoleDTO roleDTO = null;
            if (opRole.isPresent()) {
                roleDTO = roleMapper.toDto(opRole.get());

                AuthorityAction authorityAction = null;
                // 設置角色啟停狀態, 及設置後續rel表執行動作
                if (StateType.ENABLED.getCode().equals(roleDTO.getState())) {
                    //如為預設或admin角色, 不可停用
                    if (
                        RoleType.DEFAULT_ROLE.getCode().equals(roleDTO.getRoleId()) ||
                        RoleType.ISSUER_ADMIN.getCode().equals(roleDTO.getRoleId()) ||
                        RoleType.VERIFY_ADMIN.getCode().equals(roleDTO.getRoleId())
                    ) {
                        log.info("Ams331wService-setState 預設角色和admin不可停用 roleDTO={}", roleDTO);
                        ams321WRoleResponseDTO.setStatusCode(StatusCode.ROLE_DEFAULT_ROLE_AND_ADMIN_CANNOT_BE_DISABLED);
                        return ams321WRoleResponseDTO;
                    }

                    roleDTO.setState(StateType.DISABLED.getCode());
                    authorityAction = AuthorityAction.ROLE_DISABLED;

                    log.info("Ams331wService-updateState 角色改為停用狀態 res={}", roleDTO.getState());
                } else {
                    roleDTO.setState(StateType.ENABLED.getCode());
                    authorityAction = AuthorityAction.ROLE_ENABLE;

                    log.info("Ams331wService-updateState 角色改為啟用狀態 res={}", roleDTO.getState());
                }
                // 更新角色啟停狀態
                RoleDTO updateResult = roleService.save(roleDTO);

                // rel表執行動作
                // 角色停用的話要刪除帳號權限(User::Role), 但rel表 Role::Res的關係依然保留
                List<ResDTO> resDTOList = relService.roleAction(roleDTO, null, authorityAction);
                if (resDTOList != null) {
                    log.info("Ams321wResource-updateState 更新{}狀態完成", authorityAction);
                    Ams321wRoleResDTO responseRole = this.roleDTOToRoleResDTO(updateResult);
                    ams321WRoleResponseDTO.setAms321wRoleResDTO(responseRole);
                    ams321WRoleResponseDTO.setStatusCode(StatusCode.SUCCESS);

                    //新增異動紀錄
                    this.saveRoleLog(updateResult, LogType.MOD);
                } else {
                    log.warn(
                        "Ams321wResource-updateState 更新{}狀態異常, ams321ReqDTO={}, roleAction response={}",
                        authorityAction,
                        ams321ReqDTO,
                        resDTOList
                    );
                    ams321WRoleResponseDTO.setStatusCode(StatusCode.INVALID_ACTIVATED);
                }
            } else {
                log.warn("Ams321wResource-updateState 查不到角色資料, 無法執行更改啟/停狀態, id={}", ams321ReqDTO.getId());
                ams321WRoleResponseDTO.setStatusCode(StatusCode.ROLE_NOT_EXISTS);
            }

            return ams321WRoleResponseDTO;
        } catch (Exception ex) {
            log.error("Ams321wResource-updateState 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 新增角色
     *
     * @param ams321ReqDTO
     * @return
     */
    public StatusCode createRole(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams321wService-createRole 準備新增角色 ams321ReqDTO={}", ams321ReqDTO);
        try {
            //檢查角色代碼是否重複
            RoleCriteria roleCriteria = new RoleCriteria();
            roleCriteria.setRoleId(StringFilterUtils.toEqualStringFilter(ams321ReqDTO.getCategoryId()));
            List<RoleDTO> roleDTOList = roleQueryService.findByCriteria(roleCriteria);
            if (CollectionUtils.isNotEmpty(roleDTOList)) {
                log.info("Ams321wService-createRole 輸入的角色代碼已存在, 不可新增角色 ams321ReqDTO={}", ams321ReqDTO);
                return StatusCode.ROLE_ROLEID_EXIST;
            }

            //開始新增角色
            RoleDTO roleDTO = this.setToRoleDTO(ams321ReqDTO);
            RoleDTO saveRoleDTO = roleService.save(roleDTO);
            log.info("Ams321wService-createRole 新增角色成功 saveRoleDTO={}", saveRoleDTO);

            //新增異動紀錄
            this.saveRoleLog(saveRoleDTO, LogType.ADD);

            return StatusCode.SUCCESS;
        } catch (Exception ex) {
            log.error("Ams321wService-createRole 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.FAIL;
        }
    }

    /**
     * 修改角色
     *
     * @param ams321ReqDTO
     * @return
     */
    public Ams321wUpdateResultDTO updateRole(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams321wService-updateRole 準備修改角色, ams321ReqDTO={}", ams321ReqDTO);
        Ams321wUpdateResultDTO ams321WRoleResponseDTO = new Ams321wUpdateResultDTO();
        try {
            //開始修改角色
            Optional<Role> opRole = roleRepository.findById(ams321ReqDTO.getId());
            if (opRole.isPresent()) {
                RoleDTO roleDTO = roleMapper.toDto(opRole.get());
                //如為預設或admin角色, 狀態不可更改為停用
                if (
                    (RoleType.DEFAULT_ROLE.getCode().equals(roleDTO.getRoleId()) ||
                        RoleType.ISSUER_ADMIN.getCode().equals(roleDTO.getRoleId()) ||
                        RoleType.VERIFY_ADMIN.getCode().equals(roleDTO.getRoleId())) &&
                    StateType.DISABLED.getCode().equals(ams321ReqDTO.getState())
                ) {
                    log.info("Ams331wService-updateRole 預設角色和admin不可停用 ams321ReqDTO={}", ams321ReqDTO);
                    ams321WRoleResponseDTO.setStatusCode(StatusCode.ROLE_DEFAULT_ROLE_AND_ADMIN_CANNOT_BE_DISABLED);
                    return ams321WRoleResponseDTO;
                }

                // 修改角色
                this.setToUpdateRole(roleDTO, ams321ReqDTO);
                RoleDTO updateResult = roleService.save(roleDTO);

                //整理回傳內容
                Ams321wRoleResDTO responseRole = this.roleDTOToRoleResDTO(updateResult);
                ams321WRoleResponseDTO.setAms321wRoleResDTO(responseRole);
                ams321WRoleResponseDTO.setStatusCode(StatusCode.SUCCESS);
                log.info("Ams321wService-updateRole 修改角色成功 RoleDTO={}", roleDTO);

                //新增異動紀錄
                this.saveRoleLog(roleDTO, LogType.MOD);
            } else {
                log.warn("Ams321wService-updateRole 查不到該角色, 無法執行編輯角色, role table id= {}", ams321ReqDTO.getId());
                ams321WRoleResponseDTO.setStatusCode(StatusCode.ROLE_NOT_EXISTS);
            }

            return ams321WRoleResponseDTO;
        } catch (Exception ex) {
            log.error("Ams321wService-updateRole 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 刪除角色
     *
     * @param ams321ReqDTO
     * @return
     */
    public StatusCode deleteRole(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams331wService-deleteRole 準備刪除角色 Ams321wReqDTO={}", ams321ReqDTO);
        try {
            //查詢要刪除的角色是否存在
            Optional<Role> opRole = roleRepository.findById(ams321ReqDTO.getId());
            if (opRole.isPresent()) {
                RoleDTO roleDTO = roleMapper.toDto(opRole.get());

                // 如為預設角色或admin, 不可刪除
                if (
                    RoleType.DEFAULT_ROLE.getCode().equals(roleDTO.getRoleId()) ||
                    RoleType.ISSUER_ADMIN.getCode().equals(roleDTO.getRoleId()) ||
                    RoleType.VERIFY_ADMIN.getCode().equals(roleDTO.getRoleId())
                ) {
                    log.info("Ams331wService-deleteRole 預設角色和admin不可刪除 roleDTO={}", roleDTO);
                    return StatusCode.ROLE_DEFAULT_ROLE_AND_ADMIN_CANNOT_BE_DELETE;
                }

                //刪除角色有關權限
                List<ResDTO> resDTOList = relService.roleAction(roleDTO, null, AuthorityAction.ROLE_DELETE);
                if (resDTOList != null) {
                    //刪除角色
                    roleRepository.deleteById(roleDTO.getId());
                    log.info(
                        "Ams321wService-deleteRole 刪除角色完成 roleDTO[id= {}, roleId={}], ams321ReqDTO[id= {}]",
                        roleDTO.getId(),
                        roleDTO.getRoleId(),
                        ams321ReqDTO.getId()
                    );

                    //新增異動紀錄
                    this.saveRoleLog(roleDTO, LogType.DEL);

                    return StatusCode.SUCCESS;
                } else {
                    log.warn(
                        "Ams321wService-deleteRole 刪除角色, 執行rel動作發生錯誤, roleDTO={}, AuthorityAction={}",
                        roleDTO,
                        AuthorityAction.ROLE_DELETE
                    );

                    return StatusCode.ROLE_DELETE_ERROR;
                }
            } else {
                log.warn("Ams321wService-deleteRole 查不到該角色, 無法執行刪除角色, role table id= {}", ams321ReqDTO.getId());
                return StatusCode.ROLE_NOT_EXISTS;
            }
        } catch (Exception ex) {
            log.error("Ams321wService-deleteRole 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.FAIL;
        }
    }

    /**
     * 儲存角色授權功能
     *
     * @param ams321wRelDTO
     * @return
     */
    public Ams321wRoleConferRultDTO saveConferRes(Ams321wRoleConferReqDTO ams321wRelDTO) {
        log.info("Ams321wService-saveConferRes 開始儲存角色授權功能 ams321wRelDTO={}", ams321wRelDTO);
        Ams321wRoleConferRultDTO ams321wRoleConferResDTO = new Ams321wRoleConferRultDTO();
        try {
            //檢查必填欄位
            if (
                ams321wRelDTO == null ||
                ams321wRelDTO.getId() == null ||
                ams321wRelDTO.getResList() == null ||
                ams321wRelDTO.getCategoryId() == null
            ) {
                ams321wRoleConferResDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                log.warn("Ams321wService-saveConferRes 缺少必要參數 ams321wRelDTO={}", ams321wRelDTO);
                return ams321wRoleConferResDTO;
            }

            //確認角色是否存在及是否為啟用狀態
            Optional<Role> opRole = roleRepository.findById(ams321wRelDTO.getId());
            if (opRole.isPresent()) {
                RoleDTO role = roleMapper.toDto(opRole.get());
                if (StateType.DISABLED.getCode().equals(role.getState())) {
                    log.warn("Ams321wService-saveConferRes 此角色已停用,無法賦予權限 role={}", role);
                    ams321wRoleConferResDTO.setStatusCode(StatusCode.ROLE_IS_DISABLE);
                    return ams321wRoleConferResDTO;
                }

                List<ResDTO> resDTOList = relService.roleAction(role, ams321wRelDTO.getResList(), AuthorityAction.ROLE_CONFER_RES);
                if (resDTOList != null) {
                    //新增異動紀錄
                    role.setAuthChangeTime(Instant.now());
                    this.saveRoleLog(role, LogType.MOD);

                    // role 紀錄變更授權時間
                    roleService.save(role);

                    List<Ams321wRoleConferResDTO> ams321wRoleResResponseDTOList = this.resDTOtoConferResList(resDTOList);
                    ams321wRoleConferResDTO.setAms321wRoleResResponseDTOList(ams321wRoleResResponseDTOList);
                    ams321wRoleConferResDTO.setStatusCode(StatusCode.SUCCESS);
                    log.info("Ams321wService-saveConferRes 角色賦予權限完成 共計{}筆, roleId={}", resDTOList.size(), role.getRoleId());
                } else {
                    log.warn(
                        "Ams321wService-saveConferRes 角色賦予權限失敗, 執行rel動作發生錯誤, roleDTO={}, AuthorityAction={}",
                        role,
                        AuthorityAction.ROLE_CONFER_RES
                    );
                    ams321wRoleConferResDTO.setStatusCode(StatusCode.ROLE_CONFER_RES_ERROR);
                }
            } else {
                log.warn("Ams321wService-saveConferRes 查不到該角色, 無法設定授權功能, role table id= {}", ams321wRelDTO.getId());
                ams321wRoleConferResDTO.setStatusCode(StatusCode.ROLE_NOT_EXISTS);
            }

            return ams321wRoleConferResDTO;
        } catch (Exception ex) {
            log.error("Ams321wService-saveConferRes 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 取得角色已賦予權限
     *
     * @param ams321ReqDTO
     * @return
     */
    public Ams321wRoleConferRultDTO searchRoleConferRes(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams321wService-searchRoleConferRes 查詢角色已授權功能");
        Ams321wRoleConferRultDTO ams321wRoleConferResDTO = new Ams321wRoleConferRultDTO();
        try {
            Optional<Role> opRole = roleRepository.findById(ams321ReqDTO.getId());
            if (opRole.isPresent()) {
                RoleDTO roleDTO = roleMapper.toDto(opRole.get());
                List<ResDTO> resDTOList = relService.roleAction(roleDTO, null, AuthorityAction.ROLE_SEARCH_RES);
                if (resDTOList != null) {
                    List<Ams321wRoleConferResDTO> conferResList = this.resDTOtoConferResList(resDTOList);
                    ams321wRoleConferResDTO.setAms321wRoleResResponseDTOList(conferResList);
                    ams321wRoleConferResDTO.setStatusCode(StatusCode.SUCCESS);
                    log.info(
                        "Ams321wService-searchRoleConferRes 取得角色已賦予權限完成 共計{}筆, roleId={}",
                        resDTOList.size(),
                        roleDTO.getRoleId()
                    );
                } else {
                    log.warn(
                        "Ams321wService-searchRoleConferRes 取得角色已賦予權限失敗, 執行rel動作發生錯誤, roleDTO={}, AuthorityAction={}",
                        roleDTO,
                        AuthorityAction.ROLE_SEARCH_RES
                    );
                    ams321wRoleConferResDTO.setStatusCode(StatusCode.ROLE_SEARCH_RES_ERROR);
                }
            } else {
                log.error(
                    "Ams321wService-searchRoleConferRes 查不到該角色, 無法取得該角色已賦予權限, role table id= {}",
                    ams321ReqDTO.getId()
                );
                ams321wRoleConferResDTO.setStatusCode(StatusCode.ROLE_NOT_EXISTS);
            }

            return ams321wRoleConferResDTO;
        } catch (Exception ex) {
            log.error("Ams321wService-searchRoleConferRes 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 取得功能授權左樹狀圖顯示內容
     *
     * @param ams321ReqDTO
     */
    public Ams321wTreeNodeDTO getTree(Ams321wReqDTO ams321ReqDTO) {
        log.info("Ams321wService-getTree 準備取得角色功能授權樹結構內容 ams321ReqDTO={}", ams321ReqDTO);
        Ams321wTreeNodeDTO ams321wTreeNodeDTO = new Ams321wTreeNodeDTO();

        try {
            //取得結構
            MenuTreeDTO menuTree = menuTreeService.readTree(ams321ReqDTO.getId(), RelType.ROLETORES, ResType.WEB);
            if (menuTree != null) {
                ams321wTreeNodeDTO.setMenuTreeDTO(menuTree);
                ams321wTreeNodeDTO.setStatusCode(StatusCode.SUCCESS);
                log.info(
                    "Ams321wService-getTree 成功取得角色權限樹結構, roleTableId={}, RelType={}, ResType={}",
                    ams321ReqDTO.getId(),
                    RelType.ROLETORES,
                    ResType.WEB.getCode()
                );
            } else {
                log.error(
                    "Ams321wService-getTree 取得角色權限樹結構失敗, roleTableId={}, RelType={}, ResType={}",
                    ams321ReqDTO.getId(),
                    RelType.ROLETORES,
                    ResType.WEB.getCode()
                );
                ams321wTreeNodeDTO.setStatusCode(StatusCode.ROLE_GET_TREE_ERROR);
            }

            return ams321wTreeNodeDTO;
        } catch (Exception ex) {
            log.error("Ams321wService-getTree 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    private void setToUpdateRole(RoleDTO roleDTO, Ams321wReqDTO ams321ReqDTO) {
        roleDTO.setRoleName(ams321ReqDTO.getCategoryName());
        roleDTO.setDescription(ams321ReqDTO.getDescription());
        roleDTO.setState(ams321ReqDTO.getState());
    }

    private RoleDTO setToRoleDTO(Ams321wReqDTO ams321wReqDTO) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleId(ams321wReqDTO.getCategoryId());
        roleDTO.setRoleName(ams321wReqDTO.getCategoryName());
        if (StringUtils.isNotEmpty(ams321wReqDTO.getDescription())) {
            roleDTO.setDescription(ams321wReqDTO.getDescription());
        }
        roleDTO.setState(ams321wReqDTO.getState());
        roleDTO.setCreateTime(Instant.now());

        return roleDTO;
    }

    /**
     * 新增角色異動紀錄
     * @param roleDTO
     * @param logType
     */
    private void saveRoleLog(RoleDTO roleDTO, LogType logType) {
        log.info("Ams321wService-saveRoleLog 開始紀錄角色{}異動 RoleDTO={} LogType={}", logType.getName(), roleDTO, logType);
        RoleLogDTO roleLogDTO = this.toRoleLogDTO(roleDTO, logType.getCode());
        roleLogService.save(roleLogDTO);
        log.info("Ams321wService-saveRoleLog 角色{}異動紀錄完成 RoleDTO={} LogType={}", logType.getName(), roleDTO, logType);
    }

    private RoleLogDTO toRoleLogDTO(RoleDTO roleDTO, String logTypeCode) {
        RoleLogDTO roleLogDTO = new RoleLogDTO();
        roleLogDTO.setActor(SecurityUtils.getCurrentUserLogin().get());
        roleLogDTO.setLogType(logTypeCode);
        roleLogDTO.setLogTime(Instant.now());
        roleLogDTO.setRoleId(roleDTO.getRoleId());
        roleLogDTO.setRoleName(roleDTO.getRoleName());
        roleLogDTO.setDescription(roleDTO.getDescription());
        roleLogDTO.setState(roleDTO.getState());
        roleLogDTO.setDataRole1(roleDTO.getDataRole1());
        roleLogDTO.setDataRole2(roleDTO.getDataRole2());
        roleLogDTO.setCreateTime(roleDTO.getCreateTime());
        roleLogDTO.setAuthChangeTime(roleDTO.getAuthChangeTime());

        return roleLogDTO;
    }

    private List<Ams321wRoleConferResDTO> resDTOtoConferResList(List<ResDTO> resDTOList) {
        List<Ams321wRoleConferResDTO> ams321wRoleResResponseDTOList = new ArrayList<>();
        for (ResDTO resDTO : resDTOList) {
            Ams321wRoleConferResDTO ams321wRoleResResponseDTO = new Ams321wRoleConferResDTO();
            ams321wRoleResResponseDTO.setId(resDTO.getId());
            ams321wRoleResResponseDTO.setResId(resDTO.getResId());
            ams321wRoleResResponseDTO.setResName(resDTO.getResName());

            ams321wRoleResResponseDTOList.add(ams321wRoleResResponseDTO);
        }

        return ams321wRoleResResponseDTOList;
    }

    /**
     * 設定角色前端回傳的所有顯示欄位
     * @param roleDTO
     * @return
     */
    private Ams321wRoleResDTO roleDTOToRoleResDTO(RoleDTO roleDTO) {
        Ams321wRoleResDTO ams321wSearchAllResDTO = new Ams321wRoleResDTO();
        ams321wSearchAllResDTO.setId(roleDTO.getId());
        ams321wSearchAllResDTO.setRoleId(roleDTO.getRoleId());
        ams321wSearchAllResDTO.setRoleName(roleDTO.getRoleName());
        ams321wSearchAllResDTO.setDescription(roleDTO.getDescription());
        ams321wSearchAllResDTO.setState(roleDTO.getState());
        ams321wSearchAllResDTO.setCreateTime(roleDTO.getCreateTime());

        return ams321wSearchAllResDTO;
    }

    public Page<RoleDTO> searchRoles(RoleCriteria criteria, Pageable pageable) {
        return roleQueryService.findByCriteria(criteria, pageable);
    }
}
