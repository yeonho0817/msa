package com.msa.gateway.error;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class ErrorVO {
    private int code;
    private String name;
    private String txt;

    public static ErrorVO of(Error.BaseCodeException e) {
        ErrorVO errorVO = ErrorVO.builder()
                .code(e.getHttpStatus().value())
                .name(e.getErrorSpec())
                .txt(e.getMessage())
                .build();
        return errorVO;
    }

    public static ErrorVO of(Exception e, HttpStatus httpStatus) {
        ErrorVO errorVO = ErrorVO.builder()
                .code(httpStatus.value())
                .name(e.getClass().getName())
                .txt(e.getMessage())
                .build();
        return errorVO;
    }
}