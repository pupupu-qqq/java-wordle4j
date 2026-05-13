package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleGame {

    public static final int MAX_STEPS = 6;
    public static final String WIN_HINT = "+++++";

    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final List<Attempt> attempts;
    private final Set<String> suggestedWords;
    private final PrintWriter log;
    private boolean won;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this(dictionary, dictionary.getRandomWord(new Random()), log);
    }

    public WordleGame(WordleDictionary dictionary, String answer) {
        this(dictionary, answer, null);
    }

    public WordleGame(WordleDictionary dictionary, String answer, PrintWriter log) {
        if (dictionary == null || dictionary.isEmpty()) {
            throw new IllegalArgumentException("Словарь не должен быть пустым");
        }
        this.dictionary = dictionary;
        this.answer = WordleDictionary.normalize(answer);
        if (!this.dictionary.contains(this.answer)) {
            throw new IllegalArgumentException("Ответ должен быть словом из словаря");
        }
        this.steps = MAX_STEPS;
        this.attempts = new ArrayList<>();
        this.suggestedWords = new LinkedHashSet<>();
        this.log = log;
        log("Answer selected: " + this.answer);
    }

    public String makeMove(String word) throws InvalidWordException, WordNotFoundInDictionary {
        if (isFinished()) {
            throw new IllegalStateException("Игра уже завершена");
        }

        String normalizedWord = validateWord(word);
        String hint = WordleDictionary.compareWords(normalizedWord, answer);
        steps--;
        attempts.add(new Attempt(normalizedWord, hint));
        won = WIN_HINT.equals(hint);

        log("Attempt: " + normalizedWord + ", hint: " + hint + ", steps left: " + steps);
        if (steps < 0) {
            throw new IllegalStateException("Количество попыток стало отрицательным");
        }
        return hint;
    }

    public String suggestWord() {
        List<String> possibleWords = getPossibleWords();
        if (possibleWords.isEmpty()) {
            throw new IllegalStateException("Не осталось подходящих слов");
        }

        for (String word : possibleWords) {
            if (!wasGuessed(word) && !suggestedWords.contains(word)) {
                suggestedWords.add(word);
                log("Suggested word: " + word);
                return word;
            }
        }

        for (String word : possibleWords) {
            if (!wasGuessed(word)) {
                suggestedWords.add(word);
                log("Suggested repeated candidate: " + word);
                return word;
            }
        }

        String fallback = possibleWords.get(0);
        log("Suggested fallback candidate: " + fallback);
        return fallback;
    }

    public List<String> getPossibleWords() {
        List<String> possibleWords = new ArrayList<>();
        for (String candidate : dictionary.getWords()) {
            if (matchesAttempts(candidate)) {
                possibleWords.add(candidate);
            }
        }
        return possibleWords;
    }

    public boolean isWon() {
        return won;
    }

    public boolean isLost() {
        return !won && steps == 0;
    }

    public boolean isFinished() {
        return isWon() || isLost();
    }

    public int getRemainingSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    private String validateWord(String word) throws InvalidWordException, WordNotFoundInDictionary {
        String normalizedWord = WordleDictionary.normalize(word);
        if (!WordleDictionary.isGameWord(normalizedWord)) {
            throw new InvalidWordException(word);
        }
        if (!dictionary.contains(normalizedWord)) {
            throw new WordNotFoundInDictionary(normalizedWord);
        }
        return normalizedWord;
    }

    private boolean matchesAttempts(String candidate) {
        for (Attempt attempt : attempts) {
            String hint = WordleDictionary.compareWords(attempt.word, candidate);
            if (!attempt.hint.equals(hint)) {
                return false;
            }
        }
        return true;
    }

    private boolean wasGuessed(String word) {
        for (Attempt attempt : attempts) {
            if (attempt.word.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private void log(String message) {
        if (log != null) {
            log.println(message);
            log.flush();
        }
    }

    private static class Attempt {
        private final String word;
        private final String hint;

        private Attempt(String word, String hint) {
            this.word = word;
            this.hint = hint;
        }
    }
}
