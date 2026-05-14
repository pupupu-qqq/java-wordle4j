package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) throws EmptyDictionaryException {
        List<String> words = new ArrayList<>();
        int linesRead = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                linesRead++;
                words.add(line);
            }
        } catch (IOException exception) {
            throw new DictionaryLoadException(fileName, exception);
        }

        WordleDictionary dictionary = new WordleDictionary(words);
        log("Dictionary file: " + fileName);
        log("Lines read: " + linesRead);
        log("Playable words: " + dictionary.size());

        if (dictionary.isEmpty()) {
            throw new EmptyDictionaryException(fileName);
        }
        return dictionary;
    }

    private void log(String message) {
        if (log != null) {
            log.println(message);
            log.flush();
        }
    }
}
