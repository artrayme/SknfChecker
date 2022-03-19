package org.artrayme.checker.util;

import java.util.concurrent.atomic.AtomicInteger;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
class StatesGenerator {
    private final AtomicInteger currentState = new AtomicInteger();

    public boolean[] getStates() {
        boolean[] booleans = new boolean[26];
        for (int i = 25; i >= 0; i--) {
            booleans[i] = ((currentState.get() & (1 << i)) != 0);
        }
        return booleans;
    }

    public boolean[] incrementAndGet() {
        currentState.incrementAndGet();
        return getStates();
    }

}
