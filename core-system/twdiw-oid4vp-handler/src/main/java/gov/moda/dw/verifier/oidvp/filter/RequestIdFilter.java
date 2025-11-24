package gov.moda.dw.verifier.oidvp.filter;

import gov.moda.dw.verifier.oidvp.util.LogUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;

public class RequestIdFilter implements Filter {

  @Override
  public void doFilter(
      final ServletRequest request, final ServletResponse response, final FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(request, response);
    // 統一清除log thread context
    LogUtils.clearAll();
  }
}
