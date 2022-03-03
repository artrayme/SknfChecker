package org.artrayme.checker.parser;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.exceptions.invalidOperatorException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LEParserTest {

    private static Stream<Arguments> expressionProvider() {
        return Stream.of(
                Arguments.of("(A∧B)", Optional.empty()),
                Arguments.of("(A∧(B∧C))", Optional.empty()),
                Arguments.of("(A1∨B2)", Optional.empty()),
                Arguments.of("(¬C3)", Optional.empty()),
                Arguments.of(")(∧∨¬ABC)012", Optional.empty()),
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
        assertEquals(result, LEParser.checkBrackets(expression));
    }

    @Test
    void checkBracketsMethod() {
        assertTrue(LEParser.checkBrackets(""));
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
            "B1,true",
            "B123,true",
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
    void testValueOf1() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(A∧B)");
        System.out.println(tree);
    }

    @Test
    void testValueOf2() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(C∧(A∨B))");
        System.out.println(tree);
    }

    @Test
    void testValueOf3() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(C∨(¬A))");
        System.out.println(tree);
    }

    @Test
    void testValueOf4() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(C∨(¬A1234))");
        System.out.println(tree);
    }

    @Test
    void testValueOf5() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("((A∨B))");
        System.out.println(tree);
    }

    @Test
    void testValueOf6() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(¬AA)");
        System.out.println(tree);
    }

    @Test
    void testValueOf7() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("((A~¬A)∨(¬B))");
        System.out.println(tree);
    }


    @Test
    void testValueOf8() throws InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException, invalidOperatorException {
        var tree = LEParser.valueOf("(()∧())");
        System.out.println(tree);
    }

}