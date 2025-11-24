package gov.moda.dw.issuer.vc.security.accessToken;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpMethod;

public class AmsHttpServletRequestWrapper extends HttpServletRequestWrapper {

  private final byte[] body;

  public AmsHttpServletRequestWrapper(HttpServletRequest request) throws IOException, ServletException {
    super(request);
      if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.toString()) && null != request.getContentType()) {
          if (request.getContentType().contains("multipart/form-data")) {
              List<Part> parts = (List<Part>) request.getParts();
              if (parts.size() > 0) {
                  body = IOUtils.toByteArray(parts.get(0).getInputStream());
              } else {
                  body = null;
              }
          } else if(request.getContentType().contains("x-www-form-urlencoded")){
              body = null;
          }else {
              body = IOUtils.toByteArray(request.getReader(), StandardCharsets.UTF_8);
          }
      } else {
          body = IOUtils.toByteArray(request.getReader(), StandardCharsets.UTF_8);
      }
  }

  @Override
  public BufferedReader getReader() {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  public ServletInputStream getInputStream() {
    final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
    return new ServletInputStream() {
      @Override
      public int read() {
        return byteArrayInputStream.read();
      }

      @Override
      public boolean isFinished() {
        return false;
      }

      @Override
      public boolean isReady() {
        return false;
      }

      @Override
      public void setReadListener(ReadListener arg0) {}
    };
  }
}
