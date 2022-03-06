package org.artrayme.checker.exceptions;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class InvalidAtomicExpressionSyntaxException extends Exception {
    private final String expression;
    public InvalidAtomicExpressionSyntaxException(String expression) {
        super("Expression \"" + expression + "\" is not valid");
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
