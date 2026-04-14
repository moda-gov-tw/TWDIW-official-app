package gov.moda.dw.manager.service.custom;

import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import gov.moda.dw.manager.domain.Authority;
import gov.moda.dw.manager.repository.AuthorityRepository;
import gov.moda.dw.manager.repository.custom.UserAuthorityRepository;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class AuthoritiesService {

    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final CustomAuthObjQueryService authObjQueryService;

    public AuthoritiesService(
        AuthorityRepository authorityRepository,
        UserAuthorityRepository userAuthorityRepository,
        CustomAuthObjQueryService authObjQueryService
    ) {
        this.authorityRepository = authorityRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.authObjQueryService = authObjQueryService;
    }

    /**
     * 新增權限
     * @param authority
     * @param managedAuthorities
     * @return
     */
    public Set<Authority> addAuthority(String authority, Set<Authority> managedAuthorities) {
        try {
            Optional<Authority> authorityOptional = authorityRepository.findById(authority);
            if (authorityOptional.isPresent()) {
                managedAuthorities.add(authorityOptional.get());
                log.debug("AuthoritiesService-addAuthority 權限已存在, 不新增 authority={}", authority);
            } else {
                Authority newAuthority = new Authority();
                newAuthority.setName(authority);
                Authority addAuthority = authorityRepository.save(newAuthority);
                managedAuthorities.add(addAuthority);
                log.info("AuthoritiesService-addAuthority 權限新增完成 authority={}", authority);
            }
            return managedAuthorities;
        } catch (Exception ex) {
            log.error("AuthoritiesService-addAuthority 新增權限發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
            return managedAuthorities;
        }
    }

    /**
     * 刪除權限
     * @param resId
     */
    public void delAuthority(String resId) {
        try {
            log.info("AuthoritiesService-delAuthority 開始刪除權限 resId={}", resId);

            Authority authority = new Authority();
            authority.setName(resId);
            Example<Authority> authorityExample = Example.of(authority);
            Optional<Authority> auth = authorityRepository.findOne(authorityExample);
            if (auth.isPresent()) {
                //先刪除 jhi_user_authority 關聯表, 再刪除 Authority table
                userAuthorityRepository.deleteByIdAuthorityName(authority.getName());
                authorityRepository.deleteById(authority.getName());
                log.info("AuthoritiesService-delAuthority 刪除 authority={}", authority.getName());
            } else {
                log.info("AuthoritiesService-delAuthority 權限不存在 resId={}", resId);
            }
        } catch (Exception ex) {
            log.error("AuthoritiesService-delAuthority 刪除權限發生錯誤, 原因為:{}", ExceptionUtils.getStackTrace(ex));
        }
    }
}
