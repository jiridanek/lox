package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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

    @Test
    void testFibonacciFor() {
        Supplier<ArrayList<Integer>> fib = () -> {
            var result = new ArrayList<Integer>();

            var a = 0;
            int temp;

            for (var b = 1; a < 10_000; b = temp + b) {
                result.add(a);
                temp = a;
                a = b;
            }

            return result;
        };

        var program = """
                var a = 0;
                var temp;

                for (var b = 1; a < 10000; b = temp + b) {
                  print a;
                  temp = a;
                  a = b;
                }
                """;
        runExpectingOutput(program, fib.get().stream().map(Object::toString).collect(Collectors.joining("\n")) + "\n");
    }

    @Test
    void testRunFunction() {
        var program = """
                fun sayHi(first, last) {
                  print "Hi, " + first + " " + last + "!";
                }

                sayHi("Dear", "Reader");
                """;
        runExpectingOutput(program, "Hi, Dear Reader!\n");
    }

    private int fib(int n) {
        if (n <= 1) return n;
        return fib(n - 2) + fib(n - 1);
    }

    @Test
    void testFibonacciRec() {
        var result = new ArrayList<Integer>();
        for (var i = 0; i < 20; i = i + 1) {
            result.add(fib(i));
        }

        var program = """
                fun fib(n) {
                  if (n <= 1) return n;
                  return fib(n - 2) + fib(n - 1);
                }

                for (var i = 0; i < 20; i = i + 1) {
                  print fib(i);
                }
                """;
        runExpectingOutput(program, result.stream().map(Objects::toString).collect(Collectors.joining("\n")) + "\n");
    }

    @Test
    void testClosure() {
        var program = """
                fun makeCounter() {
                  var i = 0;
                  fun count() {
                    i = i + 1;
                    print i;
                  }

                  return count;
                }

                var counter = makeCounter();
                counter(); // "1".
                counter(); // "2".
                """;
        runExpectingOutput(program, "1\n2\n");
    }

    @Test
    void testBasicClass() {
        var program = """
                class DevonshireCream {
                  serveOn() {
                    return "Scones";
                  }
                }
                print DevonshireCream; // Prints "DevonshireCream".
                """;
        runExpectingOutput(program, "DevonshireCream\n");
    }

    @Test
    void testBasicClassInstance() {
        var program = """
                class Bagel {}
                var bagel = Bagel();
                print bagel; // Prints "Bagel instance".
                """;
        runExpectingOutput(program, "Bagel instance\n");
    }

    @Test
    void testBasicClassProperties() {
        var program = """
                class Bagel {}
                var bagel = Bagel();
                bagel.size = 42;
                print bagel.size;
                """;
        runExpectingOutput(program, "42\n");
    }

    @Test
    void testBasicClassMethodCall() {
        var program = """
                class Bacon {
                  eat() {
                    print "Crunch crunch crunch!";
                  }
                }

                Bacon().eat();
                """;
        runExpectingOutput(program, "Crunch crunch crunch!\n");
    }

    private static void runExpectingOutput(String program, String expected) {
        var scanner = new Scanner(program);
        var parser = new Parser(scanner.scanTokens());
        var interpreter = new Interpreter();
        var resolver = new Resolver(interpreter);

        var stdout = System.out;
        try {
            var data = new ByteArrayOutputStream();
            var out = new PrintStream(data);
            System.setOut(out);

            List<Stmt> statements = parser.parse();
            resolver.resolve(statements);
            interpreter.interpret(statements);
            Assertions.assertEquals(expected, data.toString());
        } finally {
            System.setOut(stdout);
        }
    }
}
