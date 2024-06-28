package com.msa.member.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build();

    public static void setPropertyNamingStrategy(PropertyNamingStrategy propertyNamingStrategy) {
        objectMapper.setPropertyNamingStrategy(propertyNamingStrategy);
    }

    public static <T> T unmarshal(String jsonText, Class<T> type) {
        try {
            return objectMapper.readValue(jsonText, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static String marshalLog(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("MarshallLog JsonProcessingException: {}", e.getMessage());
            return "";
        } catch (Exception e) {
            log.error("MarshallLog Exception : {}", e.getMessage());
            return "";
        }
    }

    public static String marshal(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static Map objectToMap(Object object) {
        try {
            return objectMapper.convertValue(object, Map.class);
        } catch(IllegalArgumentException e) {
            log.warn("[objectToMap] IllegalArgumentException : {}", e.getMessage(), e);
        }

        return Map.of();
    }

    public static <T> T byteTo(byte[] bytes, TypeReference<T> type) {
        if(bytes == null || bytes.length == 0) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, type);
        } catch(IOException e) {
            log.warn("[byteTo] Exception : {}", e.getMessage(), e);
        }
        return null;
    }

    public static <T> T inputStreamTo(InputStream inputStream, TypeReference<T> type) {

        try {
            return objectMapper.readValue(inputStream, type);
        } catch(IOException e) {
            log.warn("[inputStreamTo] Exception : {}", e.getMessage(), e);
        }
        return null;
    }
}

