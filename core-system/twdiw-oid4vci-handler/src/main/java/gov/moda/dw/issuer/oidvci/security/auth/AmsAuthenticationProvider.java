package gov.moda.dw.issuer.oidvci.security.auth;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import gov.moda.dw.issuer.oidvci.domain.ExtendedUser;
import gov.moda.dw.issuer.oidvci.repository.custom.CustomExtendedUserRepository;
import gov.moda.dw.issuer.oidvci.security.jwt.JwtUserObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Component
public class AmsAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomExtendedUserRepository customExtendedUserRepository;

    private UserCache userCache = new NullUserCache();
    private boolean forcePrincipalAsString = false;
    private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
    private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

    //   @Override
    @SuppressWarnings("deprecation")
    protected void additionalAuthenticationChecks(UserDetails userDetails, AmsUsernamePasswordAuthenticationToken authentication)
        throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            this.logger.debug("Failed to authenticate since no credentials provided");
            throw new BadCredentialsException(
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
            );
        }

        // validate bwd
        String presentedPassword = authentication.getCredentials().toString();
        ExtendedUser extendedUser = this.getExtendedUser(userDetails.getUsername());
        if ("AP".equalsIgnoreCase(extendedUser.getUserTypeId())) {
            if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                this.logger.debug("Failed to authenticate since password does not match stored value");
                throw new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
                );
            }
        }

    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        Assert.isInstanceOf(
            AmsUsernamePasswordAuthenticationToken.class,
            authentication,
            () ->
                this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.onlySupports",
                    "Only AmsUsernamePasswordAuthenticationToken is supported"
                )
        );
        String username = authentication.getPrincipal() == null ? "NONE_PROVIDED" : authentication.getName();
        boolean cacheWasUsed = true;
        UserDetails user = this.userCache.getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser(username, (AmsUsernamePasswordAuthenticationToken) authentication);
            } catch (UsernameNotFoundException ex) {
                this.logger.debug("Failed to find user '" + username + "'");
                if (!this.hideUserNotFoundExceptions) {
                    throw ex;
                }
                throw new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "帳號或密碼錯誤")
                );
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user, (AmsUsernamePasswordAuthenticationToken) authentication);
        } catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser(username, (AmsUsernamePasswordAuthenticationToken) authentication);
            this.preAuthenticationChecks.check(user);
            additionalAuthenticationChecks(user, (AmsUsernamePasswordAuthenticationToken) authentication);
        }
        this.postAuthenticationChecks.check(user);
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }

        // 取得 登入者所屬公司資料
        ExtendedUser extendedUser = this.getExtendedUser(username);

        // 客製jwtToken內容
        JwtUserObject customUser = new JwtUserObject(username, extendedUser.getOrgId());

        // 建立成功驗證身分的 Authentication 物件, 回傳, 以利後續使用.
        Authentication result = createSuccessAuthentication(customUser, authentication, user);
        return result;
    }

    private class DefaultPreAuthenticationChecks implements UserDetailsChecker {

        private DefaultPreAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails toCheck) {
            // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method 'check'");
        }
    }

    @Service
    private class DefaultPostAuthenticationChecks implements UserDetailsChecker {

        private DefaultPostAuthenticationChecks() {
        }

        @Override
        public void check(UserDetails toCheck) {
            // TODO Auto-generated method stub
            // throw new UnsupportedOperationException("Unimplemented method 'check'");
        }
    }

    private ExtendedUser getExtendedUser(String username) {
        return customExtendedUserRepository.findOneByUserId(username)
            .orElseThrow(() -> {
                log.error("AmsAuthenticationProvider-authenticate 找不到該 user 在 extendedUser 的資料. username={}", username);
                return new BadCredentialsException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "缺少資料")
                );
            });
    }
}
