package gov.moda.dw.issuer.vc.config;

import static org.springframework.security.config.Customizer.withDefaults;

import gov.moda.dw.issuer.vc.security.*;
//import gov.moda.dw.issuer.vc.security.accessToken.AccessTokenFilter;
//import gov.moda.dw.issuer.vc.security.accessToken.AccessTokenProvider;
import gov.moda.dw.issuer.vc.security.auth.AmsAuthenticationProvider;
import gov.moda.dw.issuer.vc.security.crypto.ModadwPasswordEncoder;
//import gov.moda.dw.issuer.vc.security.jwt.CustomAuthenticationEntryPoint;
//import gov.moda.dw.issuer.vc.service.custom.track.ExceptionTrackService;
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

//    private final AccessTokenProvider accessTokenProvider;

//    private final ExceptionTrackService exceptionTrackService;

    @Autowired
    private CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter;


    public SecurityConfiguration(
        JHipsterProperties jHipsterProperties,
        UserDetailsService userDetailsService/*,
        AccessTokenProvider accessTokenProvider,
        ExceptionTrackService exceptionTrackService*/
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.userDetailsService = userDetailsService;
        /*this.accessTokenProvider = accessTokenProvider;
        this.exceptionTrackService = exceptionTrackService;*/
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
                    // prettier-ignore

                authz
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/modadw303w/authenticate")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/**")).permitAll()

                    // DID registration
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/did")).permitAll()
//                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/did")).authenticated()

                    // credential
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/credential")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/credential/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/credential/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/credentials")).permitAll()

                    // status-list
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/status-list/**")).permitAll()

                    // public information
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/version")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/status-list/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/schema/**")).permitAll()
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/keys")).permitAll()

                    .requestMatchers(mvc.pattern("/api/**")).permitAll()

//                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
//                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
//                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
//                    .requestMatchers(mvc.pattern("/api/**")).authenticated()
//                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
//                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(
                exceptions ->
                    exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler())
            )
            /*.oauth2ResourceServer(
                oauth2 ->
                    oauth2
                        .authenticationEntryPoint(customAuAuthenticationEntryPoint())
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )*/;
            // 配置accessToken攔截器。
            //.addFilterBefore(new AccessTokenFilter(accessTokenProvider), UsernamePasswordAuthenticationFilter.class);

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

    /*@Bean
    public AuthenticationEntryPoint customAuAuthenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint(exceptionTrackService);
    }*/
}
