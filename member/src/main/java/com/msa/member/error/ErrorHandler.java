package com.msa.member.error;

import com.msa.member.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(Error.BaseCodeException.class)
    public ResponseEntity<ErrorVO> handleBaseCodeException(Error.BaseCodeException exception) {
        print("BaseCode ErrorHandler: ", exception);
        final ErrorVO error = ErrorVO.of(exception);
        return ResponseEntity.status(HttpStatus.valueOf(error.getCode())).body(error);
    }

    @ExceptionHandler( AccessDeniedException.class)
    public ResponseEntity<ErrorVO> AccessDeniedException(Exception exception) {
        print("Security ErrorHandler: ", exception);
        final Error.BaseCodeException authException = Error.of(ErrorSpec.AccessDeniedException);
        final ErrorVO error = ErrorVO.of(authException);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorVO> handleAuthenticationException(Exception exception) {
        print("Security ErrorHandler: ", exception);
        final Error.BaseCodeException authException = Error.of(ErrorSpec.InvalidTokenError);
        final ErrorVO error = ErrorVO.of(authException);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorVO> handleNotFoundException(NoHandlerFoundException exception) {
        print("Not Found ErrorHandler: ", exception);
        final Error.BaseCodeException notFoundException = Error.of(ErrorSpec.NotFoundError);
        final ErrorVO error = ErrorVO.of(notFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ErrorVO> handleParameterException(Exception exception) {
        print("ErrorHandler: ", exception);
        final ErrorVO error = ErrorVO.of(exception, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorVO> handleAllExceptions(Exception exception) {
        print("ErrorHandler: ", exception);
        final ErrorVO error = ErrorVO.of(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private void print(String format, Exception e) {
        log.error(Markers.appendEntries(JsonUtil.objectToMap(e))
                .and(Markers.append("format", format)), "{}{}", format, e.getMessage(), e);
    }

    private void print(String format, Error.BaseCodeException e) {
        HttpStatus httpStatus = Optional.ofNullable(e.getHttpStatus()).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        if(httpStatus.is5xxServerError()) {
            log.error(Markers.appendEntries(JsonUtil.objectToMap(e))
                    .and(Markers.append("format", format)), "{}{}", format, e.getMessage(), e);
        } else if(httpStatus.is4xxClientError()) {
            log.warn(Markers.appendEntries(JsonUtil.objectToMap(e))
                    .and(Markers.append("format", format)), "{}{}", format, e.getMessage(), e);
        } else {
            log.info(Markers.appendEntries(JsonUtil.objectToMap(e))
                    .and(Markers.append("format", format)), "{}{}", format, e.getMessage(), e);
        }
    }

    private void printWaring(String format, Exception e) {
        log.warn(Markers.appendEntries(JsonUtil.objectToMap(e))
                .and(Markers.append("format", format)), "{}{}", format, e.getMessage(), e);
    }
}
