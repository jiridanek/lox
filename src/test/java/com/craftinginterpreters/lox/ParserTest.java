package com.craftinginterpreters.lox;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ParserTest {
    @Test
    void testExpressions() {
        record Test(String expr, String repr) { }
        var tests = List.of(
                new Test("1;", "1.0"),
                new Test("1 + 1;", "(+ 1.0 1.0)"),
                new Test("1 - 1;", "(- 1.0 1.0)"),
                new Test("1 * 1;", "(* 1.0 1.0)"),
                new Test("1 / 1;", "(/ 1.0 1.0)"),

                new Test("1 + 2 * 3;", "(+ 1.0 (* 2.0 3.0))"),

                new Test("/;", null)
        );
        for (var test : tests) {
            if (test.repr == null) {
                Assertions.assertThrows(RuntimeException.class, () -> parseExpression((test.expr)));
            } else {
                String parsed = parseExpression(test.expr);
                Assertions.assertEquals(test.repr, parsed);
            }
        }
    }

    private String parseExpression(String expr) {
        var lexer = new Scanner(expr);
        var parser = new Parser(lexer.scanTokens());
        List<Stmt> parsed = parser.parse();
        if (!parsed.isEmpty()) {
            Assertions.assertEquals(1, parsed.size());
            return AstPrinter.stringify(parsed.getFirst().accept(new Stmt.Visitor<>() {
                @Override
                public Expr visitBlockStmt(Stmt.Block stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitExpressionStmt(Stmt.Expression stmt) {
                    return stmt.expression;
                }

                @Override
                public Expr visitFunctionStmt(Stmt.Function stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitIfStmt(Stmt.If stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitPrintStmt(Stmt.Print stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitReturnStmt(Stmt.Return stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitVarStmt(Stmt.Var stmt) {
                    throw new AssertionError("Should not reach here");
                }

                @Override
                public Expr visitWhileStmt(Stmt.While stmt) {
                    throw new AssertionError("Should not reach here");
                }
            }));
        }
        return null;
    }
}
