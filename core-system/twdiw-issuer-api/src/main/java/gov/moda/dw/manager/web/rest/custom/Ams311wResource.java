package gov.moda.dw.manager.web.rest.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.custom.Ams311wService;
import gov.moda.dw.manager.service.custom.Ams321wService;
import gov.moda.dw.manager.service.custom.CustomOrgService;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wAccountResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wActivateReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wDetailResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wSearchAllRoleReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wUpdateRolesReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wUserRolesDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams321wReqDTO;
import gov.moda.dw.manager.service.dto.custom.JwtUserDTO;
import gov.moda.dw.manager.service.dto.custom.OrgResDTO;
import gov.moda.dw.manager.service.dto.custom.ResponseDTO;
import gov.moda.dw.manager.service.dto.custom.UserTypeIdDTO;
import gov.moda.dw.manager.type.AccountActionType;
import gov.moda.dw.manager.type.RoleType;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.UserTypeIdType;
import gov.moda.dw.manager.util.DynamicDtoUtils;

@RestController
@RequestMapping("/api/modadw311w")
public class Ams311wResource {

    private static final String ENTITY_NAME = "Ams311wResource";

    private final Logger log = LoggerFactory.getLogger(Ams311wResource.class);

    private final Ams311wService ams311wService;

    private final Ams321wService ams321wService;

    private final DynamicDtoUtils dynamicDtoUtils;

    private final CustomOrgService customOrgService;

    @Autowired
    private ExtendedUserRepository extendedUserRepository;

    public Ams311wResource(Ams311wService ams311wService, Ams321wService ams321wService,
            DynamicDtoUtils dynamicDtoUtils, CustomOrgService customOrgService) {
        this.ams311wService = ams311wService;
        this.ams321wService = ams321wService;
        this.dynamicDtoUtils = dynamicDtoUtils;
        this.customOrgService = customOrgService;
    }

