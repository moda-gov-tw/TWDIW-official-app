package gov.moda.dw.verifier.oidvp.config;

import gov.moda.dw.verifier.oidvp.filter.RequestIdFilter;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

  String verifierPath = "oidvp";

  String basePath;

  @PostConstruct
  void initPath() {
    basePath = "/api/" + verifierPath + "/*";
  }

  @Bean
  public FilterRegistrationBean<RequestIdFilter> requestIDFilterBean() {
    final FilterRegistrationBean<RequestIdFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new RequestIdFilter());
    bean.addUrlPatterns(basePath);
    bean.setName("requestIDFilter");
    return bean;
  }
}
