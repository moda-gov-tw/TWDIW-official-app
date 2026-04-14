package gov.moda.dw.manager.service.custom;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.moda.dw.manager.domain.AccessToken;
import gov.moda.dw.manager.domain.BwdParam;
import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.domain.Org;
import gov.moda.dw.manager.domain.OtpVerify;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.repository.custom.CustomAccessTokenRepository;
import gov.moda.dw.manager.repository.custom.CustomExtendedUserRepository;
import gov.moda.dw.manager.repository.custom.CustomOrgRepository;
import gov.moda.dw.manager.repository.custom.CustomOtpVerifyRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.BwdHistoryService;
import gov.moda.dw.manager.service.ExtendedUserLogService;
import gov.moda.dw.manager.service.LoginCountService;
import gov.moda.dw.manager.service.criteria.BwdHistoryCriteria;
import gov.moda.dw.manager.service.criteria.ExtendedUserCriteria;
import gov.moda.dw.manager.service.criteria.LoginCountCriteria;
import gov.moda.dw.manager.service.criteria.MailTemplateCriteria;
import gov.moda.dw.manager.service.criteria.OrgCriteria;
import gov.moda.dw.manager.service.criteria.RoleCriteria;
import gov.moda.dw.manager.service.dto.BwdHistoryDTO;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.dto.ExtendedUserLogDTO;
import gov.moda.dw.manager.service.dto.LoginCountDTO;
import gov.moda.dw.manager.service.dto.MailTemplateDTO;
import gov.moda.dw.manager.service.dto.OrgDTO;
import gov.moda.dw.manager.service.dto.ResDTO;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wAccountResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wActivateReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wCurrentUserResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wDetailResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wMailResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wUserRolesDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierReqDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierResDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wValidateIdentifierResultDTO;
import gov.moda.dw.manager.service.dto.custom.Ams351wReqDTO;
import gov.moda.dw.manager.service.dto.custom.GenerateOtpReqDTO;
import gov.moda.dw.manager.service.dto.custom.JwtUserDTO;
import gov.moda.dw.manager.service.dto.custom.VerifyOtpReqDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import gov.moda.dw.manager.service.mapper.OrgMapper;
import gov.moda.dw.manager.type.AccessTokenType;
import gov.moda.dw.manager.type.AccountActionType;
import gov.moda.dw.manager.type.ApplyType;
import gov.moda.dw.manager.type.AuthorityAction;
import gov.moda.dw.manager.type.BwdProfileType;
import gov.moda.dw.manager.type.LogType;
import gov.moda.dw.manager.type.MailAction;
import gov.moda.dw.manager.type.OrgType;
import gov.moda.dw.manager.type.RoleType;
import gov.moda.dw.manager.type.StateType;
import gov.moda.dw.manager.type.StatusCode;
import gov.moda.dw.manager.type.UserTypeIdType;
import gov.moda.dw.manager.util.DateUtils;
import gov.moda.dw.manager.util.InstantFilterUtils;
import gov.moda.dw.manager.util.NotifyUtils;
import gov.moda.dw.manager.util.RSAUtils;
import gov.moda.dw.manager.util.StringFilterUtils;
import gov.moda.dw.manager.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import tech.jhipster.security.RandomUtil;

@Slf4j
@Service
public class Ams311wService {

    private static final String ENTITY_NAME = "Ams311wService";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final UserRepository userRepository;
    private final CustomExtendedUserRepository customExtendedUserRepository;
    private final ExtendedUserRepository extendedUserRepository;
    private final CustomUserRepository customUserRepository;
    private final CustomAccessTokenRepository customAccessTokenRepository;
    private final CustomOtpVerifyRepository customOtpVerifyRepository;
    private final CustomUserService customUserService;
    private final CustomExtendedUserService customExtendedUserService;
    private final Ams302wService ams302wService;
    private final PasswordEncoder passwordEncoder;
    private final BwdParamCustomService bwdParamCustomService;
    private final BwdHistoryService bwdHistoryService;
    private final ExtendedUserLogService extendedUserLogService;
    private final CustomMailTemplateQueryService mailTemplateQueryService;
    private final ExtendedUserMapper extendedUserMapper;
    private final ValidateUtils validateUtils;
    private final CustomExtendedUserQueryService customExtendedUserQueryService;
    private final CustomOrgRepository orgRepository;
    private final OrgMapper orgMapper;
    private final CustomRelService customRelService;
    private final CustomBwdHistoryQueryService customBwdHistoryQueryService;
    private final LoginCountService loginCountService;
    private final CustomLoginCountQueryService customLoginCountQueryService;
    private final CustomRelService relService;
    private final CustomOrgQueryService customOrgQueryService;
    private final CustomAccessTokenService customAccessTokenService;
    private final CustomRoleQueryService customRoleQueryService;

    @Value("${jhipster.mail.base-url}")
    private String baseURL;

    @Value("${fin.rsaUtil.prvKy}")
    private String privateKey;

    @Value("${sandbox.commonAccountOrgId:00000000}")
    private String commonAccountOrgId;

    @Value("${appleStoreImgPath}")
    private String appleStoreImgPath;

    @Value("${googlePlayImgPath}")
    private String googlePlayImgPath;

    @Value("${appleStoreAppUrl}")
    private String appleStoreAppUrl;

    @Value("${googlePlayAppUrl}")
    private String googlePlayAppUrl;

    @Value("${custom.job.removeNotActivatedUsers}")
    private boolean switchRemoveNotActivatedUsers;

    @Value("${otpExpireTime}")
    private Integer otpExpireTime;

    @Value("${otpReSendTime}")
    private Integer otpReSendTime;

    @Value("${switch.verify.otp}")
    private boolean switchVerifyOtp;

    public Ams311wService(
        UserRepository userRepository,
        ExtendedUserRepository extendedUserRepository,
        CustomUserRepository customUserRepository,
        CustomAccessTokenRepository customAccessTokenRepository,
        CustomOtpVerifyRepository customOtpVerifyRepository,
        CustomRelService relService,
        CustomUserService customUserService,
        CustomExtendedUserService customExtendedUserService,
        Ams302wService ams302wService,
        PasswordEncoder passwordEncoder,
        BwdParamCustomService bwdParamCustomService,
        BwdHistoryService bwdHistoryService,
        ExtendedUserLogService extendedUserLogService,
        CustomMailTemplateQueryService mailTemplateQueryService,
        ExtendedUserMapper extendedUserMapper,
        ValidateUtils validateUtils,
        CustomExtendedUserQueryService customExtendedUserQueryService,
        CustomOrgRepository orgRepository,
        //FIXME: this raise error:  Could not autowire. There is more than one bean of 'OrgMapper' type.
        // but somehow it compiles fine.
        OrgMapper orgmapper,
        CustomExtendedUserRepository customExtendedUserRepository,
        CustomRelService customRelService,
        CustomBwdHistoryQueryService customBwdHistoryQueryService,
        LoginCountService loginCountService,
        CustomLoginCountQueryService customLoginCountQueryService,
        CustomOrgQueryService customOrgQueryService,
        CustomAccessTokenService customAccessTokenService,
        CustomRoleQueryService customRoleQueryService
    ) {
        this.userRepository = userRepository;
        this.extendedUserRepository = extendedUserRepository;
        this.customUserRepository = customUserRepository;
        this.customAccessTokenRepository = customAccessTokenRepository;
        this.customOtpVerifyRepository = customOtpVerifyRepository;
        this.relService = relService;
        this.customUserService = customUserService;
        this.customExtendedUserService = customExtendedUserService;
        this.ams302wService = ams302wService;
        this.passwordEncoder = passwordEncoder;
        this.bwdParamCustomService = bwdParamCustomService;
        this.bwdHistoryService = bwdHistoryService;
        this.extendedUserLogService = extendedUserLogService;
        this.mailTemplateQueryService = mailTemplateQueryService;
        this.extendedUserMapper = extendedUserMapper;
        this.validateUtils = validateUtils;
        this.customExtendedUserQueryService = customExtendedUserQueryService;
        this.orgRepository = orgRepository;
        this.orgMapper = orgmapper;
        this.customExtendedUserRepository = customExtendedUserRepository;
        this.customRelService = customRelService;
        this.customBwdHistoryQueryService = customBwdHistoryQueryService;
        this.loginCountService = loginCountService;
        this.customLoginCountQueryService = customLoginCountQueryService;
        this.customOrgQueryService = customOrgQueryService;
        this.customAccessTokenService = customAccessTokenService;
        this.customRoleQueryService = customRoleQueryService;
    }

