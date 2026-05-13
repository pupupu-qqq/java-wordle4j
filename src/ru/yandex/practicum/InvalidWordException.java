package ru.yandex.practicum;

public class InvalidWordException extends Exception {

    public InvalidWordException(String word) {
        super("Введите существительное из пяти русских букв: " + word);
    }
}
