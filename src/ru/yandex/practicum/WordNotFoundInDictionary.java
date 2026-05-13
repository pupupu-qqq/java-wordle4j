package ru.yandex.practicum;

public class WordNotFoundInDictionary extends Exception {

    public WordNotFoundInDictionary(String word) {
        super("Слова нет в словаре: " + word);
    }
}
