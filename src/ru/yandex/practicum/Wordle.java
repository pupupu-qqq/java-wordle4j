package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try (PrintWriter log = createLog()) {
            try {
                run(log);
            } catch (EmptyDictionaryException exception) {
                log.println(exception.getMessage());
                log.flush();
                System.out.println("Игра завершилась с ошибкой. Подробности записаны в " + LOG_FILE);
            }
        }
    }

    private static PrintWriter createLog() {
        try {
            return new PrintWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8));
        } catch (java.io.IOException exception) {
            throw new LogFileException(LOG_FILE, exception);
        }
    }

    private static void run(PrintWriter log) throws EmptyDictionaryException {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
        WordleGame game = new WordleGame(dictionary, log);

        System.out.println("Игра Wordle. Введите слово из пяти русских букв.");
        System.out.println("Пустая строка попросит компьютер сделать ход-подсказку.");

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (!game.isFinished() && scanner.hasNextLine()) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) {
                    makeComputerMove(game);
                } else {
                    makePlayerMove(game, input);
                }
            }
        }

        if (game.isWon()) {
            System.out.println("Победа!");
        } else if (game.isLost()) {
            System.out.println("Попытки закончились.");
        }
        System.out.println("Загаданное слово: " + game.getAnswer());
    }

    private static void makePlayerMove(WordleGame game, String input) {
        try {
            String normalizedInput = WordleDictionary.normalize(input);
            String hint = game.makeMove(input);
            System.out.println(normalizedInput);
            System.out.println(hint);
            System.out.println("Осталось попыток: " + game.getRemainingSteps());
        } catch (InvalidWordException | WordNotFoundInDictionary exception) {
            System.out.println(exception.getMessage());
        }
    }

    private static void makeComputerMove(WordleGame game) {
        try {
            String suggestion = game.suggestWord();
            String hint = game.makeMove(suggestion);
            System.out.println(suggestion);
            System.out.println(hint);
            System.out.println("Осталось попыток: " + game.getRemainingSteps());
        } catch (InvalidWordException | WordNotFoundInDictionary exception) {
            System.out.println(exception.getMessage());
        }
    }
}
