package com.msa.member.response;

import com.msa.member.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@Slf4j
@Data
@Component
public class Response<T> {

    private int code;
    private String txt;
    private T body;

    public static final Response<Void> OK = new Response<>();

    private Response() {
        this.code = ResponseCode.OK.getCode();
        this.txt = ResponseCode.OK.getMessage();
    }

    private Response(T body) {
        this();
        this.body = body;
    }

    public static <T> Response<T> of(T body) {
        Response<T> response = new Response<>(body);
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        int msgCount = (int) httpRequest.getAttribute("msgCount");

        Map resMap = JsonUtil.objectToMap(response);
        String logInfo = response.getCode() + " " +response.getTxt();

        log.info(Markers.appendEntries(resMap), "response[{}]: {}", msgCount, logInfo);
        return response;
    }
}
