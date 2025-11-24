package gov.moda.dw.verifier.vc.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

  @Override
  public Collection<GrantedAuthority> convert(Jwt jwt) {
    String authClaim = jwt.getClaimAsString("auth");
    if (authClaim == null || authClaim.isBlank()) {
      log.debug("auth claim missing or blank, returning empty authorities");
      return Collections.emptyList();
    }
    return Arrays.stream(authClaim.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
  }
}
