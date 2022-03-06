package org.artrayme.checker.util;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LETree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
class SknfUtilTest {

    @Test
    void createSknf() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∧C)∧(A∨(B∨D)))");
        LETree result = SknfUtil.createSknf(expression);
        System.out.println(result);
    }

    @Test
    void checkSknf1() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∧C)∧(A∨(B∨D)))");
        LETree result = SknfUtil.createSknf(expression);
        boolean isSknf = SknfUtil.isSknf(result);
        assertTrue(isSknf);
    }

    @Test
    void checkSknf12() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(0∧((A∧C)∧(A∨(B∨D))))");
        LETree result = SknfUtil.createSknf(expression);
        boolean isSknf = SknfUtil.isSknf(result);
        assertTrue(isSknf);
    }

    @Test
    void checkSknf13() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(1∧((A∧C)∧(A∨(B∨D))))");
        LETree result = SknfUtil.createSknf(expression);
        boolean isSknf = SknfUtil.isSknf(result);
    }

    @Test
    void checkSknf2() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((¬B)∨(C∨(¬D))))∧((A∨((¬B)∨(C∨D)))∧(A∨(B∨(C∨D)))))");
        boolean isSknf = SknfUtil.isSknf(expression);
        assertTrue(isSknf);
    }

    @Test
    void checkSknf3() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((¬B)∨(C∨(¬D))))∧((A∨((¬B)∨(C∨D)))∧(A∨(B∨(D∨C)))))");
        boolean isSknf = SknfUtil.isSknf(expression);
        assertTrue(isSknf);
    }

    @Test
    void checkSknf4() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(1∨((A∨((¬B)∨(C∨(¬D))))∧((A∨((¬B)∨(C∨D)))∧(A∨(B∨(D∨C))))))");
        boolean isSknf = SknfUtil.isSknf(expression);
        assertFalse(isSknf);
    }

    @Test
    void checkSknf5() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("1");
        boolean isSknf = SknfUtil.isSknf(expression);
        assertTrue(isSknf);
    }

//    @Test
//    void createSknf2() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
//        LETree expression = LEParser.valueOf("(((¬A)∨(B∨C))∧((A∨C)∨(¬B)))");
//        SknfUtil.createSknf(expression);
//
//    }

}