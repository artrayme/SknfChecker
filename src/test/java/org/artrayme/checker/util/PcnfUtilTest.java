package org.artrayme.checker.util;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LETree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class PcnfUtilTest {

    @Test
    void checkPcnf1() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∧C)∧(A∨(B∨D)))");
        var result = PcnfUtil.createPcnf(expression);
        boolean isPcnf = PcnfUtil.isPcnf(LEParser.valueOf(result));
        assertTrue(isPcnf);
    }

    @Test
    void checkPcnf12() {
        var exception = assertThrows(InvalidSyntaxCharacterException.class, () -> {
            LETree expression = LEParser.valueOf("(0∧((A∧C)∧(A∨(B∨D))))");
            PcnfUtil.createPcnf(expression);
        });
        assertEquals(' ', exception.getInvalidCharacter());
    }

    @Test
    void checkPcnf13() {
        var exception = assertThrows(InvalidSyntaxCharacterException.class, () -> {
            LETree expression = LEParser.valueOf("(1∧((A∧C)∧(A∨(B∨D))))");
            PcnfUtil.createPcnf(expression);
        });
        assertEquals(' ', exception.getInvalidCharacter());
    }

    @Test
    void checkPcnf2() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((!B)∨(C∨(!D))))∧((A∨((!B)∨(C∨D)))∧(A∨(B∨(C∨D)))))");
        boolean isPcnf = PcnfUtil.isPcnf(expression);
        assertTrue(isPcnf);
    }

    @Test
    void checkPcnf3() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((!B)∨(C∨(!D))))∧((A∨((!B)∨(C∨D)))∧(A∨(B∨(D∨C)))))");
        boolean isPcnf = PcnfUtil.isPcnf(expression);
        assertTrue(isPcnf);
    }

    @Test
    void checkPcnf4() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(1∨((A∨((!B)∨(C∨(!D))))∧((A∨((!B)∨(C∨D)))∧(A∨(B∨(D∨C))))))");
        boolean isPcnf = PcnfUtil.isPcnf(expression);
        assertFalse(isPcnf);
    }

    @Test
    void checkPcnf5() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("1");
        boolean isPcnf = PcnfUtil.isPcnf(expression);
        assertFalse(isPcnf);
    }

    @Test
    void checkPcnf6() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(1∧1)");
        boolean isPcnf = PcnfUtil.isPcnf(expression);
        assertFalse(isPcnf);
    }

    @Test
    void checkPcnf14() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∧C)∧(A∨(B∨D)))");
        var lastRoot = PcnfUtil.createPcnf(expression);
        for (int i = 0; i < 5; i++) {
            expression = LEParser.valueOf(PcnfUtil.createPcnf(expression));
            assertTrue(PcnfUtil.isPcnf(expression));
            assertEquals(expression.getRoot().getExpression().length(), lastRoot.length());
            lastRoot = expression.getRoot().getExpression();
        }
    }

    @Test
    void checkPcnf15() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(A∨(B∨(C∨(D∨(E∨(F∨(G∨(H∨(I∨(J∨(K∨(L∨(M∨(N∨(O∨(P∨(Q∨(R∨(S∨(T∨(U∨(V∨(W∨(X∨(Y∨Z)))))))))))))))))))))))))");
        System.out.println(PcnfUtil.createPcnf(expression).length());
    }

    @Test
    void checkPcnf16() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("(A∧(B∧(C∧(D∧(E∧(F∧(G∧(H∧(I∧(J∧(K∧(L∧(M∧(N∧(O∧(P∧(Q∧(R∧(S∧(T∧(U∧(V∧W))))))))))))))))))))))");
        System.out.println(PcnfUtil.createPcnf(expression).length());
    }

    @Test
    void checkPcnf17() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨((!C)∨B))∧((!C)∨(!((!A)∨B))))");
        var result = PcnfUtil.isPcnf(expression);
        assertFalse(result);
    }

    @Test
    void checkPcnf18() throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LETree expression = LEParser.valueOf("((A∨(!A))∧B)");
        expression = LEParser.valueOf(PcnfUtil.createPcnf(expression));
        var result = PcnfUtil.isPcnf(expression);
        assertTrue(result);
        assertEquals("(((!A)∨B)∧(A∨B))", expression.getRoot().getExpression());
    }

}