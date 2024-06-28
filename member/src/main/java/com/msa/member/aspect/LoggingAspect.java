package com.msa.member.aspect;

import com.msa.member.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    private static int MSG_COUNT = 0;

    @Before("execution(* com.msa.member.controller..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, Object> paramMap = new HashMap<>();
        int msgCount = getMsgCount();
        httpRequest.setAttribute("msgCount", msgCount);
        for (int i = 0; i < signature.getParameterNames().length; i++) {

            if(ObjectUtils.isEmpty(joinPoint.getArgs()[i])) continue;
            if(Arrays.stream(joinPoint.getArgs()[i].getClass().getInterfaces()).anyMatch(a -> a.equals(List.class))
                && ((List)joinPoint.getArgs()[i]).stream().anyMatch(a -> a instanceof MultipartFile)) continue;

            if(Arrays.stream(joinPoint.getArgs()[i].getClass().getInterfaces()).anyMatch(a -> a.equals(MultipartFile.class))) continue;
            if(joinPoint.getArgs()[i].getClass().equals(ContentCachingRequestWrapper.class)) continue;

            paramMap.put(signature.getParameterNames()[i], joinPoint.getArgs()[i]);
        }
        String logInfo = httpRequest.getMethod() + " " + httpRequest.getRequestURI() + "  parameter: " + JsonUtil.marshalLog(paramMap);

        log.info(Markers.appendEntries(paramMap), "request[{}]: {}", msgCount, logInfo);
    }

    private int getMsgCount() {
        MSG_COUNT++;
        if(MSG_COUNT > 100000)
            MSG_COUNT = 1;
        return MSG_COUNT;
    }
}
