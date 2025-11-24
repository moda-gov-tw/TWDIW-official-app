package gov.moda.dw.verifier.oidvp.web.rest.oidvp;

import gov.moda.dw.verifier.oidvp.annotation.LogInfo;
import gov.moda.dw.verifier.oidvp.annotation.LogInfo.LogType;
import gov.moda.dw.verifier.oidvp.common.OidvpErrorCode.OidvpError;
import gov.moda.dw.verifier.oidvp.model.OidvpResponse;
import gov.moda.dw.verifier.oidvp.model.errors.BaseOidvpException;
import gov.moda.dw.verifier.oidvp.model.errors.BaseOidvpRuntimeException;
import gov.moda.dw.verifier.oidvp.model.errors.OidvpErrorProperty;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice(assignableTypes = {WellKnownController.class, OidvpEndpointController.class, VerifierDidController.class})
@Order(1)
@LogInfo(value = LogType.RESPONSE)
@ResponseBody
public class ExceptionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler({BaseOidvpException.class, BaseOidvpRuntimeException.class})
    public ResponseEntity<OidvpResponse> handleOidvpException(Exception exception) {
        OidvpErrorProperty oidvpErrorProperty = (OidvpErrorProperty) exception;
        OidvpError error = oidvpErrorProperty.getOidvpError();
        if (OidvpError.INVALID_SESSION.equals(error)) {
            LOGGER.warn("invalid session: {}", exception.getMessage());
        } else {
            LOGGER.error("oidvp exception: {}", exception.getMessage(), exception);
        }
        return new ResponseEntity<>(OidvpResponse.error(error, oidvpErrorProperty.getOidvpErrorMessage()), error.getHttpStatus());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<OidvpResponse> handleSQLException(SQLException exception) {
        final OidvpError error = OidvpError.DB_ERROR;
        LOGGER.error("db exception: {}", exception.getMessage(), exception);
        return new ResponseEntity<>(OidvpResponse.error(error), error.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<OidvpResponse> handleMissingRequestParamException(MissingRequestValueException exception) {
        final OidvpError error = OidvpError.INVALID_PARAMETERS;
        String errorMessage = Optional.ofNullable(exception.getDetailMessageArguments())
                                      .map(args -> args[0])
                                      .map(parameterName -> "missing required parameter '" + parameterName + "'")
                                      .orElse(error.getMsg());
        LOGGER.warn(exception.getMessage());
        return new ResponseEntity<>(OidvpResponse.error(error, errorMessage), error.getHttpStatus());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<OidvpResponse> handleNoSuchElementException(NoSuchElementException exception) {
        final OidvpError error = OidvpError.RESULT_NOT_FOUND;
        LOGGER.warn("element not found: {}", exception.getMessage());
        return new ResponseEntity<>(OidvpResponse.error(error, "not found"), error.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OidvpResponse> handleOtherException(Exception exception) {
        final OidvpError error = OidvpError.SERVER_ERROR;
        LOGGER.error("internal error : unexpected error - {}", exception.getMessage(), exception);
        return new ResponseEntity<>(OidvpResponse.error(error), error.getHttpStatus());
    }
}
