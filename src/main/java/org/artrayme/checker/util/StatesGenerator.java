package org.artrayme.checker.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class StatesGenerator {
    private int currentState = 0;

    public List<Boolean> getStates() {
        List<Boolean> booleans = new ArrayList<>();
        for (int i = 31; i >= 0; i--) {
            booleans.add((currentState & (1 << i)) != 0);
        }
        Collections.reverse(booleans);
        return booleans;
    }

    public List<Boolean> incrementAndGet() {
        currentState++;
        return getStates();
    }

}
