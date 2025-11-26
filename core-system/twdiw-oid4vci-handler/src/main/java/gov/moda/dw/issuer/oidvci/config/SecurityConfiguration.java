package gov.moda.dw.issuer.oidvci.config;

import static org.springframework.security.config.Customizer.withDefaults;

import gov.moda.dw.issuer.oidvci.security.*;
import gov.moda.dw.issuer.oidvci.security.accessToken.AccessTokenFilter;
import gov.moda.dw.issuer.oidvci.security.accessToken.AccessTokenProvider;
import gov.moda.dw.issuer.oidvci.security.auth.AmsAuthenticationProvider;
import gov.moda.dw.issuer.oidvci.security.crypto.ModadwPasswordEncoder;
import gov.moda.dw.issuer.oidvci.security.jwt.CustomAuthenticationEntryPoint;
import gov.moda.dw.issuer.oidvci.service.custom.track.ExceptionTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    private final UserDetailsService userDetailsService;

    private final AccessTokenProvider accessTokenProvider;

    private final ExceptionTrackService exceptionTrackService;

    @Autowired
    private CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter;

    public SecurityConfiguration(
        JHipsterProperties jHipsterProperties,
        UserDetailsService userDetailsService,
        AccessTokenProvider accessTokenProvider,
        ExceptionTrackService exceptionTrackService
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.userDetailsService = userDetailsService;
        this.accessTokenProvider = accessTokenProvider;
        this.exceptionTrackService = exceptionTrackService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ModadwPasswordEncoder();
    }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//       return new BCryptPasswordEncoder();
//     }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authz ->
                    authz
                        .requestMatchers(mvc.pattern("/api/**")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/modadw303w/authenticate")).permitAll()
                        .requestMatchers(mvc.pattern("/management/health")).permitAll()
                        .requestMatchers(mvc.pattern("/management/info")).permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(
                exceptions ->
                    exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            //加上 CSP Header
            .headers(headers ->
                headers.contentSecurityPolicy(csp ->
                    csp.policyDirectives("default-src 'self'; script-src 'self'; object-src 'none'; frame-ancestors 'none'")
                )
            )
            .addFilterBefore(new AccessTokenFilter(accessTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(customJwtGrantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public AmsAuthenticationProvider amsAuthenticationProvider() {
        AmsAuthenticationProvider provider = new AmsAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationEntryPoint customAuAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(exceptionTrackService);
    }
}
