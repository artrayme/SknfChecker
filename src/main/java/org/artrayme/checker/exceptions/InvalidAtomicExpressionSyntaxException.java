package org.artrayme.checker.exceptions;

public class InvalidAtomicExpressionSyntaxException extends Exception {
    public InvalidAtomicExpressionSyntaxException(String expression) {
        super("Expression \"" + expression + "\" is not valid");
    }
}
