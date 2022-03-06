package org.artrayme.checker.exceptions;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class InvalidBracketsException extends Exception{
    public InvalidBracketsException() {
        super("Brackets are invalid");
    }
}
