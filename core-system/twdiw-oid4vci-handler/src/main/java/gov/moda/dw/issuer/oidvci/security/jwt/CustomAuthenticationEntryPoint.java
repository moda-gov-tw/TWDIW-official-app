package gov.moda.dw.issuer.oidvci.security.jwt;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import org.apache.commons.lang3.exception.ExceptionUtils;
import gov.moda.dw.issuer.oidvci.config.SecurityJwtConfiguration;
import gov.moda.dw.issuer.oidvci.service.custom.track.ExceptionTrackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ResponseStatus;
import tech.jhipster.web.rest.errors.ProblemDetailWithCause;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Logger log = LoggerFactory.getLogger(SecurityJwtConfiguration.class);

    private final ExceptionTrackService exceptionTrackService;

    public CustomAuthenticationEntryPoint(ExceptionTrackService exceptionTrackService) {
        this.exceptionTrackService = exceptionTrackService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
        throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        OutputStream responseStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, HttpStatus.UNAUTHORIZED);
        responseStream.flush();
        String reqBody = "";
        String resType = "";
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = null;
            reader = request.getReader();
            String line;
            if (reader != null) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            reqBody = sb.toString().replace(" ", "");
        } catch (Exception ex) {
            log.error("ExceptionTranslator-customizeProblem，無法取得request body:{}", ExceptionUtils.getStackTrace(ex));
        }
        saveApiTrackLog(reqBody, wrapAndCustomizeProblem(authException));
    }

    private void saveApiTrackLog(String args, ProblemDetailWithCause problem) {
        try {
            //JWT token解析失敗的時候要儲存原因
            exceptionTrackService.saveHttpExceptionTrack(args, problem, 0);
        } catch (Exception ex) {
            log.error("CustomAuthenticationEntryPoint-saveApiTrackLog，無法儲存API track:{}", ExceptionUtils.getStackTrace(ex));
        }
    }

    protected ProblemDetailWithCause wrapAndCustomizeProblem(Throwable ex) {
        return ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance().withStatus(toStatus(ex).value()).build();
    }

    private HttpStatus toStatus(final Throwable throwable) {
        // Let the ErrorResponse take this responsibility
        if (throwable instanceof ErrorResponse err) return HttpStatus.valueOf(err.getBody().getStatus());

        return Optional.ofNullable(getMappedStatus(throwable)).orElse(
            Optional.ofNullable(resolveResponseStatus(throwable)).map(ResponseStatus::value).orElse(HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    private HttpStatus getMappedStatus(Throwable err) {
        // Where we disagree with Spring defaults
        if (err instanceof AccessDeniedException) return HttpStatus.FORBIDDEN;
        if (err instanceof JwtException || err instanceof AuthenticationException) return HttpStatus.UNAUTHORIZED;
        return null;
    }

    private ResponseStatus resolveResponseStatus(final Throwable type) {
        final ResponseStatus candidate = findMergedAnnotation(type.getClass(), ResponseStatus.class);
        return candidate == null && type.getCause() != null ? resolveResponseStatus(type.getCause()) : candidate;
    }
}
