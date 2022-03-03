package org.artrayme.checker.exceptions;

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
