package org.artrayme.checker.util;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
class StatesGenerator {
    private int currentState = 0;

    public boolean[] getStates() {
        boolean[] booleans = new boolean[32];
        for (int i = 31; i >= 0; i--) {
            booleans[i] = ((currentState & (1 << i)) != 0);
        }
        return booleans;
    }

    public boolean[] incrementAndGet() {
        currentState++;
        return getStates();
    }

}
