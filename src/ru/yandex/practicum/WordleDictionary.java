package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    public static final int WORD_LENGTH = 5;

    private final List<String> words;

    public WordleDictionary(Collection<String> words) {
        if (words == null) {
            throw new IllegalArgumentException("Список слов не может быть null");
        }
        Set<String> uniqueWords = new LinkedHashSet<>();
        for (String word : words) {
            String normalizedWord = normalize(word);
            if (isGameWord(normalizedWord)) {
                uniqueWords.add(normalizedWord);
            }
        }
        this.words = Collections.unmodifiableList(new ArrayList<>(uniqueWords));
    }

    public List<String> getWords() {
        return words;
    }

    public int size() {
        return words.size();
    }

    public boolean isEmpty() {
        return words.isEmpty();
    }

    public boolean contains(String word) {
        return words.contains(normalize(word));
    }

    public String get(int index) {
        return words.get(index);
    }

    public String getRandomWord(Random random) {
        if (isEmpty()) {
            throw new IllegalStateException("Нельзя выбрать слово из пустого словаря");
        }
        Random source = random == null ? new Random() : random;
        return words.get(source.nextInt(words.size()));
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.trim()
                .toLowerCase(Locale.ROOT)
                .replace('ё', 'е');
    }

    public static boolean isGameWord(String word) {
        String normalizedWord = normalize(word);
        if (normalizedWord.length() != WORD_LENGTH) {
            return false;
        }
        for (int i = 0; i < normalizedWord.length(); i++) {
            char letter = normalizedWord.charAt(i);
            if (letter < 'а' || letter > 'я') {
                return false;
            }
        }
        return true;
    }

    public static String compareWords(String guess, String answer) {
        String normalizedGuess = normalize(guess);
        String normalizedAnswer = normalize(answer);
        if (!isGameWord(normalizedGuess) || !isGameWord(normalizedAnswer)) {
            throw new IllegalArgumentException("Для сравнения нужны два русских слова из пяти букв");
        }

        char[] result = new char[WORD_LENGTH];
        int[] remainingLetters = new int['я' - 'а' + 1];

        for (int i = 0; i < WORD_LENGTH; i++) {
            char guessLetter = normalizedGuess.charAt(i);
            char answerLetter = normalizedAnswer.charAt(i);
            if (guessLetter == answerLetter) {
                result[i] = '+';
            } else {
                remainingLetters[answerLetter - 'а']++;
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '+') {
                continue;
            }
            char guessLetter = normalizedGuess.charAt(i);
            int letterIndex = guessLetter - 'а';
            if (remainingLetters[letterIndex] > 0) {
                result[i] = '^';
                remainingLetters[letterIndex]--;
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }
}
