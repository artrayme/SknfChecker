package org.artrayme.checker.util;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LETree;
import org.junit.jupiter.api.Test;

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
        System.out.println(result.getRoot().getExpression());
        boolean isSknf = SknfUtil.isSknf(result);
        System.out.println(isSknf);
    }

    @Test
    void checkSknf2() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((¬B)∨(C∨(¬D))))∧((A∨((¬B)∨(C∨D)))∧(A∨(B∨(C∨D)))))");
        boolean isSknf = SknfUtil.isSknf(expression);
        System.out.println(isSknf);
    }

//    @Test
//    void createSknf2() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
//        LETree expression = LEParser.valueOf("(((¬A)∨(B∨C))∧((A∨C)∨(¬B)))");
//        SknfUtil.createSknf(expression);
//
//    }

}