    /**
     * 驗證帳號啟用key、重置key。
     */
    @PostMapping("/validate/key")
    public ResponseEntity<ResponseDTO<Ams311wValidateIdentifierResDTO>> validateResetAndActivateIdentifier(
            @RequestBody Ams311wValidateIdentifierReqDTO reqDTO) {
        log.debug("Ams311wResource-validateResetAndActivateIdentifier-驗證啟用及重置密碼key，req: {}", reqDTO);
        try {
            Ams311wValidateIdentifierResultDTO resultDTO = this.ams311wService.validateIdentifier(reqDTO);
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms311wValidateIdentifierResDTO()));
                } else {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
                }
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-validateResetAndActivateIdentifier-發生錯誤，原因為:{}",
                    ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_VALIDATE_KEY_EXCEPTION));
    }

    /**
     * 帳號啟用。
     */
    @PostMapping("/activate")
    public ResponseEntity<ResponseDTO> finishActivateAccount(@RequestBody Ams311wActivateReqDTO reqDTO) {
        log.debug("Ams311wResource-finishActivateAccount-進行啟用帳號及重置密碼，req: {}", reqDTO);
        try {
            // 檢查activationKey和resetKey是否為空。
            if (StringUtils.isBlank(reqDTO.getActivationKey()) || StringUtils.isBlank(reqDTO.getResetKey())) {
                return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_VALIDATE_KEY_FAIL));
            }
            // 檢查newPassword是否為空。
            if (StringUtils.isBlank(reqDTO.getNewBwd())) {
                return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_NEW_BWD_ERROR));
            }

            StatusCode result = this.ams311wService.finishActivateAccount(reqDTO);
            if (result != null) {
                return ResponseEntity.ok().body(new ResponseDTO<>(result));
            } else {
                log.error("Ams311wService-finishActivateAccount-發生錯誤，驗證金鑰查詢不到帳號，ActivationKey:{}，ResetKey:{}",
                        reqDTO.getActivationKey(), reqDTO.getResetKey());
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-finishActivateAccount-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_ACTIVATE_EXCEPTION));
    }

    /**
     * {@code POST /create} : Creates a new user account. 建立帳號。
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ams311w_c')")
    public ResponseEntity<ResponseDTO> createUser(@RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[建立帳號]/api/modadw311w/create");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-createUser-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        try {
            Ams311wResultDTO resultDTO = this.ams311wService.upsertUser(ams311wReqDTO, AccountActionType.CREATE,
                    userLogin.get());
            if (resultDTO != null) {
                return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-createUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_CREATE_EXCEPTION));
    }

    /**
     * {@code POST /update} : Update user account data. 更新帳號。
     */
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ams311w_e')")
    public ResponseEntity<ResponseDTO<Ams311wAccountResDTO>> updateUser(@RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[更新帳號]/api/modadw311w/update");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-updateUser-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        try {
            Ams311wResultDTO resultDTO = this.ams311wService.upsertUser(ams311wReqDTO, AccountActionType.UPDATE,
                    userLogin.get());
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms311wAccountResDTO()));
                } else {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
                }
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-updateUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_UPDATE_EXCEPTION));
    }

    /**
     * 查詢組織清單
     */
    @GetMapping("/searchOrgList")
    public ResponseEntity<ResponseDTO<List<OrgResDTO>>> searchAllOrgListForAccountManagement() {
        log.info("[{}-searchAllOrgListForAccountManagement]，進入查詢組織功能。", ENTITY_NAME);
        ResponseDTO<List<OrgResDTO>> responseDTO = new ResponseDTO<>();
        try {
            // 查詢組織
            List<OrgDTO> orgDTOList = ams311wService.getAllOrgList();

            // 整理要回傳的資料
            List<OrgResDTO> resultOrgList = new ArrayList<>();
            for (OrgDTO orgDTO : orgDTOList) {
                OrgResDTO resultOrg = this.saveToResultOrgDTO(orgDTO);
                resultOrgList.add(resultOrg);
            }

            resultOrgList.sort(Comparator.comparing(OrgResDTO::getOrgId));

            responseDTO.setStatusCode(StatusCode.SUCCESS);
            responseDTO.setData(resultOrgList);

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            log.error("[{}-searchAllOrgListForAccountManagement]，發生錯誤, 錯誤原因為={}", ENTITY_NAME,
                    ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    /**
     * 查詢帳號類型清單
     */
    @GetMapping("/searchUserTypeIdList")
    public ResponseEntity<ResponseDTO<List<UserTypeIdDTO>>> searchAllUserTypeIdListForAccountManagement() {
        log.info("[{}-searchAllUserTypeIdListForAccountManagement]，進入查詢帳號類型功能。", ENTITY_NAME);
        ResponseDTO<List<UserTypeIdDTO>> responseDTO = new ResponseDTO<>();
        try {
            List<UserTypeIdDTO> dtoList = new ArrayList<>();
            for (UserTypeIdType type : UserTypeIdType.values()) {
                UserTypeIdDTO dto = new UserTypeIdDTO();
                dto.setCode(type.getCode());
                dto.setName(type.getName());
                dtoList.add(dto);
            }

            responseDTO.setStatusCode(StatusCode.SUCCESS);
            responseDTO.setData(dtoList);

            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception ex) {
            log.error("[{}-searchAllUserTypeIdListForAccountManagement]，發生錯誤, 錯誤原因為={}", ENTITY_NAME,
                    ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    /**
     * 變更帳號啟停狀態。
     */
    @PostMapping("/changeActivated")
    @PreAuthorize("hasAuthority('ams311w_upState')")
    public ResponseEntity<ResponseDTO<Ams311wAccountResDTO>> changeUserActivated(
            @RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[變更帳號啟停狀態]/api/modadw311w/changeActivated");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-createUser-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        try {
            Ams311wResultDTO resultDTO = this.ams311wService.upsertUser(ams311wReqDTO,
                    AccountActionType.CHANGE_ACTIVATED, userLogin.get());
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms311wAccountResDTO()));
                } else {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
                }
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-changeUserActivated-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_UPDATE_EXCEPTION));
    }

    /**
     * 重置帳號密碼。
     */
    @PostMapping("/resetBwd")
    @PreAuthorize("hasAuthority('ams311w_resetBwd')")
    public ResponseEntity<ResponseDTO<Ams311wAccountResDTO>> resetUserBwd(@RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[重置帳號密碼]/api/modadw311w/changeActivated");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-createUser-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        try {
            Ams311wResultDTO resultDTO = this.ams311wService.upsertUser(ams311wReqDTO, AccountActionType.RESET_PWD,
                    userLogin.get());
            if (resultDTO != null) {
                if (StatusCode.SUCCESS.getCode().equals(resultDTO.getStatusCode().getCode())) {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getAms311wAccountResDTO()));
                } else {
                    return ResponseEntity.ok().body(new ResponseDTO<>(resultDTO.getStatusCode()));
                }
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-resetUserBwd-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_UPDATE_EXCEPTION));
    }

    /**
     * 刪除帳號。
     */
    @PostMapping("/deleteAccount")
    @PreAuthorize("hasAuthority('ams311w_d')")
    public ResponseEntity<ResponseDTO> deleteAccount(@RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[刪除帳號]/api/modadw311w/deleteAccount");
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-deleteAccount-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        if (userLogin.get().equals(ams311wReqDTO.getLogin())) {
            log.error("Ams311wResource-deleteAccount-發生錯誤，不可刪除正在登入的帳號。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_DELETE_LOGGED_IN_ACCOUNT));
        }

        if ("admin".equals(ams311wReqDTO.getLogin())) {
            log.error("Ams311wResource-deleteAccount-發生錯誤，不可刪除帳號:admin。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_DELETE_ADMIN_IN_ACCOUNT));
        }

        log.debug("[{}-deleteAccount]，準備刪除帳號", ENTITY_NAME);
        try {
            StatusCode result = this.ams311wService.deleteAccount(ams311wReqDTO);
            if (result != null) {
                if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
                    log.debug("[{}-deleteAccount]，帳號，已刪除", ENTITY_NAME);
                }
                return ResponseEntity.ok().body(new ResponseDTO<>(result));
            }
        } catch (Exception ex) {
            log.error("Ams311wResource-deleteAccount-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_DELETE_EXCEPTION));
    }

    /**
     * 帳號權限-帳號管理_重新寄送帳號啟用信
     */
    @PostMapping("/reSendActivationEmail")
    @PreAuthorize("hasAuthority('ams311w_c')")
    public ResponseEntity<ResponseDTO> reSendActivationEmail(@RequestBody Ams311wReqDTO ams311wReqDTO) {
        log.debug("[重新寄送帳號啟用信]/api/modadw311w/reSendActivationEmail");

        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-reSendActivationEmail-發生錯誤，登入帳號查詢異常。");
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR));
        }
        log.debug("Ams311wResource-reSendActivationEmail-userLogin:{}", userLogin.get());
        try {
            StatusCode result = this.ams311wService.reSendActivationEmail(ams311wReqDTO.getId(), userLogin.get());

            return ResponseEntity.ok().body(new ResponseDTO<>(result));
        } catch (Exception ex) {
            log.error("Ams311wResource-reSendActivationEmail-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_UPDATE_EXCEPTION));
        }
    }

    /**
     * 帳號權限-帳號管理_取得所有帳號清單by搜尋條件。
     */
    @GetMapping("/queryAll")
    @PreAuthorize("hasAuthority('ams311w_s')")
    public ResponseEntity<Page<Map<String, Object>>> findUserListByConditions(Pageable pageable,
            Ams311wReqDTO ams311wReqDTO, @RequestParam(required = false) List<String> fields) {
        log.debug("[查詢帳號]/api/modadw311w/queryAll，pageable:{}", pageable);

        // 查詢所有用戶帳號資訊by條件。
        Page<Ams311wAccountResDTO> page = this.ams311wService.getAllUser(pageable, ams311wReqDTO);
        Page<Map<String, Object>> body = fields == null || fields.isEmpty() ? page.map(this.dynamicDtoUtils::dtoToMap)
                : page.map(userDTO -> this.dynamicDtoUtils.dtoToMap(userDTO, fields));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/queryDetail/{extendedId}")
    @PreAuthorize("hasAuthority('ams311w_s')")
    public ResponseEntity<ResponseDTO<Ams311wAccountResDTO>> getAccount(@PathVariable("extendedId") Long extendedId) {
        String methodName = "getAccount";
        log.info("[{}-{}] 進入帳號管理查詢資料ById", ENTITY_NAME, methodName);
        ResponseDTO<Ams311wAccountResDTO> responseDTO = new ResponseDTO<>();
        Ams311wDetailResultDTO resultDTO = this.ams311wService.queryByOne(extendedId);

        if (StatusCode.SUCCESS.equals(resultDTO.getStatusCode())) {
            responseDTO.setData(resultDTO.getAms311wAccountResDTO());
        }
        responseDTO.setStatusCode(resultDTO.getStatusCode());
        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * 取得登入使用者是否具備Admin角色
     */
    @GetMapping("/checkAdminRole")
    public ResponseEntity<ResponseDTO> checkAdminRoleForCurrentUser() {
        log.info("[{}-checkAdminRoleForCurrentUser]，查詢登入者是否具備Admin角色。", ENTITY_NAME);

        ResponseDTO<?> responseDTO = new ResponseDTO<>();

        // 取得JwtUserDTO
        JwtUserDTO jwtUserDTO = null;
        try {
            jwtUserDTO = this.ams311wService.extractJwtUser();
        } catch (JsonProcessingException ex) {
            log.error("[{}-checkAdminRoleForCurrentUser]，查詢登入者是否具備Admin角色，發生錯誤，{}，錯誤原因:{}。", ENTITY_NAME,
                    StatusCode.ACCOUNT_ADMIN_ROLE_QUERY_ERROR.getMsg(), ExceptionUtils.getStackTrace(ex));
            responseDTO.setStatusCode(StatusCode.ACCOUNT_ADMIN_ROLE_QUERY_ERROR);
            return ResponseEntity.ok().body(responseDTO);
        }

        // 確認登入使用者是否擁有admin權限
        boolean isAdminRole = this.ams311wService.hasAdminRole(jwtUserDTO);

        if (isAdminRole) {
            responseDTO.setStatusCode(StatusCode.SUCCESS);
        } else {
            responseDTO.setStatusCode(StatusCode.FAIL);
        }

        return ResponseEntity.ok().body(responseDTO);
    }

    /**
     * {@code post ams311w/search/role} : get all Role.
     *
     * @return role list.
     */
    @PostMapping("/allRole")
    @PreAuthorize("hasAuthority('ams311w_conferRole')")
    public ResponseEntity<ResponseDTO<List<RoleDTO>>> searchAllRole(@RequestBody Ams311wSearchAllRoleReqDTO reqDTO) {
        log.debug("[查詢所有角色]/api/modadw311w/allRole");
        try {
            // 檢查request必要參數
            if (reqDTO == null || StringUtils.isBlank(reqDTO.getUserId())) {
                log.warn("Ams311wResource-searchAllRole 請求參數缺少 reqDTO:{}", reqDTO);
                return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.REQUEST_MISSING_REQUIRED_PARAM));
            }

            // 查詢目前所有角色。
            Pageable pageable = PageRequest.of(0, 100);
            Ams321wReqDTO ams321wReqDTO = new Ams321wReqDTO();
            ams321wReqDTO.setState(StateType.ENABLED.getCode());
            Page<RoleDTO> allRole = this.ams321wService.getAllRole(ams321wReqDTO, pageable);

            // 檢查登入者是否有 最高管理權限
            JwtUserDTO jwtUserDTO = ams311wService.extractJwtUser();
            boolean hasAdmin = ams311wService.hasAdminRole(jwtUserDTO);

            // 查詢被點選的帳號原有的角色清單
            List<RoleDTO> userRoleList = ams311wService.getUserRoles(reqDTO.getUserId());

            List<RoleDTO> filteredRoles = new ArrayList<>();
            for (RoleDTO role : allRole.getContent()) {
                List<String> managerRoles = Arrays.asList(RoleType.ISSUER_ORG_MANAGER.getCode(),
                        RoleType.ISSUER_ORG_USER.getCode(), RoleType.DEFAULT_ROLE.getCode());

                // 登入者不是 最高管理權限, 僅能顯示這3個角色, 但若有其他角色, 也要顯示
                boolean isManagerRoles = managerRoles.contains(role.getRoleId());
                boolean showRole = hasAdmin || isManagerRoles;

                if (!showRole) {
                    showRole = userRoleList.stream()
                            .anyMatch(userRole -> role.getRoleId().equals(userRole.getRoleId()));
                }

                if (!showRole) {
                    continue;
                }
                RoleDTO filteredRole = new RoleDTO();
                filteredRole.setId(role.getId());
                filteredRole.setRoleId(role.getRoleId());
                filteredRole.setRoleName(role.getRoleName());
                filteredRole.setDescription(role.getDescription());
                filteredRole.setDataRole1(role.getDataRole1());
                filteredRoles.add(filteredRole);
            }
            log.info("Ams311wResource-searchAllRole，已查詢所有可選擇角色清單，rolesList:{}", filteredRoles);
            return ResponseEntity.ok().body(new ResponseDTO<>(filteredRoles));
        } catch (Exception ex) {
            log.error("Ams311wResource-searchAllRole-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok().body(new ResponseDTO<>(StatusCode.ACCOUNT_GET_ALL_ROLE_EXCEPTION));
        }
    }

    /**
     * 帳號已授權角色清單
     */
    @PostMapping("/userRoles")
    @PreAuthorize("hasAuthority('ams311w_userRoles')")
    public ResponseDTO<List<RoleDTO>> getUserRoles(@RequestBody Ams311wReqDTO userDTO) {
        log.debug("[帳號已授權角色清單]/api/modadw311w/userRoles");
        ResponseDTO<List<RoleDTO>> responseDTO = new ResponseDTO<>();
        try {
            // 查詢帳號授權的角色
            Ams311wUserRolesDTO ams311wUserRolesDTO = this.ams311wService.getUserRoles(userDTO);

            if (ams311wUserRolesDTO != null) {
                if (ams311wUserRolesDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams311wUserRolesDTO.getUserRolesData());
                }
                responseDTO.setStatusCode(ams311wUserRolesDTO.getStatusCode());
            }
            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams311wResource-getUserRoles-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 帳號授權角色
     */
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ams311w_conferRole')")
    public ResponseDTO updateUserRoles(@RequestBody Ams311wUpdateRolesReqDTO request) {
        log.debug("[帳號授權角色]/api/modadw311w/roles");

        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty()) {
            log.error("Ams311wResource-updateUserRoles-發生錯誤，登入帳號查詢異常。");
            return new ResponseDTO<>(StatusCode.ACCOUNT_LOGIN_USER_ERROR);
        }

        ResponseDTO<StatusCode> responseDTO = new ResponseDTO<>();

        try {
            Ams311wReqDTO ams311wReqDTO = request.getUserDTO();
            List<RoleDTO> roleDTOList = request.getAuthCategories();

            Ams311wUserRolesDTO ams311wUserRolesDTO = this.ams311wService.updateUserRoles(ams311wReqDTO, roleDTOList,
                    userLogin.get());
            if (ams311wUserRolesDTO != null) {
                responseDTO.setStatusCode(ams311wUserRolesDTO.getStatusCode());
            }
            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams311wResource-updateUserRoles-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 左側MENU選單 - 使用者登入後取得可視SideLink列表
     */
    @PostMapping("/getSideLink")
    public ResponseEntity<ResponseDTO<Set<String>>> getSideLink() {
        log.info("[準備取得登入者可視SideLink列表]/api/modadw311w/getSideLink");
        ResponseDTO<Set<String>> responseDTO = new ResponseDTO<>();
        try {
            // 取得當前登入者資料
            Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
            if (userLogin.isEmpty()) {
                log.info("使用者未登入，或者無法取得使用者資料");
                responseDTO.setStatusCode(StatusCode.CURRENT_USER_LOGIN_NOT_FOUND);
                return ResponseEntity.ok(responseDTO);
            }

            // 取得登入者所擁有 SideLink 內容
            String userLoginStr = userLogin.get();
            Set<String> sideLinkList = ams311wService.getSideLink(userLoginStr);
            responseDTO.setStatusCode(StatusCode.SUCCESS);
            responseDTO.setData(sideLinkList);

            return ResponseEntity.ok(responseDTO);
        } catch (Exception ex) {
            log.error("Ams311wResource-getSideLink 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return ResponseEntity.ok(responseDTO);
        }
    }

    /**
     * 準備取得當前登入者個人資料
     */
    @PostMapping("/getUserDetails")
    public ResponseDTO<Ams311wCurrentUserResDTO> queryPersonalInfo() {
        log.info("[準備取得當前登入者個人資料]/api/modadw311w/getUserDetails");

        ResponseDTO<Ams311wCurrentUserResDTO> responseDTO = new ResponseDTO<>();
        try {
            // 取得當前登入帳號
            List<JwtUserObject> jwtUserObjectList = SecurityUtils.getJwtUserObject();
            if (jwtUserObjectList.isEmpty()) {
                log.info("Ams311wResource-queryPersonalInfo 找不到當前登入的使用者");
                responseDTO.setStatusCode(StatusCode.CURRENT_USER_LOGIN_NOT_FOUND);
                return responseDTO;
            }

            // 查詢登入帳號的個人資料
            Ams311wCurrentUserResultDTO ams311wCurrentUserResultDTO = ams311wService
                    .queryPersonalInfo(jwtUserObjectList.get(0).getUserId());
            if (ams311wCurrentUserResultDTO != null) {
                if (ams311wCurrentUserResultDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams311wCurrentUserResultDTO.getAms311wCurrentUserResDTO());
                }
                responseDTO.setStatusCode(ams311wCurrentUserResultDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams311wResource-queryPersonalInfo 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 更新個人資料設定
     *
     * @return
     */
    @PostMapping("/updateUserDetails")
    public ResponseDTO<Ams311wCurrentUserResDTO> profileSettings(
            @RequestBody Ams311wCurrentUserReqDTO ams311wCurrentUserReqDTO) {
        log.info("[更新個人資料設定]/api/modadw311w/updateUserDetails");
        ResponseDTO<Ams311wCurrentUserResDTO> responseDTO = new ResponseDTO<>();

        try {
            // 取得當前登入帳號 & 檢查準備update的資料是否與當前登入者一致
            Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();
            if (currentUser.isEmpty()) {
                log.info("Ams311wResource-profileSettings 找不到當前登入的使用者");
                responseDTO.setStatusCode(StatusCode.CURRENT_USER_LOGIN_NOT_FOUND);
                return responseDTO;
            } else if (!currentUser.get().equals(ams311wCurrentUserReqDTO.getUserId())) {
                log.info("Ams311wService-profileSettings 當前登入帳號與要更新的個人帳號不一致");
                responseDTO.setStatusCode(StatusCode.CURRENT_USER_DO_NOT_MATCH);
                return responseDTO;
            }

            // 更新登入帳號的個人資料
            Ams311wCurrentUserResultDTO ams311wCurrentUserResultDTO = ams311wService
                    .profileSettings(ams311wCurrentUserReqDTO);
            if (ams311wCurrentUserResultDTO != null) {
                if (ams311wCurrentUserResultDTO.getStatusCode() == StatusCode.SUCCESS) {
                    responseDTO.setData(ams311wCurrentUserResultDTO.getAms311wCurrentUserResDTO());
                }
                responseDTO.setStatusCode(ams311wCurrentUserResultDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams311wResource-profileSettings 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 補發新 Access Token
     *
     * @return
     */
    @PostMapping("/resetAccessToken")
    public ResponseDTO<Ams311wCurrentUserResDTO> resetAccessToken() {
        log.info("[準備補發新 Access Token]/api/modadw311w/resetAccessToken");
        ResponseDTO<Ams311wCurrentUserResDTO> responseDTO = new ResponseDTO<>();

        try {
            // 取得當前登入帳號
            Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();
            if (currentUser.isEmpty()) {
                log.info("Ams311wResource-resetAccessToken 找不到當前登入的使用者");
                responseDTO.setStatusCode(StatusCode.CURRENT_USER_LOGIN_NOT_FOUND);
                return responseDTO;
            }

            // 更新登入帳號的個人資料(Access Token)
            Ams311wResultDTO ams311wResultDTO = ams311wService.resetAccessToken(currentUser.get());
            if (ams311wResultDTO != null) {
                responseDTO.setStatusCode(ams311wResultDTO.getStatusCode());
            }

            return responseDTO;
        } catch (Exception ex) {
            log.error("Ams311wResource-resetAccessToken 發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return responseDTO;
        }
    }

    /**
     * 整理回傳的組織清單內容
     */
    private OrgResDTO saveToResultOrgDTO(OrgDTO orgDTO) {
        OrgResDTO orgSearchAllResDTO = new OrgResDTO();
        orgSearchAllResDTO.setId(orgDTO.getId());
        orgSearchAllResDTO.setOrgId(orgDTO.getOrgId());
        orgSearchAllResDTO.setOrgTwName(orgDTO.getOrgTwName());
        orgSearchAllResDTO.setOrgEnName(orgDTO.getOrgEnName());
        orgSearchAllResDTO.setCreateTime(orgDTO.getCreateTime());
        orgSearchAllResDTO.setUpdateTime(orgDTO.getUpdateTime());

        return orgSearchAllResDTO;
    }
}
