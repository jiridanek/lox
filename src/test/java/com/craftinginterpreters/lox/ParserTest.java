package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ParserTest {
    @Test
    void testExpressions() {
        record Test(String expr, String repr) { }
        var tests = List.of(
                new Test("1", "1.0"),
                new Test("1 + 1", "(+ 1.0 1.0)"),
                new Test("1 - 1", "(- 1.0 1.0)"),
                new Test("1 * 1", "(* 1.0 1.0)"),
                new Test("1 / 1", "(/ 1.0 1.0)"),

                new Test("1 + 2 * 3", "(+ 1.0 (* 2.0 3.0))"),

                new Test("/", null)
        );
        for (var test : tests) {
            String parsed = parse(test.expr);
            if (test.repr == null) {
                Assertions.assertNull(parsed);
            } else {
                Assertions.assertEquals(test.repr, parsed);
            }
        }
    }

    private String parse(String expr) {
        var lexer = new Scanner(expr);
        var parser = new Parser(lexer.scanTokens());
        Expr parsed = parser.parse();
        return (parsed != null) ? AstPrinter.stringify(parsed) : null;
    }
}