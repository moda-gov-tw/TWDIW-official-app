package gov.moda.dw.issuer.vc.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
/**
 * @author AlexChang
 * @create 2024/06/20
 * @description
 */
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
    log.info("jwt to resource here , jwt:{}", jwt);
    List<String> authorities = Arrays.asList(jwt.getClaimAsString("auth").split(","));
    if (authorities == null) {
      log.error("empty list");
      return Collections.emptyList();
    }
    List<GrantedAuthority> simple = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    log.info("simple list:{}", simple);
    return simple;
  }
}
