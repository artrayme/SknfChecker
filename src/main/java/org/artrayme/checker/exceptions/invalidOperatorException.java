package org.artrayme.checker.exceptions;

public class invalidOperatorException extends Exception {
    public invalidOperatorException(Character operator) {
        super("You cannot use operator \"" + operator + "\"");
    }
}
