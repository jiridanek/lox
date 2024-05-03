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
        runExpectingOutput(program, "one\ntrue\n3\n");
    }

    @Test
    void testGlobalVariableDecl() {
        var program = """
                var a = 1;
                var b = 2;
                print a + b;
                """;
        runExpectingOutput(program, "3\n");
    }

    @Test
    void testGlobalVariableAssignment() {
        var program = """
                var a = 1;
                a = 2;
                print a;
                """;
        runExpectingOutput(program, "2\n");
    }

    @Test
    void testNestedBlocks() {
        var program = """
                var a = "global a";
                var b = "global b";
                var c = "global c";
                {
                  var a = "outer a";
                  var b = "outer b";
                  {
                    var a = "inner a";
                    print a;
                    print b;
                    print c;
                  }
                  print a;
                  print b;
                  print c;
                }
                print a;
                print b;
                print c;
                """;
        runExpectingOutput(program,
                "inner a\nouter b\nglobal c\n" +
                "outer a\nouter b\nglobal c\n" +
                "global a\nglobal b\nglobal c\n");
    }

    private static void runExpectingOutput(String program, String expected) {
        var scanner = new Scanner(program);
        var parser = new Parser(scanner.scanTokens());
        var interpreter = new Interpreter();

        var stdout = System.out;
        try {
            var data = new ByteArrayOutputStream();
            var out = new PrintStream(data);
            System.setOut(out);

            interpreter.interpret(parser.parse());
            Assertions.assertEquals(expected, data.toString());
        } finally {
            System.setOut(stdout);
        }
    }
}