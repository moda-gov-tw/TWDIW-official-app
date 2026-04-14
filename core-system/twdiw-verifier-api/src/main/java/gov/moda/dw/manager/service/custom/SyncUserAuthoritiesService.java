package gov.moda.dw.manager.service.custom;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.moda.dw.manager.domain.Authority;
import gov.moda.dw.manager.domain.User;
import gov.moda.dw.manager.repository.UserRepository;
import gov.moda.dw.manager.service.criteria.AuthObjCriteria;
import gov.moda.dw.manager.service.dto.AuthObjDTO;
import gov.moda.dw.manager.util.StringFilterUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class SyncUserAuthoritiesService {

    private final AuthoritiesService authoritiesService;

    private final UserRepository userRepository;

    private final CustomAuthObjQueryService authObjQueryService;

    public SyncUserAuthoritiesService(
        AuthoritiesService authoritiesService,
        UserRepository userRepository,
        CustomAuthObjQueryService authObjQueryService
    ) {
        this.authoritiesService = authoritiesService;
        this.userRepository = userRepository;
        this.authObjQueryService = authObjQueryService;
    }

    /**
     * 同步 rel 跟 jhi_user_authority
     *
     * @param login
     */
    public boolean syncUserAuthorities(String login) {
        boolean syncOK = false;
        try {
            log.info("SyncUserAuthoritiesService-syncUserAuthorities 開始同步 jhi_user_authority table");
            Set<String> authorities = new HashSet<>();

            //找到與user相關的所有權限
            AuthObjCriteria authObjCriteria = new AuthObjCriteria();
            authObjCriteria.setLogin(StringFilterUtils.toEqualStringFilter(login));
            List<AuthObjDTO> authList = authObjQueryService.findByCriteria(authObjCriteria);

            //去除重複權限
            for (AuthObjDTO view : authList) {
                authorities.add(view.getResCode());
            }

            // 同步到 jhi_user_authority
            Optional<User> userOptional = userRepository.findOneByLogin(login);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                // 獲取 User 的 Authorities 並清空
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();

                // 遍歷 authorities，查找 Authority 並添加到 managedAuthorities
                log.info("AuthoritiesService-addAuthority 開始新增權限 authorities={}", authorities);
                for (String authority : authorities) {
                    authoritiesService.addAuthority(authority, managedAuthorities);
                }
                user.setAuthorities(managedAuthorities);
                userRepository.save(user);

                log.info("SyncUserAuthoritiesService-syncUserAuthorities 同步完成 jhi_user_authority table");

                syncOK = true;
                return syncOK;
            } else {
                log.warn("SyncUserAuthoritiesService-syncUserAuthorities 查不到該user帳號, 同步失敗");
                return syncOK;
            }
        } catch (Exception ex) {
            log.error(
                "SyncUserAuthoritiesService-syncUserAuthorities 同步 jhi_user_authority 發生錯誤, 錯誤原因為:{}",
                ExceptionUtils.getStackTrace(ex)
            );
            return syncOK;
        }
    }
}
