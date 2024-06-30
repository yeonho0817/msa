package com.msa.gateway.error;

import com.msa.gateway.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
