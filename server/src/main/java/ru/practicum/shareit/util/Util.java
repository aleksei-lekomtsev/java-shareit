package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";

    public static void checkForNull(Object entity) {
        if (entity == null) {
            log.warn("Произошла непредвиденная ошибка. Значение entity не может быть null");
            throw new RuntimeException("Произошла непредвиденная ошибка. Значение entity не может быть null");
        }
    }
}
