package gov.moda.dw.manager.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import gov.moda.dw.manager.security.AuthoritiesConstants;
import gov.moda.dw.manager.security.accessToken.AccessTokenFilter;
import gov.moda.dw.manager.security.accessToken.AccessTokenProvider;
import gov.moda.dw.manager.security.auth.AmsAuthenticationProvider;
import gov.moda.dw.manager.security.crypto.ModadwPasswordEncoder;
import gov.moda.dw.manager.security.jwt.CustomAuthenticationEntryPoint;
import gov.moda.dw.manager.security.xss.XssFilter;
import gov.moda.dw.manager.service.custom.track.ExceptionTrackService;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    private final AccessTokenProvider accessTokenProvider;

    private final ExceptionTrackService exceptionTrackService;

    private final CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter;

    private final XssFilter xssFilter;

    public SecurityConfiguration(
        UserDetailsService userDetailsService,
        AccessTokenProvider accessTokenProvider,
        ExceptionTrackService exceptionTrackService,
        CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter,
        XssFilter xssFilter
    ) {
        this.userDetailsService = userDetailsService;
        this.accessTokenProvider = accessTokenProvider;
        this.exceptionTrackService = exceptionTrackService;
        this.customJwtGrantedAuthoritiesConverter = customJwtGrantedAuthoritiesConverter;
        this.xssFilter = xssFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new ModadwPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(
                authz ->
                    // prettier-ignore
                    authz
                        .requestMatchers(mvc.pattern("/customError")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/modadw303w/authenticate")).permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
                        .requestMatchers(mvc.pattern("/api/register")).permitAll()
                        .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw301w/reset-bwd/init")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw311w/validate/key")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw311w/activate")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw303w/generateNonce")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw303w/generateOtp")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw303w/verifyOtp")).permitAll()
                        .requestMatchers(mvc.pattern("/api/redirect/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/terms/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/logo/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/ext/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/info/**")).permitAll()
                        
                        //FIXME: 移除以下測試用 API 權限
                        .requestMatchers(mvc.pattern("/api/ping")).permitAll()
                        .requestMatchers(mvc.pattern("/api/testSendTBB")).permitAll()
                        .requestMatchers(mvc.pattern("/test/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw201w/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw302w/reset-bwd/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw304w/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw351w/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/modadw701w/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/verifier/data/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/verifier/deepLink/**")).permitAll()
                        .requestMatchers(mvc.pattern("/api/verifier/result")).permitAll()
                        //FIXME: 移除以上測試用 API 權限

                        .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/api/**")).authenticated()
//                        .requestMatchers(mvc.pattern("/api/**")).permitAll()
                        .requestMatchers(mvc.pattern("/v3/api-docs/**"))
                        .hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/management/health")).permitAll()
                        .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                        .requestMatchers(mvc.pattern("/management/info")).permitAll()
                        .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                        .requestMatchers(mvc.pattern("/management/**"))
                        .hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions.accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
            .oauth2ResourceServer(
                oauth2 ->
                    oauth2
                        .authenticationEntryPoint(customAuAuthenticationEntryPoint())
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            //加上 CSP Header
            .headers(headers ->
                headers.contentSecurityPolicy(csp ->
                    csp.policyDirectives("default-src 'self'; script-src 'self'; object-src 'none'; frame-ancestors 'none'")
                )
            )
            // 配置accessToken攔截器。
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