    /**
     * 建立、更新帳號。
     */
    @Transactional
    public Ams311wResultDTO upsertUser(Ams311wReqDTO ams311wReqDTO, AccountActionType actionType, String userLogin) {
        try {
            // 存入時統一轉為小寫
            if (StringUtils.isNotBlank(ams311wReqDTO.getLogin())) {
                ams311wReqDTO.setLogin(ams311wReqDTO.getLogin().toLowerCase());
            }

            // 確認前端輸入資料是否輸入正確。
            StatusCode result = this.checkData(ams311wReqDTO, actionType);

            if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
                // 新增或更新jhi_user。
                User saveResult;
                ExtendedUser extendedSaveResult;
                String accessToken = "";
                if (AccountActionType.CREATE.equals(actionType)) {
                    // 確認登入使用者是否擁有admin權限
                    JwtUserDTO jwtUserDTO = null;
                    jwtUserDTO = this.extractJwtUser();
                    if (!this.hasAdminRole(jwtUserDTO)) {
                        log.error("Ams311wService-upsertUser 建立帳號時使用者非管理員 jwtUserDTO={}", jwtUserDTO);
                        return new Ams311wResultDTO(StatusCode.ACCOUNT_CREATE_NOT_ADMIN);
                    }

                    saveResult = this.customUserService.saveUser(ams311wReqDTO, userLogin);
                    // 設定預設角色 並同步權限到 jhi_user_authority
                    List<RoleDTO> roleDTOList = relService.accountAction(saveResult, null, AuthorityAction.ACCOUNT_SET_DEFAULT_ROLE);
                    if (roleDTOList == null) {
                        log.error("Ams311wService-upsertUser 新建帳號設定預設角色時, 找不到該角色資料, 無法設定 user={}", saveResult);
                        return new Ams311wResultDTO(StatusCode.ACCOUNT_DEFAULT_ROLE_NOT_FOUND);
                    }
                } else {
                    saveResult = this.customUserService.updateUser(ams311wReqDTO, actionType, userLogin);
                }

                if (saveResult != null) {
                    // 新增或更新extended_user。
                    if (actionType.equals(AccountActionType.CREATE)) {
                        extendedSaveResult = this.customExtendedUserService.saveExtendedUser(ams311wReqDTO);
                    } else {
                        extendedSaveResult = this.customExtendedUserService.updateExtendedUser(
                                ams311wReqDTO,
                                actionType,
                                saveResult.isActivated()
                            );
                    }

                    // 新增、停用、更新 AccessToken
                    if (extendedSaveResult != null) {
                        String login = ams311wReqDTO.getLogin().toLowerCase();
                        String orgId = ams311wReqDTO.getOrgId();
                        if (actionType.equals(AccountActionType.CREATE)) {
                            Optional<Org> orgOpt = orgRepository.findByOrgId(orgId);
                            if (!orgOpt.isPresent()) {
                                return new Ams311wResultDTO(StatusCode.ACCOUNT_CREATE_EXCEPTION);
                            }
                            String orgTwName = orgOpt.get().getOrgTwName();
                            Ams351wReqDTO ams351wReqDTO = new Ams351wReqDTO();
                            ams351wReqDTO.setAuthKeyName(login + "存取權杖");
                            ams351wReqDTO.setOwner(login);
                            ams351wReqDTO.setOrgId(orgId);
                            ams351wReqDTO.setOrgName(orgTwName);
                            ams351wReqDTO.setState(StateType.ENABLED.getCode());
                            ams351wReqDTO.setActype(AccessTokenType.UNLIMITED.getCode());
                            AccessToken accessTokenResult = customAccessTokenService.saveAccessToken4CreateUser(ams351wReqDTO);
                            accessToken = accessTokenResult.getAccessToken();
                        } else if (actionType.equals(AccountActionType.CHANGE_ACTIVATED)) {
                            Optional<AccessToken> optionalAccessToken = customAccessTokenRepository
                                    .findFirstByOwnerOrderByCreateTimeDesc(ams311wReqDTO.getLogin());
                            if (optionalAccessToken.isPresent()) {
                                AccessToken accessTokenEntity = optionalAccessToken.get();
                                if (StringUtils.equals(StateType.ENABLED.getCode(), accessTokenEntity.getState())) {
                                    accessTokenEntity.setState(StateType.DISABLED.getCode());
                                } else {
                                    accessTokenEntity.setState(StateType.ENABLED.getCode());
                                }
                                customAccessTokenRepository.save(accessTokenEntity);
                            } else {
                                log.error(
                                        "Ams311wService-findFirstByOwnerOrderByCreateTimeDesc 查詢帳號AccessToken時, 找不到該帳號資料, 無法設定");
                            }
                        } else if (actionType.equals(AccountActionType.UPDATE)) {
                            Optional<AccessToken> optionalAccessToken = customAccessTokenRepository
                                    .findFirstByOwnerOrderByCreateTimeDesc(ams311wReqDTO.getLogin());
                            if (optionalAccessToken.isPresent()) {
                                Optional<Org> orgOpt = orgRepository.findByOrgId(orgId);
                                if (!orgOpt.isPresent()) {
                                    return new Ams311wResultDTO(StatusCode.ACCOUNT_UPDATE_EXCEPTION);
                                }
                                String orgTwName = orgOpt.get().getOrgTwName();
                                AccessToken accessTokenEntity = optionalAccessToken.get();
                                accessTokenEntity.setOrgId(orgId);
                                accessTokenEntity.setOrgName(orgTwName);
                                customAccessTokenRepository.save(accessTokenEntity);
                            } else {
                                log.error(
                                        "Ams311wService-findFirstByOwnerOrderByCreateTimeDesc 查詢帳號AccessToken時, 找不到該帳號資料, 無法設定");
                            }
                        }
                    }

                    if (extendedSaveResult != null) {
                        if (actionType.equals(AccountActionType.CREATE) || actionType.equals(AccountActionType.RESET_PWD)) {
                            StatusCode mailResult = this.doSendMail(saveResult, extendedSaveResult, actionType, accessToken, null, null);
                            if (!StatusCode.SUCCESS.getCode().equals(mailResult.getCode())) {
                                return new Ams311wResultDTO(mailResult);
                            }
                        }
                        if (actionType.equals(AccountActionType.CREATE)) {
                            // 新增帳號異動紀錄
                            this.saveAccountLog(saveResult, extendedSaveResult, LogType.ADD);

                            return new Ams311wResultDTO(result);
                        } else {
                            // 新增帳號異動紀錄
                            this.saveAccountLog(saveResult, extendedSaveResult, LogType.MOD);

                            return new Ams311wResultDTO(new Ams311wAccountResDTO(saveResult, extendedSaveResult));
                        }
                    }
                    return new Ams311wResultDTO(StatusCode.ACCOUNT_UPDATE_EXCEPTION);
                }
            } else {
                return new Ams311wResultDTO(result);
            }
        } catch (Exception ex) {
            log.error("Ams311wService-upsertUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        if (actionType.equals(AccountActionType.CREATE)) {
            return new Ams311wResultDTO(StatusCode.ACCOUNT_CREATE_EXCEPTION);
        } else {
            return new Ams311wResultDTO(StatusCode.ACCOUNT_UPDATE_EXCEPTION);
        }
    }

    /**
     * 取得所有帳號清單by搜尋條件。
     */
    public Page<Ams311wAccountResDTO> getAllUser(Pageable pageable, Ams311wReqDTO ams311wReqDTO) {
        log.info("Ams311wService-getAllUser 開始查詢帳號清單");
        try {
            // 取得JwtUserDTO
            JwtUserDTO jwtUserDTO = null;
            jwtUserDTO = this.extractJwtUser();

            // 確認登入使用者是否擁有admin權限
            boolean isAdminRole = this.hasAdminRole(jwtUserDTO);

            ExtendedUserCriteria extendedUserCriteria = this.setCriteriaFromSearch(isAdminRole, ams311wReqDTO, jwtUserDTO);
            List<ExtendedUserDTO> extendedUserDTOList =
                this.customExtendedUserQueryService.findByCriteria(extendedUserCriteria, pageable.getSort());
            List<String> logins = new ArrayList<>();
            for (ExtendedUserDTO extendedUserDTO : extendedUserDTOList) {
                logins.add(extendedUserDTO.getUserId());
            }

            // jhi_user。
            List<User> users = this.customUserRepository.findAllByLoginIn(logins);

            return this.getAllUserWithUserAndExtendedUser(extendedUserDTOList, logins, users, pageable, ams311wReqDTO);
        } catch (Exception ex) {
            log.error("Ams311wService-getAllUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 取得所有組織清單。
     */
    public List<OrgDTO> getAllOrgList() throws JsonProcessingException {
        // 取得JwtUserDTO
        JwtUserDTO jwtUserDTO = null;
        jwtUserDTO = this.extractJwtUser();

        // 確認登入使用者是否擁有admin權限
        boolean isAdminRole = this.hasAdminRole(jwtUserDTO);

        OrgCriteria criteria = new OrgCriteria();
        if (!isAdminRole) {
            criteria.setOrgId(StringFilterUtils.toEqualStringFilter(jwtUserDTO.getOrgId()));
        }

        return this.customOrgQueryService.findByCriteria(criteria);
    }

    /**
     * 查詢使用者已有的角色清單
     *
     * @param userId
     * @return
     */
    public List<RoleDTO> getUserRoles(String userId) {
        //查詢帳號擁有的角色資料
        User user = new User();
        user.setLogin(userId);
        List<RoleDTO> roleDTOList = relService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);
        return roleDTOList;
    }

    /**
     * 判斷用戶是否擁有發行端或驗證端管理者角色
     *
     * @param jwtUserDTO 用戶DTO，包含用戶ID等資料
     * @return 如果用戶擁有 ISSUER_ADMIN 或 VERIFY_ADMIN 角色，則返回true，否則返回false。
     */
    public boolean hasAdminRole(JwtUserDTO jwtUserDTO) {
        // 創建Use對象並設置用戶ID
        User user = new User();
        user.setLogin(jwtUserDTO.getUserId());

        // 查詢用戶的角色
        List<RoleDTO> roleDTOS = this.customRelService.searchUserRole(user);

        // 遍歷角色並檢查是否有ISSUER_ADMIN或VERIFY_ADMIN角色
        for (RoleDTO dto : roleDTOS) {
            if (RoleType.ISSUER_ADMIN.getCode().equals(dto.getRoleId()) || RoleType.VERIFY_ADMIN.getCode().equals(dto.getRoleId())) {
                return true; // 如果有這兩個角色中的任何一個，返回 true
            }
        }
        // 沒有找到符合的角色，返回 false
        return false;
    }

    /**
     * 帳號權限-帳號管理_取得JwtUserDTO
     */
    public JwtUserDTO extractJwtUser() throws JsonProcessingException {
        // 登入使用者
        List<JwtUserObject> jwtUserObject = SecurityUtils.getJwtUserObject();
        JwtUserDTO jwtUserDTO = new JwtUserDTO();
        jwtUserDTO.setUserId(jwtUserObject.get(0).getUserId());
        jwtUserDTO.setOrgId(jwtUserObject.get(0).getOrgId());
        return jwtUserDTO;
    }

    /**
     * 帳號權限-帳號管理_取得帳號詳情
     */
    public Ams311wDetailResultDTO queryByOne(Long extendedId) {
        Ams311wDetailResultDTO ams311wDetailResultDTO = new Ams311wDetailResultDTO();
        try {
            Optional<ExtendedUserDTO> dbResultByExtendedUser = this.customExtendedUserService.findOne(extendedId);
            if (dbResultByExtendedUser.isEmpty()) {
                log.warn("[{}] queryByOne ,{}", ENTITY_NAME, StatusCode.ACCOUNT_NOT_FOUND.getMsg());
                ams311wDetailResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_FOUND);
                return ams311wDetailResultDTO;
            }

            Optional<User> dbResultByUser = this.userRepository.findOneByLogin(dbResultByExtendedUser.get().getUserId());
            if (dbResultByUser.isEmpty()) {
                log.warn("[{}] queryByOne ,{}", ENTITY_NAME, StatusCode.ACCOUNT_NOT_FOUND.getMsg());
                ams311wDetailResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_FOUND);
                return ams311wDetailResultDTO;
            }
            Optional<Org> orgOp = orgRepository.findByOrgId(dbResultByExtendedUser.get().getOrgId());
            OrgDTO orgDTO = orgMapper.toDto(orgOp.get());
            Ams311wAccountResDTO ams311wAccountResDTO = new Ams311wAccountResDTO(
                dbResultByUser.get(),
                dbResultByExtendedUser.get(),
                orgDTO
            );
            ams311wDetailResultDTO.setStatusCode(StatusCode.SUCCESS);
            ams311wDetailResultDTO.setAms311wAccountResDTO(ams311wAccountResDTO);
            return ams311wDetailResultDTO;
        } catch (Exception ex) {
            log.warn(
                "[{}-queryByOne]，{}，extendedUser id:{}，exception:{}",
                ENTITY_NAME,
                StatusCode.ACCOUNT_QUERY_DETAIL_ERROR.getMsg(),
                extendedId,
                ExceptionUtils.getStackTrace(ex)
            );
            ams311wDetailResultDTO.setStatusCode(StatusCode.ACCOUNT_QUERY_DETAIL_ERROR);
            return ams311wDetailResultDTO;
        }
    }

