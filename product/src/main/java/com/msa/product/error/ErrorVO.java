package com.msa.product.error;

import com.msa.product.util.JsonUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
        responseTrace(errorVO);
        return errorVO;
    }

    public static ErrorVO of(Exception e, HttpStatus httpStatus) {
        ErrorVO errorVO = ErrorVO.builder()
                .code(httpStatus.value())
                .name(e.getClass().getName())
                .txt(e.getMessage())
                .build();
        responseTrace(errorVO);
        return errorVO;
    }

    private static void responseTrace(ErrorVO error) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        int msgCount=0;
        try {
            msgCount = (int) httpRequest.getAttribute("msgCount");
        } catch (Exception e) {
            msgCount = 0;
        }

        Map resMap = JsonUtil.objectToMap(error);
        String logInfo = error.getCode() + " " + error.getTxt();

        log.info(Markers.appendEntries(resMap), "response[{}]: {}", msgCount, logInfo);
    }
}