package org.artrayme.checker.parser;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
                Arguments.of("(!C)", Optional.empty()),
                Arguments.of(")(∧∨!ABC)", Optional.empty()),
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
            "true,false"
    })
    void checkAtomicSyntax(String expression, boolean result) {
        assertEquals(result, LEParser.checkAtomicSyntax(expression));
    }

    @Test
    void checkAtomicMethod() {
        assertFalse(LEParser.checkAtomicSyntax(""));
    }

    @ParameterizedTest
    @CsvSource({
            "(C∨(!A1234)),2",
            "(C∨(A)¬),¬"
    })
    void invalidSyntaxCharacterExceptionTest(String expression, Character invalidSyntaxCharacter) {
        InvalidSyntaxCharacterException exception = assertThrows(InvalidSyntaxCharacterException.class, () -> {
            LEParser.valueOf(expression);
        });
        assertEquals(invalidSyntaxCharacter, exception.getInvalidCharacter());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "((A~!A)∨(!B)),!A",
            "((A!A)∨(!B)),!A",
            "(!AA),AA",
            "(A~A~A),A~A",
            "(~A),''",
            "(~),''",
            "(!),''",
            "((A∨!A)~(!B)),!A"
    })
    void invalidAtomicExpressionSyntaxExceptionTest(String expression, String invalidAtomicExpression) {
        InvalidAtomicExpressionSyntaxException invalidOperatorException = assertThrows(InvalidAtomicExpressionSyntaxException.class, () -> {
            LEParser.valueOf(expression);
        });
        assertEquals(invalidAtomicExpression, invalidOperatorException.getExpression());
    }

    @ParameterizedTest
    @CsvSource({
            "((A∨B)),(A∨B)",
            "((A∧B)∧((C∨)!D)),!",
            "((A~A~A)),(A~A~A)",
            "((!C)!B),!",
            "((A∨B)∧(B∨(!A)!)),!"
    })
    void invalidOperatorExceptionTest(String expression, String invalidOperator) {
        InvalidOperatorException invalidOperatorException = assertThrows(InvalidOperatorException.class, () -> {
            LEParser.valueOf(expression);
        });
        assertEquals(invalidOperator, invalidOperatorException.getInvalidOperator());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "(A∧B)",
            "(C∧(A∨B))",
            "(C∨(!1))",
            "((A∨((!B)∨C))∧(A∨(B∨C)))",
            "(!A)",
            "((A∧B)→B)",
            "A",
            "1",
            "0",
            "(!((A∧B)→B))",
            "((!((A∧B)→B))~B)",
            "(((!((A∧B)→1))~0)∧(!A))",
            "(A~A)",
            "(A→B)",
            "(!(!A))",
            "(A~(0~A))"
    })
    void validExpressions(String expression) {
        assertDoesNotThrow(() -> {
            LEParser.valueOf(expression);
        });
    }

    @Test
    void testValueOf12() {
        assertThrows(InvalidBracketsException.class, () -> {
            LEParser.valueOf("((!()∨)∧())");
        });

    }

    @Test
    void testValueOf13() {
        assertThrows(InvalidBracketsException.class, () -> {
            LEParser.valueOf("A∨A");
        });

    }

//    @Test
//    void temp() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
//                LEParser.valueOf();
//    }

}