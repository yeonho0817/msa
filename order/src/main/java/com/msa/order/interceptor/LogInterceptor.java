package com.msa.order.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msa.order.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if ("OPTIONS".equals(request.getMethod())) return;

        final ContentCachingRequestWrapper cachingRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        final ContentCachingResponseWrapper cachingResponse = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);

        if(ObjectUtils.isEmpty(cachingRequest) && ObjectUtils.isEmpty(cachingResponse) ) return;

        HttpStatus httpStatus = cachingResponse != null ? HttpStatus.valueOf(cachingResponse.getStatus()) : null;
        Map content = Map.of("request", request(cachingRequest), "response", response(cachingResponse, httpStatus));

        if(httpStatus == null || httpStatus.is5xxServerError()) {
            log.error(Markers.appendEntries(content), "{}", ex);
        } else if(httpStatus.is4xxClientError()) {
            log.warn(Markers.appendEntries(content), "{}", ex);
        } else {
            log.info(Markers.appendEntries(content), "{}");
        }
    }

    private Object response(ContentCachingResponseWrapper response, HttpStatus httpStatus) {
        Map<String, Object > results = new HashMap<>();
        if(ObjectUtils.isEmpty(response)) {
            return results;
        }

        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().stream().forEach(header -> {
            headers.put(header, response.getHeader(header));
        });

        Optional.ofNullable(httpStatus).ifPresent(v -> results.put("httpStatus", v.value()));
        results.put("header", headers);

        if (response.getContentType() != null
                && response.getContentType().contains("application/json") && response.getContentSize() > 0) {
            Optional.ofNullable(JsonUtil.byteTo(response.getContentAsByteArray(), new TypeReference<Map<String,Object>>() {}))
                    .ifPresent(v -> results.put("body", v));
        }

        return results;
    }

    private Object request(ContentCachingRequestWrapper request) {
        Map<String, Object > results = new HashMap<>();
        if(ObjectUtils.isEmpty(request)) {
            return results;
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new HashMap<>();
        headerNames.asIterator().forEachRemaining(header -> {
            headers.put(header, request.getHeader(header));
        });

        results.put("method", request.getMethod());
        results.put("url", request.getRequestURL().toString());
        results.put("path", request.getRequestURI());
        results.put("header", headers);
        if (request.getContentType() != null
                && request.getContentType().contains("application/json") && request.getContentLength() > 0) {
            Optional.ofNullable(JsonUtil.byteTo(request.getContentAsByteArray(), new TypeReference<Map<String, Object>>() {}))
                    .ifPresent(v -> results.put("body", v));
        }

        Optional.ofNullable(request.getParameterMap()).ifPresent(v -> results.put("params", v));
        Optional.ofNullable(request.getQueryString()).ifPresent(v -> results.put("query", v));

        return results;
    }
}
