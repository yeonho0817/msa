package com.msa.product.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@AllArgsConstructor
@Component
public class Error {

    public static BaseCodeException of(ErrorSpec spec, String... args) {
        BaseCodeException exception = new BaseCodeException(spec,
            Arrays.stream(args)
                .reduce(spec.getMessage(), (m, a) -> m.replaceFirst("%s", a))
                .replaceAll("%s", "")
        );
        return exception;
    }

    @Getter
    public static class BaseCodeException extends RuntimeException {

        private final HttpStatus httpStatus;
        private final String errorSpec;

        private BaseCodeException(ErrorSpec spec, String message) {
            super(message);
            this.errorSpec = spec.name();
            this.httpStatus = spec.getHttpStatus();
        }
    }
}
