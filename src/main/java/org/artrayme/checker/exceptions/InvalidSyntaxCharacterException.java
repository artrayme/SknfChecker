package org.artrayme.checker.exceptions;

public class InvalidSyntaxCharacterException extends Exception {
    private final char invalidCharacter;
    public InvalidSyntaxCharacterException(Character invalidChar) {
        super("You cannot use '" + invalidChar + "' character");
        invalidCharacter = invalidChar;
    }

    public char getInvalidCharacter() {
        return invalidCharacter;
    }
}
