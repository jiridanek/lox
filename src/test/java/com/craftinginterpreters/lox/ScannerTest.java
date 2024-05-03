package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTest {
    // https://craftinginterpreters.com/scanning.html#longer-lexemes
    @Test
    void testSample01() {
        var source = """
                // this is a comment
                (( )){} // grouping stuff
                !*+-/=<> <= == // operators
                """;

        var scanner = new Scanner(source);
        var tokens = scanner.scanTokens();

        assertThat(tokens).usingRecursiveComparison().isEqualTo(List.of(
                new Token(LEFT_PAREN, "(", null, 2),
                new Token(LEFT_PAREN, "(", null, 2),
                new Token(RIGHT_PAREN, ")", null, 2),
                new Token(RIGHT_PAREN, ")", null, 2),
                new Token(LEFT_BRACE, "{", null, 2),
                new Token(RIGHT_BRACE, "}", null, 2),

                new Token(BANG, "!", null, 3),
                new Token(STAR, "*", null, 3),
                new Token(PLUS, "+", null, 3),
                new Token(MINUS, "-", null, 3),
                new Token(SLASH, "/", null, 3),
                new Token(EQUAL, "=", null, 3),
                new Token(LESS, "<", null, 3),
                new Token(GREATER, ">", null, 3),
                new Token(LESS_EQUAL, "<=", null, 3),
                new Token(EQUAL_EQUAL, "==", null, 3),

                new Token(EOF, "", null, 4)
        ));
    }

    @Test
    void testSampleFiles() throws IOException {
        var srcs = Paths.get("src/main/lox");
        Files.list(srcs).forEach((src) -> {
            try {
                var str = Files.readString(src);
                var scanner = new Scanner(str);
                var tokens = scanner.scanTokens();
                System.out.println(src + ", Tokens: " + tokens);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
