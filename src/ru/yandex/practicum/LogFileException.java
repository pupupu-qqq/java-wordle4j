package ru.yandex.practicum;

import java.io.IOException;

public class LogFileException extends RuntimeException {

    public LogFileException(String fileName, IOException cause) {
        super("Не удалось создать лог-файл: " + fileName, cause);
    }
}
