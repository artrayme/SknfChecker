package org.artrayme.checker.exceptions;

public class InvalidSyntaxCharacterException extends Exception {
    public InvalidSyntaxCharacterException(Character invalidChar) {
        super("You cannot use '" + invalidChar + "' character");
    }
}
