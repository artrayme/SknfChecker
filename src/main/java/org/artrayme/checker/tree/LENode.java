package org.artrayme.checker.tree;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public class LENode {
    private final String expression;
    private BiPredicate<Boolean, Boolean> operator;
    private char operatorSymbol;
    private LENode leftChild;
    private LENode rightChild;

    public LENode(String expression) {
        this.expression = expression;
    }

    public LENode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(LENode leftChild) {
        this.leftChild = leftChild;
    }

    public LENode getRightChild() {
        return rightChild;
    }

    public void setRightChild(LENode rightChild) {
        this.rightChild = rightChild;
    }

    public BiPredicate<Boolean, Boolean> getOperator() {
        return operator;
    }

    public char getOperatorSymbol() {
        return operatorSymbol;
    }

    public void setOperator(BiPredicate<Boolean, Boolean> operator, char operatorSymbol) {
        this.operator = operator;
        this.operatorSymbol = operatorSymbol;
    }

    public String getExpression() {
        return expression;
    }

    public boolean calcValue(Map<String, Boolean> parameters) {
        if (rightChild == null)
            return parameters.get(expression);
        else {
            return operator.test(leftChild.calcValue(parameters), rightChild.calcValue(parameters));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LENode node = (LENode) o;
        return Objects.equals(expression, node.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }
}
