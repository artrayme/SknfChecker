package org.artrayme.checker.exceptions;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class InvalidSyntaxCharacterException extends Exception {
    private final char invalidCharacter;

    public InvalidSyntaxCharacterException(Character invalidChar) {
        super("You cannot use '" + invalidChar + "' character");
        invalidCharacter = invalidChar;
    }

    public InvalidSyntaxCharacterException(String customMessage, Character invalidChar) {
        super(customMessage);
        invalidCharacter = invalidChar;
    }

    public char getInvalidCharacter() {
        return invalidCharacter;
    }
}
