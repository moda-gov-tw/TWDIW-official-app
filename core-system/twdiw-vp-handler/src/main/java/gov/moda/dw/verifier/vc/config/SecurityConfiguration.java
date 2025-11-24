package gov.moda.dw.verifier.vc.config;

import static org.springframework.security.config.Customizer.withDefaults;

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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final JHipsterProperties jHipsterProperties;

    private final UserDetailsService userDetailsService;

    @Autowired
    private CustomJwtGrantedAuthoritiesConverter customJwtGrantedAuthoritiesConverter;

    public SecurityConfiguration(
        JHipsterProperties jHipsterProperties,
        UserDetailsService userDetailsService
    ) {
        this.jHipsterProperties = jHipsterProperties;
        this.userDetailsService = userDetailsService;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(
                authz ->
                    // prettier-ignore

                authz.requestMatchers(mvc.pattern("/api/**")).permitAll()

                    // presentation
                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/presentation/validation")).permitAll()
//                    .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/presentation/validation")).authenticated()

                    // public information
                    .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/version")).permitAll()

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
            );
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(customJwtGrantedAuthoritiesConverter);
        return converter;
    }
}
