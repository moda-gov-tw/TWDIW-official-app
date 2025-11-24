package gov.moda.dw.verifier.vc.filter;

import gov.moda.dw.verifier.vc.util.LogUtils;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class TransactionIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String transactionId = httpServletRequest.getHeader("transaction-id");
        LogUtils.addRequestID(transactionId);
        chain.doFilter(request, response);
        LogUtils.clearAll();
    }
}