    /**
     * 整合jhi_user、extended_user。
     */
    private Page<Ams311wAccountResDTO> getAllUserWithUserAndExtendedUser(
        List<ExtendedUserDTO> extendedUserDTOList,
        List<String> logins,
        List<User> users,
        Pageable pageable,
        Ams311wReqDTO ams311wReqDTO
    ) {
        List<Ams311wAccountResDTO> dtoList = new ArrayList<>();

        for (String login : logins) {
            try {
                User foundUser = null;
                for (User u : users) {
                    if (login.equals(u.getLogin())) {
                        foundUser = u;
                        break;
                    }
                }
                if (foundUser == null) {
                    throw new RuntimeException("User not found for login");
                }
                // 查找對應的 ExtendedUserDTO。
                ExtendedUserDTO foundExtendedUserDTO = null;
                for (ExtendedUserDTO e : extendedUserDTOList) {
                    if (login.equals(e.getUserId())) {
                        foundExtendedUserDTO = e;
                        break;
                    }
                }
                if (foundExtendedUserDTO == null) {
                    throw new RuntimeException("ExtendedUserDTO not found for userId");
                }

                OrgDTO orgDTO = new OrgDTO();
                if (!foundExtendedUserDTO.getOrgId().isEmpty()) {
                    Optional<Org> orgOp = orgRepository.findByOrgId(foundExtendedUserDTO.getOrgId());
                    orgDTO = orgMapper.toDto(orgOp.get());
                } else {
                    orgDTO.setOrgTwName("");
                }

                // 將User和ExtendedUserDTO組合並轉換為 ms311wReqDTO。
                Ams311wAccountResDTO dto = new Ams311wAccountResDTO(foundUser, foundExtendedUserDTO, orgDTO);

                if (ams311wReqDTO != null) {
                    if (ams311wReqDTO.getState() != null) {
                        if ("1".equals(ams311wReqDTO.getState())) {
                            if ("1".equals(dto.getState())) {
                                dtoList.add(dto);
                            }
                        } else {
                            if ("0".equals(dto.getState())) {
                                dtoList.add(dto);
                            }
                        }
                    } else {
                        dtoList.add(dto);
                    }
                } else {
                    dtoList.add(dto);
                }
            } catch (Exception ex) {
                log.error("Ams311wService-getAllUserAccountWithUserAndExtendedUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            }
        }
        // 計算分頁起始位置和結束位置。
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtoList.size());

        // 獲取分頁結果
        List<Ams311wAccountResDTO> pageContent = dtoList.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }

    /**
     * 取得帳號已授予角色
     *
     * @param ams311wReqDTO
     * @return
     */
    public Ams311wUserRolesDTO getUserRoles(Ams311wReqDTO ams311wReqDTO) {
        try {
            Ams311wUserRolesDTO ams311wUserRolesDTO = new Ams311wUserRolesDTO();
            // 檢查傳入的值。
            if (ams311wReqDTO == null || ams311wReqDTO.getId() == null) {
                log.error("Ams311wService-getUserRoles 缺少必要參數");
                ams311wUserRolesDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return ams311wUserRolesDTO;
            }

            log.info("Ams311wService-getUserRoles 開始取得帳號已授予的角色列");

            Optional<User> optionalUser = this.userRepository.findById(ams311wReqDTO.getId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                //查詢帳號擁有的角色資料
                List<RoleDTO> roleDTOList = relService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);

                // 整理回傳內容
                if (roleDTOList != null) {
                    ams311wUserRolesDTO.setUserRolesData(roleDTOList);
                    ams311wUserRolesDTO.setStatusCode(StatusCode.SUCCESS);
                    log.info("Ams311wService-getUserRoles 取得帳號已授權角色 roleDTOList={}", roleDTOList);
                } else {
                    ams311wUserRolesDTO.setStatusCode(StatusCode.ACCOUNT_GET_USER_ROLE_EXCEPTION);
                    log.error(
                        "Ams311wService-getUserRoles 查詢帳號擁有的角色roleDTOList回傳 null, action={} user={}",
                        AuthorityAction.ACCOUNT_SEARCH_ROLE,
                        user
                    );
                }
            } else {
                ams311wUserRolesDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
                log.error("Ams311wService-getUserRoles 查不到該帳號資料");
            }
            return ams311wUserRolesDTO;
        } catch (Exception ex) {
            log.error("Ams311wService-getUserRoles 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 帳號授權角色。
     */
    public Ams311wUserRolesDTO updateUserRoles(Ams311wReqDTO ams311wReqDTO, List<RoleDTO> roleDTOList, String userLogin) {
        try {
            log.info("Ams311wService-updateUserRoles 進入帳號授權角色");

            Ams311wUserRolesDTO ams311wUserRolesDTO = new Ams311wUserRolesDTO();
            // 檢查傳入的值。
            if (ams311wReqDTO == null || ams311wReqDTO.getLogin() == null || ams311wReqDTO.getId() == null) {
                log.error("Ams311wService-updateUserRoles 缺少必要參數");
                ams311wUserRolesDTO.setStatusCode(StatusCode.REQUEST_MISSING_REQUIRED_PARAM);
                return ams311wUserRolesDTO;
            }

            // 檢查登入者是否有 最高管理權限
            JwtUserDTO jwtUserDTO = this.extractJwtUser();
            boolean hasAdmin = this.hasAdminRole(jwtUserDTO);
            // 如果沒有 最高管理權限, 檢查請求來的角色清單是否含有不可被賦予的角色(因為沒有 最高管理權限)
            if (!hasAdmin) {
                // 查詢 需要被檢查的角色清單
                RoleCriteria criteria = new RoleCriteria();
                criteria.setDataRole1(StringFilterUtils.toEqualStringFilter("1"));
                criteria.setState(StringFilterUtils.toEqualStringFilter(StateType.ENABLED.getCode()));
                List<RoleDTO> checkRoleList = customRoleQueryService.findByCriteria(criteria);

                // 查詢 準備被賦予的帳號原有的角色清單
                List<RoleDTO> userRoleList = this.getUserRoles(userLogin);

                // 將請求中的角色id存入
                Set<String> requestRoleIds = new HashSet<>();
                for (RoleDTO requestRole : roleDTOList) {
                    requestRoleIds.add(requestRole.getRoleId());
                }

                // 將使用者已有的角色id存入
                Set<String> userRoleIds = new HashSet<>();
                for (RoleDTO userRole : userRoleList) {
                    userRoleIds.add(userRole.getRoleId());
                }

                // 檢查請求中是否有不可被賦予的角色
                boolean requestHasAdminRole = false;
                for (RoleDTO checkRole : checkRoleList) {
                    String roleId = checkRole.getRoleId();
                    // 如果請求的角色包含在 checkRoleList 中，並且使用者尚未擁有該角色
                    if (requestRoleIds.contains(roleId) && !userRoleIds.contains(roleId)) {
                        requestHasAdminRole = true;
                        break; // 找到一個就可以跳出，提升效能
                    }
                }

                // 登入者沒有授權 最高權限管理者角色的權力, 回覆錯誤至前端
                if (requestHasAdminRole) {
                    log.warn(
                        "Ams311wService-updateUserRoles 登入者請求賦予的角色清單中, 含有登入者不可授權的角色, 帳號賦予角色失敗." +
                        " 操作者:{}, 請求賦予的角色清單:{}",
                        userLogin,
                        roleDTOList
                    );
                    ams311wUserRolesDTO.setStatusCode(StatusCode.ACCOUNT_CANT_CONFER_ROLE);
                    return ams311wUserRolesDTO;
                }
            }

            // 帳號授權角色並同步至 jhi_user_authority
            this.updateUserAuthority(ams311wReqDTO, roleDTOList, ams311wUserRolesDTO, userLogin);

            // extended_user 紀錄角色授權時間
            ExtendedUser extendedSaveResult =
                this.customExtendedUserService.updateExtendedUser(
                        ams311wReqDTO,
                        AccountActionType.CHANGE_ROLE,
                        ams311wReqDTO.getActivated()
                    );

            // 紀錄帳號異動
            this.saveAccountLog(null, extendedSaveResult, LogType.MOD);

            return ams311wUserRolesDTO;
        } catch (Exception ex) {
            log.error("Ams311wService-updateUserRoles 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 帳號授權角色 - 更新jhi_user_authority。
     */
    private void updateUserAuthority(
        Ams311wReqDTO ams311wReqDTO,
        List<RoleDTO> roleDTOList,
        Ams311wUserRolesDTO ams311wUserRolesDTO,
        String userLogin
    ) {
        Optional<User> userOptional = this.userRepository.findById(ams311wReqDTO.getId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            log.info("Ams311wService-updateUserAuthority 成功取得角色資料");

            //綁定帳號授予的角色 並同步至 jhi_user_authority
            List<RoleDTO> roleDTOS = relService.accountAction(user, roleDTOList, AuthorityAction.ACCOUNT_CONFER_ROLE);
            if (roleDTOS != null) {
                user.setLastModifiedDate(Instant.now());
                user.setLastModifiedBy(userLogin);
                this.userRepository.save(user);
                ams311wUserRolesDTO.setStatusCode(StatusCode.SUCCESS);
                log.info("Ams311wService-getUserRoles 帳號授權角色完成 roleDTOList={}", roleDTOList);
            } else {
                ams311wUserRolesDTO.setStatusCode(StatusCode.ACCOUNT_UPDATE_USER_ROLES_EXCEPTION);
                log.error(
                    "Ams311wService-getUserRoles 帳號授權角色 roleDTOS 回傳 null, action={}",
                    AuthorityAction.ACCOUNT_SEARCH_ROLE
                );
            }
        } else {
            ams311wUserRolesDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
            log.error("Ams311wService-updateUserAuthority 查詢不到角色");
        }
    }

    /**
     * 驗證建立及更新帳號的參數。
     */
    private StatusCode checkData(Ams311wReqDTO ams311wReqDTO, AccountActionType actionType) {
        boolean isCreateOrUpdate = actionType.equals(AccountActionType.CREATE) || actionType.equals(AccountActionType.UPDATE);
        String orgId = ams311wReqDTO.getOrgId();

        // 檢核組織是否存在
        if (StringUtils.isNotBlank(orgId) && isCreateOrUpdate) {
            Optional<Org> orgOpt = orgRepository.findByOrgId(orgId);
            if (orgOpt.isEmpty()) {
                return StatusCode.ACCOUNT_INVALID_ORG_ID;
            }
        }

        if (StringUtils.isBlank(ams311wReqDTO.getUserName()) && isCreateOrUpdate) {
            log.error("Ams311wService-checkData-發生錯誤，使用者名稱不符合格式");
            return StatusCode.ACCOUNT_INVALID_NAME;
        }
        if (
            (StringUtils.isBlank(ams311wReqDTO.getUserTypeId()) ||
                UserTypeIdType.toUserTypeIdType(ams311wReqDTO.getUserTypeId()) == null) &&
            isCreateOrUpdate
        ) {
            log.error("Ams311wService-checkData-發生錯誤，帳號類型不符合規範");
            return StatusCode.ACCOUNT_INVALID_USER_TYPE_ID;
        }
        if (isCreateOrUpdate && (StringUtils.isBlank(ams311wReqDTO.getTel()) || ams311wReqDTO.getTel().length() > 100)) {
            log.error("Ams311wService-checkData-發生錯誤，聯絡電話不符合格式");
            return StatusCode.ACCOUNT_INVALID_PHONE;
        }
        if ((StringUtils.isBlank(orgId) || OrgType.DEFAULT_ORG.getCode().equals(orgId)) && isCreateOrUpdate) {
            log.error("Ams311wService-checkData-發生錯誤，組織欄位不符合格式");
            return StatusCode.ACCOUNT_INVALID_ORG_ID;
        }
        if (actionType.equals(AccountActionType.CREATE)) {
            if ((StringUtils.isBlank(ams311wReqDTO.getLogin()) || !this.isValidEmail(ams311wReqDTO.getLogin().toLowerCase()))) {
                log.error("Ams311wService-checkData-發生錯誤，帳號(信箱)格式有誤");
                return StatusCode.ACCOUNT_INVALID_LOGIN;
            }
            if (ams311wReqDTO.getId() != null) {
                log.error("A new user cannot already have an ID,ID:{}", ams311wReqDTO.getId());
                log.error("Ams311wService-checkData-發生錯誤，建立新帳號，ID不得有值");
                return StatusCode.ACCOUNT_ID_EXISTS;
            }
            if (this.userRepository.findOneByLogin(ams311wReqDTO.getLogin()).isPresent()) {
                log.error("Ams311wService-checkData-發生錯誤，此帳號已存在");
                return StatusCode.ACCOUNT_LOGIN_EXISTS;
            }
            if (this.userRepository.findOneByEmailIgnoreCase(ams311wReqDTO.getLogin()).isPresent()) {
                log.error("Ams311wService-checkData-發生錯誤，此信箱已存在");
                return StatusCode.ACCOUNT_EMAIL_EXISTS;
            }
        } else {
            if (this.userRepository.findOneByLogin(ams311wReqDTO.getLogin().toLowerCase()).isEmpty()) {
                log.error("Ams311wService-checkData-發生錯誤，此帳號不存在");
                return StatusCode.ACCOUNT_NOT_EXISTS;
            }
        }
        return StatusCode.SUCCESS;
    }

    /**
     * 設定查詢條件。
     */
    private ExtendedUserCriteria setCriteriaFromSearch(boolean isAdminRole, Ams311wReqDTO ams311wReqDTO, JwtUserDTO jwtUserDTO) {
        ExtendedUserCriteria extendedUserCriteria = new ExtendedUserCriteria();
        if (StringUtils.isNotBlank(ams311wReqDTO.getLogin())) {
            extendedUserCriteria.setUserId(StringFilterUtils.toContainStringFilter(ams311wReqDTO.getLogin().toLowerCase().trim()));
        }
        if (StringUtils.isNotBlank(ams311wReqDTO.getUserName())) {
            extendedUserCriteria.setUserName(StringFilterUtils.toContainStringFilter(ams311wReqDTO.getUserName().trim()));
        }
        if (!isAdminRole) {
            extendedUserCriteria.setOrgId(StringFilterUtils.toEqualStringFilter(jwtUserDTO.getOrgId()));
        }

        if (StringUtils.isNotBlank(ams311wReqDTO.getBeginDate())) {
            ams311wReqDTO.setBeginDate(ams311wReqDTO.getBeginDate().replaceAll("-", "/"));
        }

        if (StringUtils.isNotBlank(ams311wReqDTO.getEndDate())) {
            ams311wReqDTO.setEndDate(ams311wReqDTO.getEndDate().replaceAll("-", "/"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        Instant beginDate = DateUtils.convertToInstant(ams311wReqDTO.getBeginDate(), formatter, true);
        Instant endDate = DateUtils.convertToInstant(ams311wReqDTO.getEndDate(), formatter, false);
        if (beginDate != null && endDate != null) {
            if (endDate.isAfter(beginDate)) {
                extendedUserCriteria.setCreateTime(InstantFilterUtils.toInstantFilterBetween("createTime", beginDate, endDate));
            } else {
                log.error("Ams311wService-setCriteriaFromSearch，結束時間不能早於起始時間。");
            }
        } else {
            extendedUserCriteria.setCreateTime(InstantFilterUtils.toInstantFilterBetween("createTime", beginDate, endDate));
        }

        log.info("Ams311wService-setCriteriaFromSearch 查詢條件 extendedUserCriteria={}", extendedUserCriteria);
        return extendedUserCriteria;
    }

    /**
     * 發送帳號啟用信、密碼重置信、未激活帳號刪除通知信by帳號管理頁面。
     */
    public StatusCode doSendMail(User user, ExtendedUser extendedUser, AccountActionType actionType, String accessToken,
            String otpCode, String otpTime) {
        try {
            MailAction mailAction = null;
            // 內嵌圖片
            Map<String, String> imagePaths = null;
            if (AccountActionType.CREATE.getCode().equals(actionType.getCode())) {
                mailAction = MailAction.ACCOUNT_CREATE;
                imagePaths = Map.of(
                        "appleStoreImgPath", this.appleStoreImgPath,
                        "googlePlayImgPath", this.googlePlayImgPath
                 );
            } else if (AccountActionType.RESET_PWD.getCode().equals(actionType.getCode())) {
                mailAction = MailAction.ACCOUNT_RESET_BWD;
            } else if (AccountActionType.DELETE_ACCOUNT.getCode().equals(actionType.getCode())) {
                mailAction = MailAction.ACCOUNT_NOT_ACTIVATED_DELETE;
            } else if (AccountActionType.RESET_ACCESS_TOKEN.getCode().equals(actionType.getCode())) {
                mailAction = MailAction.ACCOUNT_RESET_ACCESS_TOKEN;
            } else if (AccountActionType.VERIFY_OTP.getCode().equals(actionType.getCode())) {
                mailAction = MailAction.ACCOUNT_VERIFY_OTP;
            }
            log.debug("Ams311wService-doSendMail，準備發送信件，{}", mailAction.getName());
            // 確認Mail範本。
            Ams311wMailResultDTO criteriaResult = this.queryMailTemplateByCriteria(mailAction);
            if (!StatusCode.SUCCESS.getCode().equals(criteriaResult.getStatusCode().getCode())) {
                log.error("Ams311wService-doSendMail，寄送mail失敗，msg:{}", criteriaResult.getStatusCode().getMsg());
                return criteriaResult.getStatusCode();
            }
            MailTemplateDTO mailTemplateDTO = criteriaResult.getMailTemplateDTO();

            // 確認mailContent。
            String message = this.changeKeyWordForMessage(mailAction, mailTemplateDTO.getMessage(), user, extendedUser,
                    accessToken, otpCode, otpTime);
            if (message == null) {
                log.error("Ams311wService-doSendMail，寄送mail失敗，msg:{}", StatusCode.MAIL_CONTENT_ERROR.getMsg());
                return StatusCode.MAIL_CONTENT_ERROR;
            }

            // 將寄信的參數帶入NotifyUtils。
            try {
                NotifyUtils.MailBuilder mail = NotifyUtils.mail()
                    .addContact(extendedUser.getEmail())
                    .setSubject(mailTemplateDTO.getSubject())
                    .setMessage(message)
                    .addAllImagePaths(imagePaths)
                    .html(StateType.ENABLED.getCode().equals(mailTemplateDTO.getHtmlState()));
                Boolean send = mail.send();
                if (send) {
                    return StatusCode.SUCCESS;
                } else {
                    log.error("Ams311wService-doSendMail，寄送mail失敗");
                    return StatusCode.MAIL_SERVER_UNAVAILABLE;
                }
            } catch (Exception ex) {
                log.error("Ams311wService-doSendMail，發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
                return StatusCode.MAIL_UNKNOWN_ERROR;
            }
        } catch (Exception ex) {
            log.error("Ams311wService-sendMail-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return StatusCode.ACCOUNT_SEND_MAIL_ERROR;
        }
    }

    public Ams311wMailResultDTO queryMailTemplateByCriteria(MailAction mailAction) {
        MailTemplateCriteria criteria = new MailTemplateCriteria();
        criteria.setMailType(StringFilterUtils.toEqualStringFilter(mailAction.getMailType()));
        criteria.setRecipientRole(StringFilterUtils.toEqualStringFilter(mailAction.getRecipient_role()));
        criteria.setActivated(StringFilterUtils.toEqualStringFilter(StateType.ENABLED.getCode()));

        // 撈出信件範本。
        List<MailTemplateDTO> templateDTOList = this.mailTemplateQueryService.findByCriteria(criteria);
        if (templateDTOList == null || templateDTOList.isEmpty()) {
            log.error("Ams311wService-queryMailTemplateByCriteria，發生錯誤，mailTemplate不存在，criteria:{}", criteria);
            return new Ams311wMailResultDTO(StatusCode.MAIL_TEMPLATE_NOT_EXISTS);
        }
        return new Ams311wMailResultDTO(templateDTOList.get(0));
    }

    /**
     * 替換MailBody關鍵字。
     */
    private String changeKeyWordForMessage(
        MailAction mailAction,
        String content,
        User user,
        ExtendedUser extendedUser,
        String accessToken,
        String otpCode,
        String otpTime
    ) {
        String updatedContent;
        if (MailAction.ACCOUNT_CREATE.getMailType().equals(mailAction.getMailType())) {
            Instant createdDate = user.getCreatedDate().plus(1, ChronoUnit.DAYS);
            String expiryTime = DateUtils.convertZone(createdDate, ZoneId.of("UTC"), ZoneId.of("Asia/Taipei"), "[yyyy-MM-dd HH:mm]");

            updatedContent = content
                .replace("@userName", safeHtml(extendedUser.getUserName()))
                .replace("@baseURL", this.baseURL)
                .replace("@activation", user.getActivationKey())
                .replace("@rest", user.getResetKey())
                .replace("@accessToken", accessToken)
                .replace("@expiryTime", expiryTime)
                .replace("@appleStoreAppUrl", this.appleStoreAppUrl)
                .replace("@googlePlayAppUrl", this.googlePlayAppUrl);
        } else if (
            MailAction.ACCOUNT_RESET_BWD.getMailType().equals(mailAction.getMailType()) ||
            MailAction.FORGOT_PASSWORD_RESET_BWD.getMailType().equals(mailAction.getMailType())
        ) {
            updatedContent = content
                .replace("@userName", safeHtml(extendedUser.getUserName()))
                .replace("@baseURL", this.baseURL)
                .replace("@rest", user.getResetKey());
        } else if (MailAction.ACCOUNT_NOT_ACTIVATED_DELETE.getMailType().equals(mailAction.getMailType())) {
            updatedContent = content.replace("@userName", safeHtml(extendedUser.getUserName()));
        } else if (MailAction.ACCOUNT_RESET_ACCESS_TOKEN.getMailType().equals(mailAction.getMailType())) {
            updatedContent = content
                .replace("@userName", safeHtml(extendedUser.getUserName()))
                .replace("@accessToken", accessToken);
        } else if (MailAction.ACCOUNT_VERIFY_OTP.getMailType().equals(mailAction.getMailType())) {
            updatedContent = content
                .replace("@email", user.getEmail())
                .replace("@otpCode", otpCode)
                .replace("@otpTime", otpTime);
        } else {
            updatedContent = null;
        }
        return updatedContent;
    }

    private String safeHtml(String input) {
        return Encode.forHtml(input == null ? "" : input);
    }

    /**
     * 驗證信箱參數是否有包含特定字串。
     */
    private boolean isValidEmail(String email) {
        if ("admin".equals(email) || "user".equals(email)) {
            return true;
        }
        return email != null && this.validateUtils.isEmail(email);
    }

    /**
     * 驗證帳號啟用key、重置key。
     *
     * @param reqDTO
     * @return
     */
    public Ams311wValidateIdentifierResultDTO validateIdentifier(Ams311wValidateIdentifierReqDTO reqDTO) {
        Ams311wValidateIdentifierResultDTO ams311wValidateIdentifierResultDTO = new Ams311wValidateIdentifierResultDTO();
        if (!StringUtils.isNotBlank(reqDTO.getActivationKey()) || !StringUtils.isNotBlank(reqDTO.getResetKey())) {
            log.warn("Ams311wService-validateIdentifier-發生錯誤,驗證金鑰不得無值，ActivationKey:{}，ResetKey:{}",
                    reqDTO.getActivationKey(), reqDTO.getResetKey());
            ams311wValidateIdentifierResultDTO.setStatusCode(StatusCode.ACCOUNT_VALIDATE_KEY_FAIL);
            return ams311wValidateIdentifierResultDTO;
        }

        Optional<User> optionalUser = this.customUserRepository
                .findOneByActivationKeyAndResetKey(reqDTO.getActivationKey(), reqDTO.getResetKey());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (this.isOnTime(user)) {
                Optional<ExtendedUser> extendedUserOp = extendedUserRepository.findOneByUserId(user.getLogin());
                if (extendedUserOp.isEmpty()) {
                    log.info("Ams311wService-validateIdentifier Extended User table 找不到該筆資料 loginId={}",
                            user.getLogin());
                    ams311wValidateIdentifierResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
                    return ams311wValidateIdentifierResultDTO;
                }
                log.info("Ams311wService-validateIdentifier,驗證金鑰已通過。");
                ExtendedUserDTO extendedUserDTO = extendedUserMapper.toDto(extendedUserOp.get());
                Ams311wValidateIdentifierResDTO ams311wValidateIdentifierResDTO = new Ams311wValidateIdentifierResDTO();
                ams311wValidateIdentifierResDTO.setUserId(extendedUserDTO.getUserId());
                ams311wValidateIdentifierResDTO.setUserName(extendedUserDTO.getUserName());
                ams311wValidateIdentifierResultDTO.setAms311wValidateIdentifierResDTO(ams311wValidateIdentifierResDTO);
                ams311wValidateIdentifierResultDTO.setStatusCode(StatusCode.SUCCESS);
            } else {
                log.warn("Ams311wService-validateIdentifier-發生錯誤,驗證金鑰已過期，req:{}", reqDTO);
                ams311wValidateIdentifierResultDTO.setStatusCode(StatusCode.ACCOUNT_VALIDATE_KEY_EXPIRED);
            }
        } else {
            log.warn("Ams311wService-validateIdentifier-發生錯誤,驗證金鑰不通過，ActivationKey:{}，ResetKey:{}",
                    reqDTO.getActivationKey(), reqDTO.getResetKey());
            ams311wValidateIdentifierResultDTO.setStatusCode(StatusCode.ACCOUNT_VALIDATE_KEY_FAIL);
        }
        return ams311wValidateIdentifierResultDTO;
    }

    /**
     * 帳號啟用。
     */
    public StatusCode finishActivateAccount(Ams311wActivateReqDTO reqDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        StatusCode result = this.checkDataToActivate(reqDTO);
        if (StatusCode.SUCCESS.getCode().equals(result.getCode())) {
            ExtendedUser extendedUser = this.activateUserAccount(reqDTO);
            if (extendedUser != null) {
                // 紀錄帳號異動
                Optional<User> userOptional = userRepository.findOneByLogin(extendedUser.getUserId());
                userOptional.ifPresent(user -> this.saveAccountLog(userOptional.get(), extendedUser, LogType.MOD));
                return StatusCode.SUCCESS;
            }
        } else {
            return result;
        }
        return null;
    }

    /**
     * 使用者登入後取得可視SideLink列表
     *
     * @param userLogin
     * @return
     */
    public Set<String> getSideLink(String userLogin) {
        log.info("Ams311wService-getSideLink 取得登入者可視SideLink列表");

        Set<String> sideLinkList = new HashSet<>();

        User user = new User();
        user.setLogin(userLogin);
        List<RoleDTO> roleDTOList = relService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);
        for (RoleDTO roleDTO : roleDTOList) {
            List<ResDTO> resDTOList = relService.roleAction(roleDTO, null, AuthorityAction.ROLE_SEARCH_RES);
            for (ResDTO resDTO : resDTOList) {
                sideLinkList.add(resDTO.getWebUrl());
            }
        }

        log.info("Ams311wService-getSideLink 取得登入者可視SideLink列表完畢 sideLink={}", sideLinkList);
        return sideLinkList;
    }

    /**
     * 查詢個人資料
     *
     * @param userId
     * @return
     */
    public Ams311wCurrentUserResultDTO queryPersonalInfo(String userId) {
        log.info("Ams311wService-queryPersonalInfo 開始取得登入者個人資料");
        try {
            Ams311wCurrentUserResultDTO ams311wCurrentUserResultDTO = new Ams311wCurrentUserResultDTO();

            // 依據取得的登入帳號搜尋 Extended User table 資料
            Optional<ExtendedUser> extendedUserOp = extendedUserRepository.findOneByUserId(userId);
            if (extendedUserOp.isEmpty()) {
                log.info("Ams311wService-queryPersonalInfo Extended User table 找不到該筆資料");
                ams311wCurrentUserResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
                return ams311wCurrentUserResultDTO;
            }

            Optional<Org> orgOp = orgRepository.findByOrgId(extendedUserOp.get().getOrgId());
            OrgDTO orgDTO = orgMapper.toDto(orgOp.get());

            // 整理資料, 回傳當前登入使用者資料
            ExtendedUserDTO extendedUserDTO = extendedUserMapper.toDto(extendedUserOp.get());
            Ams311wCurrentUserResDTO ams311wCurrentUserResDTO = this.toCurrentUserDTO(extendedUserDTO, orgDTO);
            ams311wCurrentUserResultDTO.setAms311wCurrentUserResDTO(ams311wCurrentUserResDTO);
            ams311wCurrentUserResultDTO.setStatusCode(StatusCode.SUCCESS);

            return ams311wCurrentUserResultDTO;
        } catch (Exception ex) {
            log.error("Ams311wService-queryPersonalInfo 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 更新個人資料
     *
     * @param ams311wCurrentUserReqDTO
     * @return
     */
    public Ams311wCurrentUserResultDTO profileSettings(Ams311wCurrentUserReqDTO ams311wCurrentUserReqDTO) {
        try {
            Ams311wCurrentUserResultDTO ams311wCurrentUserResultDTO = new Ams311wCurrentUserResultDTO();

            // 檢查請求要更新的欄位
            StatusCode statusCode = this.checkCurrentUserUpdateData(ams311wCurrentUserReqDTO);
            if (statusCode != StatusCode.SUCCESS) {
                ams311wCurrentUserResultDTO.setStatusCode(statusCode);
                return ams311wCurrentUserResultDTO;
            }

            String userId = ams311wCurrentUserReqDTO.getUserId();
            // 依據帳號搜尋使用者資料(user table)
            Optional<User> userOp = userRepository.findOneByLogin(userId);
            if (userOp.isEmpty()) {
                log.info("Ams311wService-profileSettings user table 找不到該筆資料");
                ams311wCurrentUserResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
                return ams311wCurrentUserResultDTO;
            }

            // 依據帳號搜尋 Extended User table 資料
            Optional<ExtendedUser> extendedUserOp = extendedUserRepository.findOneByUserId(userId);
            if (extendedUserOp.isEmpty()) {
                log.info("Ams311wService-profileSettings Extended User table 找不到該筆資料");
                ams311wCurrentUserResultDTO.setStatusCode(StatusCode.ACCOUNT_NOT_EXISTS);
                return ams311wCurrentUserResultDTO;
            }

            Optional<Org> orgOp = orgRepository.findByOrgId(extendedUserOp.get().getOrgId());
            OrgDTO orgDTO = orgMapper.toDto(orgOp.get());

            // 更新 Extended User
            ExtendedUserDTO extendedUserDTO = extendedUserMapper.toDto(extendedUserOp.get());
            extendedUserDTO.setUserName(ams311wCurrentUserReqDTO.getUserName());
            extendedUserDTO.setTel(ams311wCurrentUserReqDTO.getTel());
            customExtendedUserService.save(extendedUserDTO);

            // 更新 User
            User user = userOp.get();
            user.setLastModifiedBy(user.getLogin());
            user.setLastModifiedDate(Instant.now());
            customUserService.update(user);

            // 新增帳號異動紀錄
            this.saveAccountLog(user, extendedUserOp.get(), LogType.MOD);

            // 整理回傳資訊
            ams311wCurrentUserResultDTO.setStatusCode(StatusCode.SUCCESS);
            Ams311wCurrentUserResDTO ams311wCurrentUserResDTO = this.toCurrentUserDTO(extendedUserDTO, orgDTO);
            ams311wCurrentUserResultDTO.setAms311wCurrentUserResDTO(ams311wCurrentUserResDTO);

            return ams311wCurrentUserResultDTO;
        } catch (Exception ex) {
            log.error("Ams311wService-profileSettings 發生錯誤, 錯誤原因為={}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 刪除帳號
     */
    @Transactional
    public StatusCode deleteAccount(Ams311wReqDTO ams311wReqDTO) {
        // 取得帳號(User)
        Optional<User> optionalUser = userRepository.findOneByLogin(ams311wReqDTO.getLogin());
        if (optionalUser.isEmpty()) {
            log.error(
                "[{}-deleteAccount]，{}",
                ENTITY_NAME,
                StatusCode.ACCOUNT_DELETE_USER_NOT_EXISTS.getMsg()
            );
            return StatusCode.ACCOUNT_DELETE_USER_NOT_EXISTS;
        }
        User user = optionalUser.get();

        // 刪除REL關聯
        this.customRelService.accountAction(user, new ArrayList<>(), AuthorityAction.ACCOUNT_DELETE_ROLE);

        // 紀錄帳號異動、刪除ExtendedUser
        Optional<ExtendedUserDTO> optionalExtendedUserDTO = this.customExtendedUserService.findOneByUserId(user.getLogin());
        optionalExtendedUserDTO.ifPresent(extendedUserDTO -> {
            ExtendedUser extendedUser = extendedUserMapper.toEntity(extendedUserDTO);
            this.saveAccountLog(user, extendedUser, LogType.DEL);
            this.customExtendedUserService.delete(extendedUserDTO.getId());
        });

        // 刪除User
        this.customUserService.deleteUser(user.getLogin());

        // 刪除BwdHistory
        BwdHistoryCriteria bwdHistoryCriteria = new BwdHistoryCriteria();
        bwdHistoryCriteria.setUserId(StringFilterUtils.toEqualStringFilter(user.getLogin()));
        List<BwdHistoryDTO> bwdHistoryDTOList = this.customBwdHistoryQueryService.findByCriteria(bwdHistoryCriteria);
        for (BwdHistoryDTO dto : bwdHistoryDTOList) {
            this.bwdHistoryService.delete(dto.getId());
        }

        // 刪除LoginCount
        LoginCountCriteria loginCountCriteria = new LoginCountCriteria();
        loginCountCriteria.setUserId(StringFilterUtils.toEqualStringFilter(user.getLogin()));
        List<LoginCountDTO> loginCountDTOList = this.customLoginCountQueryService.findByCriteria(loginCountCriteria);
        if (!loginCountDTOList.isEmpty()) {
            this.loginCountService.delete(loginCountDTOList.get(0).getId());
        }

        // 刪除AccessToken
        customAccessTokenService.deleteAccessToken(user.getLogin());

        return StatusCode.SUCCESS;
    }

    /**
     * 每日凌晨1時執行
     * 刪除未激活帳號(超過三天)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void removeNotActivatedUsers() {
        if (switchRemoveNotActivatedUsers) {
            // 查詢未激活且創建時間超過3天的用戶
            List<User> usersToDelete = userRepository.findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(
                    Instant.now().minus(3, ChronoUnit.DAYS)
                    );

            if (!usersToDelete.isEmpty()) {
                // 記錄總數
                log.info("[CustomUserService-removeNotActivatedUsers]，尋找到超過三天未激活的帳號需要刪除，數量為:{}。", usersToDelete.size());

                // 刪除每一個用戶
                usersToDelete.forEach(user -> {
                    userRepository.delete(user);
                    // 根據login來查詢並刪除extended_user表中的資料
                    Optional<ExtendedUser> optionalExtendedUser = customExtendedUserRepository.findOneByUserId(user.getLogin());
                    if (optionalExtendedUser.isPresent()) {
                        customExtendedUserRepository.deleteByUserId(user.getLogin());
                        this.doSendMail(user, optionalExtendedUser.get(), AccountActionType.DELETE_ACCOUNT, null, null, null);
                        log.info("[CustomUserService-removeNotActivatedUsers]，使用者帳號超過三天未激活，刪除使用者帳戶:{}", user.getLogin());
                    }
                    // 刪除REL關聯
                    this.customRelService.accountAction(user, new ArrayList<>(), AuthorityAction.ACCOUNT_DELETE_ROLE);
                });
            } else {
                log.info("[CustomUserService-removeNotActivatedUsers]，目前沒有超過三天未激活的帳號需要刪除。");
            }
        }
    }

    /**
     * 重新寄送帳號啟用信
     */
    @Transactional
    public StatusCode reSendActivationEmail(Long id, String userLogin) {
        if (id == null) {
            log.error("Ams311wResource-reSendActivationEmail-發生錯誤，啟用帳號Id取得異常。");
            return StatusCode.ACCOUNT_REACTIVATION_USER_ID_IS_NULL;
        }

        Optional<User> optionalUser = this.userRepository.findOneById(id);

        if (optionalUser.isEmpty()) {
            log.error(
                "Ams311wResource-reSendActivationEmail-發生錯誤，{}，user id:{}",
                StatusCode.ACCOUNT_REACTIVATION_LOGIN_NOT_FUND.getMsg(),
                id
            );
            return StatusCode.ACCOUNT_REACTIVATION_LOGIN_NOT_FUND;
        }

        String login = optionalUser.get().getLogin();

        Optional<ExtendedUser> optionalExtendedUser = this.customExtendedUserRepository.findOneByUserId(login);
        Optional<AccessToken> optionalAccessToken = customAccessTokenRepository.findFirstByOwnerOrderByCreateTimeDesc(login);

        if (optionalExtendedUser.isEmpty() || optionalAccessToken.isEmpty()) {
            log.error(
                "Ams311wResource-reSendActivationEmail-發生錯誤，{}",
                StatusCode.ACCOUNT_REACTIVATION_LOGIN_NOT_FUND.getMsg()
            );
            return StatusCode.ACCOUNT_REACTIVATION_LOGIN_NOT_FUND;
        }

        User user = optionalUser.get();
        ExtendedUser extendedUser = optionalExtendedUser.get();
        AccessToken accessToken = optionalAccessToken.get();

        if ((StringUtils.isBlank(user.getActivationKey())) || (StringUtils.isBlank(user.getResetKey()))) {
            log.error(
                "Ams311wResource-reSendActivationEmail-發生錯誤，{}，activationKey:{}，resetKey:{}",
                StatusCode.ACCOUNT_REACTIVATION_INVALID_CONDITION.getMsg(),
                user.getActivationKey(),
                user.getResetKey()
            );
            return StatusCode.ACCOUNT_REACTIVATION_INVALID_CONDITION;
        }

        Instant currentTime = Instant.now();

        user.setActivationKey(RandomUtil.generateActivationKey());
        user.setResetKey(RandomUtil.generateResetKey());
        user.setActivated(false);
        user.setLastModifiedBy(userLogin);
        user.setLastModifiedDate(currentTime);
        user.setResetDate(currentTime);
        User save = this.userRepository.save(user);

        this.doSendMail(save, extendedUser, AccountActionType.CREATE, accessToken.getAccessToken(), null, null);

        return StatusCode.SUCCESS;
    }

    /**
     * 補發新 Access Token
     *
     * @return
     */
    public Ams311wResultDTO resetAccessToken(String login) {
        // 取得當前帳號Access Token
        Optional<AccessToken> optionalAccessToken = customAccessTokenRepository
                .findFirstByOwnerOrderByCreateTimeDesc(login);
        if (optionalAccessToken.isPresent()) {
            // 重新補發新 Access Token
            AccessToken accessTokenEntity = optionalAccessToken.get();
            accessTokenEntity.setAccessToken(customAccessTokenService.createUniqueAccessToken());
            accessTokenEntity.setCreateTime(this.getCurrentTimeInLocalZone());
            AccessToken accessTokenResult = customAccessTokenRepository.save(accessTokenEntity);
            if (null == accessTokenResult) {
                return new Ams311wResultDTO(StatusCode.ACCOUNT_NOT_EXISTS);
            }

            Optional<ExtendedUser> extendedUserOpt = this.customExtendedUserRepository.findOneByUserId(login);
            if (extendedUserOpt.isEmpty()) {
                return new Ams311wResultDTO(StatusCode.ACCOUNT_NOT_EXISTS);
            }

            // 寄送 email
            String accessToken = accessTokenResult.getAccessToken();
            ExtendedUser extendedUser = extendedUserOpt.get();
            StatusCode mailResult = this.doSendMail(null, extendedUser, AccountActionType.RESET_ACCESS_TOKEN, accessToken, null, null);
            if (!StatusCode.SUCCESS.getCode().equals(mailResult.getCode())) {
                return new Ams311wResultDTO(mailResult);
            }
            // 新增帳號異動紀錄
            this.saveAccountLog(null, extendedUser, LogType.MOD);

            return new Ams311wResultDTO(StatusCode.SUCCESS);
        } else {
            log.warn("Ams311wService-resetAccessToken 查詢帳號Access Token時, 找不到該帳號資料, 無法設定");
            return new Ams311wResultDTO(StatusCode.ACCOUNT_NOT_EXISTS);
        }
    }

    /**
     * 發送 OTP
     * 
     * @param request
     */
    public void generateOtp(GenerateOtpReqDTO request) {
        // 先判斷是否需要驗證 OTP
        if (!switchVerifyOtp) {
            return;
        }

        String admin = "admin";
        String email = StringUtils.trim(request.getEmail()).toLowerCase();

        Optional<User> userOpt = userRepository.findOneByLogin(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("請檢查帳號是否正確");
        }
        User user = userOpt.get();

        // 檢核帳號是否啟用
        if (!user.isActivated()) {
            throw new RuntimeException("帳號目前已停用");
        }

        Optional<ExtendedUser> extendedUserOpt = this.customExtendedUserRepository.findOneByUserId(email);
        if (extendedUserOpt.isEmpty()) {
            throw new RuntimeException("請檢查帳號是否正確");
        }
        ExtendedUser extendedUser = extendedUserOpt.get();

        // 檢核是否為 AP 帳號
        if ("AP".equalsIgnoreCase(extendedUser.getUserTypeId())) {
            throw new RuntimeException("AP 帳號無法進行登入");
        }

        // 產生 OTP，如為 admin 依照 YYMMDD 規則生成 OTP
        String otpCode;
        if (StringUtils.equalsIgnoreCase(admin, email)) {
            otpCode = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        } else {
            otpCode = this.getSecureRandomNumber(6);
        }

        // 寫入 DB OTPVerify
        Instant now = Instant.now();
        OtpVerify otpVerify = new OtpVerify();
        Optional<OtpVerify> otpVerifyOpt = customOtpVerifyRepository.findByEmail(email);
        if (otpVerifyOpt.isPresent()) {
            OtpVerify otpVerifyDB = otpVerifyOpt.get();
            Instant otpReSendTimeInstant = otpVerifyDB.getCreateTime().plusSeconds(otpReSendTime);
            // 檢核 1 分鐘後才能重發 OTP，如為 admin 不檢核
            if (!StringUtils.equalsIgnoreCase(admin, email) && now.isBefore(otpReSendTimeInstant)) {
                throw new RuntimeException("發送 OTP 失敗，請稍後再試");
            }

            otpVerify = otpVerifyDB;
        } else {
            otpVerify.setEmail(email);
        }

        // OTP 過期時間
        Instant otpExpireTimeInstant = now.plusSeconds(otpExpireTime);
        String otpExpireMinute = String.valueOf(otpExpireTime / 60);

        otpVerify.setOtpToken(otpCode);
        otpVerify.setOtpTokenExpired(otpExpireTimeInstant);
        otpVerify.setCreateTime(now);
        otpVerify.setIsPass(false);
        customOtpVerifyRepository.save(otpVerify);

        // 發送 OTP Email，如為 admin 不發信
        if (!StringUtils.equalsIgnoreCase(admin, otpVerify.getEmail())) {
            this.doSendMail(user, extendedUser, AccountActionType.VERIFY_OTP, null, otpCode, otpExpireMinute);
        }
    }

    /**
     * 驗證 OTP
     * 
     * @param request
     */
    public void verifyOtp(VerifyOtpReqDTO request) {
        String email = StringUtils.trim(request.getEmail()).toLowerCase();
        OtpVerify otpVerify = customOtpVerifyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP 驗證失敗"));

        // OTP 已過期 or 輸入錯誤
        if (Instant.now().isAfter(otpVerify.getOtpTokenExpired())
                || !StringUtils.equals(request.getOtpToken(), otpVerify.getOtpToken())) {
            throw new RuntimeException("OTP 驗證失敗");
        }

        // 驗證通過 update is_pass
        otpVerify.setIsPass(true);
        customOtpVerifyRepository.save(otpVerify);
    }

    /**
     * 檢核 OTP 是否已驗證成功
     * 
     * @param email
     */
    public void verifyOtpIsPass(String email) {
        // 先判斷是否需要驗證 OTP
        if (!switchVerifyOtp) {
            return;
        }

        customOtpVerifyRepository.findByEmailAndIsPassTrue(email).orElseThrow(() -> new RuntimeException("帳號(電子郵件)驗證失敗，請完成驗證後再登入"));
    }

    /**
     * 更新 OtpVerify
     * 
     * @param email
     */
    public void updateOtpVerify(String email) {
        Optional<OtpVerify> otpVerifyOpt = customOtpVerifyRepository.findByEmail(email);
        if (otpVerifyOpt.isPresent()) {
            OtpVerify otpVerify = otpVerifyOpt.get();
            otpVerify.setIsPass(false);
            customOtpVerifyRepository.save(otpVerify);
        }
    }

    /**
     * 啟用帳號、儲存登入密碼、重置使用者登入失敗次數、紀錄歷史密碼、清除啟用及重置Key。
     */
    private ExtendedUser activateUserAccount(Ams311wActivateReqDTO reqDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<User> optionalUser =
            this.customUserRepository.findOneByActivationKeyAndResetKey(reqDTO.getActivationKey(), reqDTO.getResetKey());
        if (optionalUser.isPresent()) {
            Optional<ExtendedUser> optionalExtendedUser = extendedUserRepository.findOneByUserId(optionalUser.get().getLogin());
            if (optionalExtendedUser.isPresent()) {
                String newBwd = RSAUtils.privateDecrypt(reqDTO.getNewBwd(), RSAUtils.getPrivateKey(this.privateKey));
                Instant currentTime = this.getCurrentTimeInLocalZone();
                String cipher = this.passwordEncoder.encode(newBwd);
                // 重置使用者登入失敗次數。
                this.ams302wService.resetLoginCount(optionalUser.get().getLogin(), false);

                // 修改密碼紀錄。
                this.logBwdHistory(optionalUser.get().getLogin(), cipher, currentTime);

                optionalUser.get().setPassword(cipher);
                optionalUser.get().setActivated(true);
                optionalUser.get().setResetKey(null);
                optionalUser.get().setActivationKey(null);
                optionalUser.get().setResetDate(null);
                optionalUser.get().setLastModifiedBy(optionalUser.get().getLogin());
                optionalUser.get().setLastModifiedDate(currentTime);
                this.userRepository.save(optionalUser.get());

                optionalExtendedUser.get().setState("1");
                ExtendedUser saveExtendedUserResult = this.extendedUserRepository.save(optionalExtendedUser.get());

                log.info("Ams311wService-activateUserAccount,帳號啟用已成功");
                return saveExtendedUserResult;
            } else {
                log.error(
                    "Ams311wService-finishActivateAccount-發生錯誤，查詢不到extendedUser，ActivationKey:{}，ResetKey:{}",
                    reqDTO.getActivationKey(),
                    reqDTO.getResetKey()
                );
                return null;
            }
        } else {
            log.error("Ams311wService-finishActivateAccount-發生錯誤，UserId查詢不到extendedUser，optionalUser:{}", optionalUser);
            return null;
        }
    }

    /**
     * 驗證帳號啟用時設定的登入密碼。
     */
    private StatusCode checkDataToActivate(Ams311wActivateReqDTO reqDTO) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Optional<User> optionalUser =
            this.customUserRepository.findOneByActivationKeyAndResetKey(reqDTO.getActivationKey(), reqDTO.getResetKey());
        if (optionalUser.isPresent()) {
            if (!StringUtils.isNotBlank(reqDTO.getNewBwd())) {
                log.warn("Ams311wService-checkDataToActivate-發生錯誤,新密碼輸入不正確，reqDTO:{}", reqDTO);
                return StatusCode.ACCOUNT_NEW_BWD_ERROR;
            }
            String newBwd = RSAUtils.privateDecrypt(reqDTO.getNewBwd(), RSAUtils.getPrivateKey(this.privateKey));

            if (!this.ams302wService.checkBwdLength(newBwd)) {
                log.warn("Ams311wService-checkDataToActivate-發生錯誤,新密碼長度不正確，newBwd:{}", newBwd);
                return StatusCode.ACCOUNT_NEW_BWD_ERROR;
            }
            // 檢查密碼原則。
            return this.checkBwdRule(optionalUser.get().getLogin(), newBwd, true);
        } else {
            log.warn("Ams311wService-checkDataToActivate-發生錯誤,新密碼未通過密碼檢查原則，reqDTO:{}", reqDTO);
            return StatusCode.ACCOUNT_VALIDATE_KEY_FAIL;
        }
    }

    /**
     * 檢查 重置key 時間有效性
     * (一日內)
     */
    private boolean isOnTime(User user) {
        if (user == null || user.getResetDate() == null) {
            return false;
        }
        Instant resetDate = user.getResetDate();
        Instant now = Instant.now();
        return now.isBefore(resetDate.plus(1, ChronoUnit.DAYS));
    }

    /**
     * 驗證密碼是否符合密碼原則。
     */
    private StatusCode checkBwdRule(String userId, String newBwd, boolean isUrlReset) {
        // 取得密碼原則。
        List<BwdParam> rules = this.bwdParamCustomService.getBwdRule(BwdProfileType.DEFAULT, false);

        Map<String, BwdParam> ruleIdMap = new HashMap<>();
        for (BwdParam bwdParam : rules) {
            ruleIdMap.put(bwdParam.getRuleId(), bwdParam);
        }
        // 檢查及取得錯誤訊息。
        String msg = this.bwdParamCustomService.ruleCheck(userId, newBwd, ruleIdMap, isUrlReset, true);

        if (null != msg) {
            log.info("Ams311wService-checkBwdRule，密碼原則檢驗未通過，原因:{}", msg);
            return StatusCode.ACCOUNT_NEW_BWD_ERROR;
        } else {
            return StatusCode.SUCCESS;
        }
    }

    /**
     * 儲存密碼log。
     */
    private void logBwdHistory(String userId, String encodeBwd, Instant instant) {
        BwdHistoryDTO bwdHistoryDTO = new BwdHistoryDTO();
        bwdHistoryDTO.setUserId(userId);
        bwdHistoryDTO.setBwdHash(encodeBwd);
        bwdHistoryDTO.setCreateTime(instant);
        BwdHistoryDTO saveResult = this.bwdHistoryService.save(bwdHistoryDTO);
        log.info("Ams311wService-logBwdHistory,儲存帳號歷史密碼完成，userId:{}。", saveResult.getUserId());
    }

    /**
     * 取得本地時區Instant。
     */
    public Instant getCurrentTimeInLocalZone() {
        // 獲取當前的Instant。
        Instant now = Instant.now();
        // 定義本地時區(Asia/Taipei)。
        ZoneId localZoneId = ZoneId.of("Asia/Taipei");
        // 將Instant轉換為本地時區的ZonedDateTime
        ZonedDateTime localTime = now.atZone(localZoneId);
        // 返回本地時區的時間，轉換為Instant
        return localTime.toInstant();
    }

    /**
     * 新增帳號異動紀錄
     *
     * @param user         data
     * @param extendedUser data
     * @param logType      異動類型
     */
    private void saveAccountLog(User user, ExtendedUser extendedUser, LogType logType) {
        log.info(
            "Ams311wService-saveAccountLog 開始紀錄帳號{}異動 LogType={}",
            logType.getName(),
            logType
        );

        ExtendedUserLogDTO extendedUserLogDTO = this.toExtendedUserLogDTO(user, extendedUser, logType.getCode());
        extendedUserLogService.save(extendedUserLogDTO);

        log.info(
            "Ams311wService-saveAccountLog 帳號{}異動紀錄完成 LogType={}",
            logType.getName(),
            logType
        );
    }

    private ExtendedUserLogDTO toExtendedUserLogDTO(User user, ExtendedUser extendedUser, String logTypeCode) {
        ExtendedUserLogDTO extendedUserLogDTO = new ExtendedUserLogDTO();
        extendedUserLogDTO.setActor(SecurityUtils.getCurrentUserLogin().get());
        extendedUserLogDTO.setLogType(logTypeCode);
        extendedUserLogDTO.setLogTime(Instant.now());
        extendedUserLogDTO.setOrgId(extendedUser.getOrgId());
        extendedUserLogDTO.setUserId(extendedUser.getUserId());
        extendedUserLogDTO.setUserName(extendedUser.getUserName());
        extendedUserLogDTO.setEmail(extendedUser.getEmail());
        extendedUserLogDTO.setPhone(extendedUser.getPhone());
        extendedUserLogDTO.setTel(extendedUser.getTel());
        extendedUserLogDTO.setEmployeeId(extendedUser.getEmployeeId());
        extendedUserLogDTO.setEmployeeTypeId(extendedUser.getEmployeeTypeId());
        extendedUserLogDTO.setLeftDate(extendedUser.getLeftDate());
        extendedUserLogDTO.setOnboardDate(extendedUser.getOnboardDate());
        extendedUserLogDTO.setUserTypeId(extendedUser.getUserTypeId());
        extendedUserLogDTO.setDataRole1(extendedUser.getDataRole1());
        extendedUserLogDTO.setDataRole2(extendedUser.getDataRole2());
        extendedUserLogDTO.setState(extendedUser.getState());
        extendedUserLogDTO.setCreateTime(extendedUser.getCreateTime());
        extendedUserLogDTO.setAuthChangeTime(extendedUser.getAuthChangeTime());
        extendedUserLogDTO.setPwdResetTime(extendedUser.getPwdResetTime());

        if (user != null) {
            extendedUserLogDTO.setEmail(user.getEmail());
        }

        return extendedUserLogDTO;
    }

    private Ams311wCurrentUserResDTO toCurrentUserDTO(ExtendedUserDTO extendedUserDTO, OrgDTO orgDTO) {
        Ams311wCurrentUserResDTO ams311wCurrentUserDTO = new Ams311wCurrentUserResDTO();
        ams311wCurrentUserDTO.setUserId(extendedUserDTO.getUserId());
        ams311wCurrentUserDTO.setUserName(extendedUserDTO.getUserName());
        ams311wCurrentUserDTO.setOrgId(extendedUserDTO.getOrgId());
        ams311wCurrentUserDTO.setOrgTwName(orgDTO.getOrgTwName());
        ams311wCurrentUserDTO.setTel(extendedUserDTO.getTel());
        ams311wCurrentUserDTO.setState(extendedUserDTO.getState());
        ams311wCurrentUserDTO.setUserTypeId(extendedUserDTO.getUserTypeId());

        UserTypeIdType type = UserTypeIdType.toUserTypeIdType(extendedUserDTO.getUserTypeId());
        if (type != null) {
            ams311wCurrentUserDTO.setUserTypeName(type.getName());
        } else {
            ams311wCurrentUserDTO.setUserTypeName(extendedUserDTO.getUserTypeId());
        }

        if (StringUtils.equals(commonAccountOrgId, extendedUserDTO.getOrgId())) {
            ams311wCurrentUserDTO.setApplyUserTypeName(ApplyType.GENERAL_ACCOUNT.getName());
        } else {
            ams311wCurrentUserDTO.setApplyUserTypeName(ApplyType.ADVANCED_ACCOUNT.getName());
        }

        return ams311wCurrentUserDTO;
    }

    /**
     * 檢查請求傳送過來的個人資料設定更新資料是否正確
     *
     * @return
     */
    private StatusCode checkCurrentUserUpdateData(Ams311wCurrentUserReqDTO ams311wCurrentUserReqDTO) {
        // 姓名
        if (StringUtils.isEmpty(ams311wCurrentUserReqDTO.getUserName())) {
            log.info("Ams311wService-checkCurrentUserUpdateData 姓名為必填欄位");
            return StatusCode.REQUEST_MISSING_REQUIRED_PARAM;
        }

        // 聯絡電話
        if (StringUtils.isEmpty(ams311wCurrentUserReqDTO.getTel())) {
            log.info("Ams311wService-checkCurrentUserUpdateData 聯絡電話為必填欄位");
            return StatusCode.REQUEST_MISSING_REQUIRED_PARAM;
        }

        return StatusCode.SUCCESS;
    }

    public Instant convertToInstant(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        // 1.解析日期字串為LocalDate
        LocalDate localDate = LocalDate.parse(dateStr, FORMATTER);
        // 2.將LocalDate轉換為LocalDateTime(假設時間為午夜)。
        LocalDateTime localDateTime = localDate.atStartOfDay();
        // 3.將LocalDateTime轉換為Instant(假設使用系統預設時區)。
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * 生成 0-9 的隨機數字
     * 
     * @param length 隨機數字長度
     * @return
     */
    private String getSecureRandomNumber(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder result = new StringBuilder();

        for (int i = 1; i <= length; i++) {
            // 生成 0-9 的隨機數字
            int digit = secureRandom.nextInt(10);
            result.append(digit);
        }
        return result.toString();
    }

}
