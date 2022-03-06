package org.artrayme.checker.exceptions;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class InvalidOperatorException extends Exception {
    private final String invalidOperator;

    public InvalidOperatorException(String operator) {
        super("You cannot use operator \"" + operator + "\" hear");
        invalidOperator = operator;
    }

    public InvalidOperatorException(Character operator) {
        super("You cannot use operator \"" + operator + "\" hear");
        invalidOperator = String.valueOf(operator);
    }

    public String getInvalidOperator() {
        return invalidOperator;
    }
}
