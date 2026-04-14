package gov.moda.dw.manager.service.custom;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.config.Constants;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.AuthorityRepository;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.repository.custom.CustomUserRepository;
import gov.moda.dw.manager.security.SecurityUtils;
import gov.moda.dw.manager.security.jwt.JwtUserObject;
import gov.moda.dw.manager.service.dto.RoleDTO;
import gov.moda.dw.manager.service.dto.custom.Ams311wReqDTO;
import gov.moda.dw.manager.type.AccountActionType;
import gov.moda.dw.manager.type.AuthorityAction;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class CustomUserService {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private final Logger log = LoggerFactory.getLogger(CustomUserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;
    private static final int DESIRED_PWD_LENGTH = 256;
    private final CustomUserRepository customUserRepository;
    private final CustomRelService customRelService;

    public CustomUserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CustomUserRepository customUserRepository,
        CustomRelService customRelService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.customUserRepository = customUserRepository;
        this.customRelService = customRelService;
    }

    /**
     * 建立使用者帳號，jhi_user。
     */
    public User saveUser(Ams311wReqDTO ams311wReqDTO, String userLogin) {
        try {
            Instant currentTime = this.getCurrentTimeInTaiwan();

            User user = new User();
            user.setLogin(ams311wReqDTO.getLogin().toLowerCase()); // 帳號為信箱。
            user.setEmail(ams311wReqDTO.getLogin().toLowerCase());
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // ZH-TW。
            user.setPassword(RandomUtil.generatePassword());
            user.setResetKey(RandomUtil.generateResetKey());
            user.setResetDate(currentTime);
            user.setActivated(false); // 帳號創建為不啟動狀態。
            user.setActivationKey(RandomUtil.generateActivationKey());
            user.setCreatedBy(userLogin);
            user.setCreatedDate(currentTime);
            user.setLastModifiedBy(userLogin);
            user.setLastModifiedDate(currentTime);

            User save = this.userRepository.save(user);
            log.info("CustomUserService-saveUser，建立帳號完成");
            return save;
        } catch (Exception ex) {
            log.error("CustomUserService-saveUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
            return null;
        }
    }

    /**
     * 更新使用者帳號，jhi_user。
     */
    public User updateUser(Ams311wReqDTO ams311wReqDTO, AccountActionType actionType, String userLogin) {
        try {
            Instant currentTime = this.getCurrentTimeInTaiwan();

            Optional<User> dbUser = this.userRepository.findOneByLogin(ams311wReqDTO.getLogin());

            if (dbUser.isPresent()) {
                if (actionType.equals(AccountActionType.CHANGE_ACTIVATED)) {
                    dbUser.get().setActivated(!dbUser.get().isActivated());
                    dbUser.get().setActivationKey(null);
                    dbUser.get().setResetKey(null);
                } else if (actionType.equals(AccountActionType.RESET_PWD)) {
                    dbUser.get().setActivationKey(null);
                    dbUser.get().setResetKey(RandomUtil.generateResetKey());
                    dbUser.get().setPassword(RandomUtil.generatePassword());
                    dbUser.get().setResetDate(Instant.now());
                }
                dbUser.get().setLastModifiedBy(userLogin);
                dbUser.get().setLastModifiedDate(currentTime);

                User update = this.userRepository.save(dbUser.get());
                log.info("CustomUserService-updateUser，更新帳號完成");
                return update;
            } else {
                log.error("CustomUserService-updateUser，查詢不到帳號");
            }
        } catch (Exception ex) {
            log.error("CustomUserService-updateUser-發生錯誤，原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
        return null;
    }

    /**
     * 更新 user table
     * @param user
     * @return
     */
    public User update(User user) {
        User result = this.userRepository.save(user);
        return result;
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

    public List<RoleDTO> getRoles(String login) {
        User user = customUserRepository.findOneByLogin(login).orElseThrow();
        return customRelService.accountAction(user, null, AuthorityAction.ACCOUNT_SEARCH_ROLE);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.debug("Deleted User");
            });
    }

    public long getLoginUserId() {
        String loginId = null;
        Long userId = 0L;
        Optional<JwtUserObject> first = SecurityUtils.getJwtUserObject().stream().findFirst();
        if (first.isPresent()) {
            loginId = first.get().getUserId();
            userId = findUserId(loginId);
        }
        return userId;
    }

    public Long findUserId(String login) {
        return customUserRepository.findOneByLogin(login).orElseThrow().getId();
    }
}
