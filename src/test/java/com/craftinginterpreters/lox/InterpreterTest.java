package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class InterpreterTest {
    @Test
    void testSimpleProgram() {
        var program = """
                print "one";
                print true;
                print 2 + 1;
                """;

        var scanner = new Scanner(program);
        var parser = new Parser(scanner.scanTokens());
        var interpreter = new Interpreter();

        var stdout = System.out;
        try {
            var data = new ByteArrayOutputStream();
            var out = new PrintStream(data);
            System.setOut(out);

            interpreter.interpret(parser.parse());
            Assertions.assertEquals("one\ntrue\n3\n", data.toString());
        } finally {
            System.setOut(stdout);
        }
    }
}
