package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordleTest {

    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        dictionary = new WordleDictionary(Arrays.asList(
                "герой",
                "гонец",
                "город",
                "берег",
                "ежики",
                "маска",
                "тесто",
                "сосна"
        ));
    }

    @Test
    void dictionaryNormalizesFiltersAndRemovesDuplicates() {
        WordleDictionary actual = new WordleDictionary(Arrays.asList(
                " ЁЖИКИ ",
                "ежики",
                "кот",
                "wordl",
                "маска"
        ));

        assertEquals(Arrays.asList("ежики", "маска"), actual.getWords());
        assertTrue(actual.contains("ёжики"));
        assertFalse(actual.contains("wordl"));
    }

    @Test
    void compareWordsReturnsExpectedWordleMask() {
        assertEquals("+^-^-", WordleDictionary.compareWords("гонец", "герой"));
    }

    @Test
    void compareWordsHandlesDuplicateLetters() {
        assertEquals("+^-^^", WordleDictionary.compareWords("сосна", "салон"));
        assertEquals("+-+--", WordleDictionary.compareWords("ссссс", "сосна"));
    }

    @Test
    void correctMoveWinsAndSpendsOneStep() throws InvalidWordException, WordNotFoundInDictionary {
        WordleGame game = new WordleGame(dictionary, "герой", testLog());

        String hint = game.makeMove("герой");

        assertEquals("+++++", hint);
        assertTrue(game.isWon());
        assertTrue(game.isFinished());
        assertEquals(5, game.getRemainingSteps());
    }

    @Test
    void invalidMoveDoesNotSpendStep() {
        WordleGame game = new WordleGame(dictionary, "герой", testLog());

        assertThrows(InvalidWordException.class, () -> game.makeMove("word"));
        assertEquals(6, game.getRemainingSteps());
    }

    @Test
    void unknownDictionaryWordDoesNotSpendStep() {
        WordleGame game = new WordleGame(dictionary, "герой", testLog());

        assertThrows(WordNotFoundInDictionary.class, () -> game.makeMove("кошка"));
        assertEquals(6, game.getRemainingSteps());
    }

    @Test
    void suggestionMatchesPreviousAttemptsAndIsNotRepeated() throws InvalidWordException, WordNotFoundInDictionary {
        WordleGame game = new WordleGame(dictionary, "герой", testLog());

        game.makeMove("гонец");
        List<String> possibleWords = game.getPossibleWords();
        String suggestion = game.suggestWord();

        assertTrue(possibleWords.contains("герой"));
        assertTrue(possibleWords.contains(suggestion));
        assertEquals("герой", suggestion);
    }

    @Test
    void loaderReadsUtf8DictionaryAndFiltersWords() throws IOException, EmptyDictionaryException {
        Path dictionaryFile = Files.createTempFile("wordle-test", ".txt");
        Files.write(dictionaryFile, Arrays.asList("ЁЖИКИ", "кот", "маска"), StandardCharsets.UTF_8);

        WordleDictionary loaded = new WordleDictionaryLoader(testLog()).load(dictionaryFile.toString());

        assertEquals(Arrays.asList("ежики", "маска"), loaded.getWords());
    }

    @Test
    void loaderThrowsOwnExceptionWhenFileCannotBeRead() {
        assertThrows(DictionaryLoadException.class, () ->
                new WordleDictionaryLoader(testLog()).load("missing-dictionary-file.txt"));
    }

    private PrintWriter testLog() {
        return new PrintWriter(System.out);
    }
}
