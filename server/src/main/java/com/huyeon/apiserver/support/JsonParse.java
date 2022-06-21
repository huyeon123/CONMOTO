package com.huyeon.apiserver.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public class JsonParse {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readJson(String jsonMsg, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonMsg, clazz);
        } catch (Exception e) {
            log.error("Json Parsing Err!");
            e.printStackTrace();
        }
        return (T) new Object(); //시범 적용
    }

    public static <T> T readJson(String jsonMsg, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonMsg, typeReference);
        } catch (Exception e) {
            log.error("Json Parsing Err!");
            e.printStackTrace();
        }
        return null;
    }

    public static String writeJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (Exception e) {
            log.error("Json Write Err!");
            e.printStackTrace();
        }
        return null;
    }
}
