package com.audtream.desktop.config;

import com.audtream.desktop.service.TokenStorage;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import okhttp3.OkHttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApiConfig {
    private static final String BASE_URL = "http://localhost:8080/api";
    private static OkHttpClient httpClient;
    private static ObjectMapper objectMapper;

    static {
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    var originalRequest = chain.request();
                    var token = TokenStorage.getToken();

                    if (token != null && !token.isEmpty()) {
                        var requestBuilder = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + token);
                        return chain.proceed(requestBuilder.build());
                    }
                    return chain.proceed(originalRequest);
                })
                .build();

        objectMapper = new ObjectMapper();

        // Wyłącz FAIL_ON_UNKNOWN_PROPERTIES, aby ignorował dodatkowe pola (jak formattedDuration)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JavaTimeModule module = new JavaTimeModule();

        // Dodaj deserializer dla LocalDateTime, który obsłuży format ze spacją
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getValueAsString();
                // Spróbuj parsować w formacie ze spacją
                try {
                    return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (DateTimeParseException e) {
                    // Jeśli to nie zadziała, spróbuj ISO format (z T)
                    return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            }
        });

        objectMapper.registerModule(module);
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}