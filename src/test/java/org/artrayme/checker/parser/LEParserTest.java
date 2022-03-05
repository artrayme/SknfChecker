package org.artrayme.checker.parser;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LEParserTest {

    private static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("(A∧B)", Optional.empty()),
                Arguments.of("(A∧(B∧C))", Optional.empty()),
                Arguments.of("(A∨B)", Optional.empty()),
                Arguments.of("(¬C)", Optional.empty()),
                Arguments.of(")(∧∨¬ABC)", Optional.empty()),
                Arguments.of("a", Optional.of('a')),
                Arguments.of("(A∧b∧c)", Optional.of('b')),
                Arguments.of("-", Optional.of('-'))
        );
    }

    @ParameterizedTest
    @CsvSource({
            "(),true",
            "()),false",
            "((),false",
            ")(,false",
            "(()()),true",
            "(,false",
            "),false",
            "((())),true",
            "()(),false",
            "(())(),false",
            "(()()()),true"
    })
    void checkBracketsValidity(String expression, boolean result) {
        assertEquals(result, LEParser.checkBrackets(expression) != -1);
    }

    @Test
    void checkBracketsMethod() {
        assertTrue(LEParser.checkBrackets("") != -1);
    }

    @ParameterizedTest()
    @MethodSource("expressionProvider")
    void checkSymbolsValidity(String expression, Optional<Character> result) {
        assertEquals(result, LEParser.checkSymbolsValidity(expression));
    }

    @ParameterizedTest
    @CsvSource({
            "A,true",
            "a,false",
            "A0,false",
            "A01,false",
            "B,true",
            "B1,false",
            "B123,false",
            "B123A,false",
            "-,false",
            "0,false"
    })
    void checkAtomicSyntax(String expression, boolean result) {
        assertEquals(result, LEParser.checkAtomicSyntax(expression));
    }

    @Test
    void checkAtomicMethod() {
        assertFalse(LEParser.checkAtomicSyntax(""));
    }

    @Test
    void testValueOf1() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, InvalidOperatorException {
        LEParser.valueOf("(A∧B)");
    }

    @Test
    void testValueOf2() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, InvalidOperatorException {
        LEParser.valueOf("(C∧(A∨B))");
    }

    @Test
    void testValueOf3() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, InvalidOperatorException {
        LEParser.valueOf("(C∨(¬A))");
    }

    @Test
    void testValueOf4() {
        assertThrows(InvalidSyntaxCharacterException.class, () -> {
            LEParser.valueOf("(C∨(¬A1234))");
        });
    }

    @Test
    void testValueOf5() {
        assertThrows(InvalidOperatorException.class, () -> {
            LEParser.valueOf("((A∨B))");
        });

    }

    @Test
    void testValueOf6() {
        assertThrows(InvalidAtomicExpressionSyntaxException.class, () -> {
            LEParser.valueOf("(¬AA)");
        });
    }

    @Test
    void testValueOf7() {
        assertThrows(InvalidAtomicExpressionSyntaxException.class, () -> {
            LEParser.valueOf("((A~¬A)∨(¬B))");
        });
    }


    @Test
    void testValueOf8() {
        InvalidAtomicExpressionSyntaxException invalidOperatorException = assertThrows(InvalidAtomicExpressionSyntaxException.class, () -> {
            LEParser.valueOf("((A~¬A)∨(¬B))");
        });

    }

    @Test
    void testValueOf9() {
        InvalidOperatorException invalidOperatorException = assertThrows(InvalidOperatorException.class, () -> {
            LEParser.valueOf("((A∧B)∧((C∨)¬D))");
        });

        assertEquals("¬", invalidOperatorException.getInvalidOperator());
    }

    @Test
    void testValueOf10() {
        InvalidOperatorException invalidOperatorException = assertThrows(InvalidOperatorException.class, () -> {
            LEParser.valueOf("((A~A~A))");
        });

    }

    @Test
    void testValueOf11() {
        assertThrows(InvalidAtomicExpressionSyntaxException.class, () -> {
            LEParser.valueOf("((A¬A)∨(¬B))");
        });

    }

    @Test
    void testValueOf12() {
        assertThrows(InvalidBracketsException.class, () -> {
            LEParser.valueOf("((¬()∨)∧())");
        });

    }

    @Test
    void testValueOf13() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LEParser.valueOf("((A∨((¬B)∨C))∧(A∨(B∨C)))");

    }

}