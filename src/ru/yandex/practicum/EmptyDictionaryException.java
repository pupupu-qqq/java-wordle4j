package ru.yandex.practicum;

public class EmptyDictionaryException extends Exception {

    public EmptyDictionaryException(String source) {
        super("В словаре нет подходящих слов: " + source);
    }
}
