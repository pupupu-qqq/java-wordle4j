package ru.yandex.practicum;

import java.io.IOException;

public class DictionaryLoadException extends RuntimeException {

    public DictionaryLoadException(String fileName, IOException cause) {
        super("Не удалось загрузить словарь: " + fileName, cause);
    }
}
