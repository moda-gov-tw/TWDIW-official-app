package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.ExtendedUser;
import gov.moda.dw.manager.repository.ExtendedUserRepository;
import gov.moda.dw.manager.service.ExtendedUserService;
import gov.moda.dw.manager.service.dto.ExtendedUserDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wReqDTO;
import gov.moda.dw.manager.service.mapper.ExtendedUserMapper;
import gov.moda.dw.manager.type.AccountActionType;
import gov.moda.dw.manager.type.StateType;

/**
 * Service Implementation for managing {@link ExtendedUser}.
 */
@Service
@Transactional
public class CustomExtendedUserService extends ExtendedUserService {

    private final Logger log = LoggerFactory.getLogger(CustomExtendedUserService.class);

    private final ExtendedUserRepository extendedUserRepository;

    private final ExtendedUserMapper extendedUserMapper;

    public CustomExtendedUserService(ExtendedUserRepository extendedUserRepository, ExtendedUserMapper extendedUserMapper) {
        super(extendedUserRepository, extendedUserMapper);
        this.extendedUserRepository = extendedUserRepository;
        this.extendedUserMapper = extendedUserMapper;
    }

    /**
     * 根據組織ID查詢所有使用者
     */
    public List<ExtendedUser> findAllByOrgId(String orgId) {
        return extendedUserRepository.findAllByOrgId(orgId);
    }

    /**
     * 建立使用者帳號，extended_user。
     */
    public ExtendedUser saveExtendedUser(Ams311wReqDTO ams311wReqDTO) {
        try {
            Instant currentTime = this.getCurrentTimeInTaiwan();

            ExtendedUser extendedUser = new ExtendedUser();

            extendedUser.setUserId(ams311wReqDTO.getLogin().toLowerCase());
            extendedUser.setUserName(ams311wReqDTO.getUserName());
            extendedUser.setEmail(ams311wReqDTO.getLogin().toLowerCase());
            extendedUser.setState(StateType.DISABLED.getCode());
            extendedUser.setCreateTime(currentTime);
            extendedUser.setTel(ams311wReqDTO.getTel());
            extendedUser.setUserTypeId(ams311wReqDTO.getUserTypeId());
            extendedUser.setOrgId(ams311wReqDTO.getOrgId());
            extendedUser.setDataRole1(ams311wReqDTO.getDataCategory1());

            ExtendedUser extendedSave = this.extendedUserRepository.save(extendedUser);
            log.info("CustomExtendedUserService-saveExtendedUser，更新帳號完成");
            return extendedSave;
        } catch (Exception ex) {
            log.error("CustomExtendedUserService-saveExtendedUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 更新使用者帳號，extended_user。
     */
    public ExtendedUser updateExtendedUser(Ams311wReqDTO ams311wReqDTO, AccountActionType actionType, Boolean isState) {
        try {
            Optional<ExtendedUser> dbExtendedUser = this.extendedUserRepository.findOneByUserId(ams311wReqDTO.getLogin());

            if (dbExtendedUser.isPresent()) {
                // 重置密碼ExtendedUser只更新紀錄時間
                if (AccountActionType.RESET_PWD.equals(actionType)) {
                    Instant currentTime = this.getCurrentTimeInTaiwan();
                    dbExtendedUser.get().setPwdResetTime(currentTime);
                    ExtendedUser update = this.extendedUserRepository.save(dbExtendedUser.get());
                    log.info("CustomExtendedUserService-updateExtendedUser，更新帳號完成");
                    return update;
                }
                // 變更授權角色ExtendedUser更新紀錄時間
                if (AccountActionType.CHANGE_ROLE.equals(actionType)) {
                    Instant currentTime = this.getCurrentTimeInTaiwan();
                    dbExtendedUser.get().setAuthChangeTime(currentTime);
                    ExtendedUser update = this.extendedUserRepository.save(dbExtendedUser.get());
                    log.info("CustomExtendedUserService-updateExtendedUser，更新帳號完成");
                    return update;
                }
                if (AccountActionType.UPDATE.equals(actionType)) {
                    dbExtendedUser.get().setUserName(ams311wReqDTO.getUserName());
                    dbExtendedUser.get().setOrgId(ams311wReqDTO.getOrgId());
                    dbExtendedUser.get().setTel(ams311wReqDTO.getTel());
                    dbExtendedUser.get().setUserTypeId(ams311wReqDTO.getUserTypeId());
                }

                if (AccountActionType.CHANGE_ACTIVATED.equals(actionType)) {
                    dbExtendedUser.get().setState(isState ? StateType.ENABLED.getCode() : StateType.DISABLED.getCode());
                }

                ExtendedUser update = this.extendedUserRepository.save(dbExtendedUser.get());
                log.info("CustomExtendedUserService-updateExtendedUser，更新帳號完成");

                return update;
            } else {
                log.error("CustomExtendedUserService-updateExtendedUser，查詢不到帳號");
            }
        } catch (Exception ex) {
            log.error("CustomExtendedUserService-updateExtendedUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Optional<ExtendedUserDTO> findOneByUserId(String login) {
        log.debug("Request to get ExtendedUser");
        return extendedUserRepository.findOneByUserId(login).map(extendedUserMapper::toDto);
    }

    public Instant getCurrentTimeInTaiwan() {
        // 獲取當前的Instant。
        Instant now = Instant.now();
        // 定義本地時區(Asia/Taipei)。
        ZoneId taiwanZoneId = ZoneId.of("Asia/Taipei");
        // 將Instant轉換為本地時區的ZonedDateTime
        ZonedDateTime taiwanTime = now.atZone(taiwanZoneId);
        // 返回本地時區的時間，轉換為Instant
        return taiwanTime.toInstant();
    }
}
