package org.artrayme.checker.parser;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.tree.LENode;
import org.artrayme.checker.tree.LETree;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LEParser {
    private static final char OPEN_BRACKET = '(';
    private static final char CLOSE_BRACKET = ')';
    private static final char CONJUNCTION = '∧';
    private static final char DISJUNCTION = '∨';
    private static final char NEGATION = '¬';
    private static final char EQUALITY = '~';
    private static final char IMPLICIT = '→';

    private LEParser() {
    }

    public static LETree valueOf(String expression) throws InvalidBracketsException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidOperatorException {
        if (expression.length() < 4)
            throw new InvalidSyntaxCharacterException(' ');
        LEParser LEParser = new LEParser();
        if (checkBrackets(expression) == -1) {
            throw new InvalidBracketsException();
        }
        Optional<Character> invalidSymbol = checkSymbolsValidity(expression);
        if (invalidSymbol.isPresent()) {
            throw new InvalidSyntaxCharacterException(invalidSymbol.get());
        }
        LETree leTree = new LETree(LEParser.parseRecursive(expression));
        return leTree;
    }

    public static int checkBrackets(String expression) {
        if (expression.isEmpty())
            return 0;
        if (expression.charAt(0) != OPEN_BRACKET || expression.charAt(expression.length() - 1) != CLOSE_BRACKET) {
            return -1;
        }

        int maxDepth = 0;
        int counter = 0;
        for (int i = 0; i < expression.length(); i++) {
            if (counter > maxDepth)
                maxDepth = counter;
            char c = expression.charAt(i);
            if (c == OPEN_BRACKET)
                counter++;
            if (c == CLOSE_BRACKET)
                counter--;
            if (counter < 0)
                return -1;
            if (counter == 0 && i < expression.length() - 1)
                return -1;
        }
        if (counter != 0) {
            return -1;
        }
        return maxDepth;
    }

    public static Optional<Character> checkSymbolsValidity(String expression) {
        for (char c : expression.toCharArray()) {
            if ((c < 'A' || c > 'Z')
                    //                    && (c < '0' || c > '9')
                    && c != OPEN_BRACKET
                    && c != CLOSE_BRACKET
                    && c != CONJUNCTION
                    && c != DISJUNCTION
                    && c != NEGATION
                    && c != EQUALITY
            ) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public static boolean checkAtomicSyntax(String expression) {
        if (expression.length() != 1)
            return false;
        return expression.charAt(0) >= 'A' && expression.charAt(0) <= 'Z';
    }

    private LENode parseRecursive(String expressionPart) throws InvalidAtomicExpressionSyntaxException, InvalidOperatorException {
        if (expressionPart.isEmpty())
            throw new InvalidAtomicExpressionSyntaxException(expressionPart);
        if (expressionPart.charAt(0) != OPEN_BRACKET) {
            if (!checkAtomicSyntax(expressionPart)) {
                throw new InvalidAtomicExpressionSyntaxException(expressionPart);
            }
            return new LENode(expressionPart);
        }
        if (expressionPart.length() < 3)
            throw new InvalidAtomicExpressionSyntaxException(expressionPart);

        LENode node = new LENode(expressionPart);
        int index = 0;
        if (expressionPart.charAt(1) == NEGATION) {
            node.setOperator(convertToOperator(NEGATION), NEGATION);
            node.setRightChild(parseRecursive(expressionPart.substring(2, expressionPart.length() - 1)));
        } else if (checkBrackets(expressionPart) == 1) {
            flatExpression(expressionPart.substring(1, expressionPart.length() - 1), node);
        } else {
            deepExpression(expressionPart, node);
        }
        return node;

    }

    private String getPartByRule(AtomicInteger index, String expressionPart, Predicate<Character> rule) {
        StringBuilder part = new StringBuilder();
        while (index.get() < expressionPart.length() && rule.test(expressionPart.charAt(index.get()))) {
            part.append(expressionPart.charAt(index.get()));
            index.incrementAndGet();
        }
        return part.toString();
    }

    private void flatExpression(String expressionPart, LENode node) throws InvalidOperatorException, InvalidAtomicExpressionSyntaxException {
        AtomicInteger index = new AtomicInteger();
        String firstPart = getPartByRule(index, expressionPart, e -> e >= 'A' && e <= 'Z');
        if (!checkAtomicSyntax(firstPart)) {
            throw new InvalidAtomicExpressionSyntaxException(firstPart);
        }
        String operator = getPartByRule(index, expressionPart, e -> e < 'A' || e > 'Z');
        if (operator.length() > 1)
            throw new InvalidOperatorException(operator);

//        String secondPart = getPartByRule(index, expressionPart, e -> e >= 'A' && e <= 'Z');

        if (index.get() >= expressionPart.length())
            throw new InvalidOperatorException(expressionPart);

        String secondPart = expressionPart.substring(index.get());
        if (!checkAtomicSyntax(secondPart)) {
            throw new InvalidAtomicExpressionSyntaxException(secondPart);
        }



        node.setLeftChild(new LENode(firstPart));
        node.setOperator(convertToOperator(operator.charAt(0)), operator.charAt(0));
        node.setRightChild(new LENode(secondPart));
    }

    private void deepExpression(String expressionPart, LENode node) throws InvalidOperatorException, InvalidAtomicExpressionSyntaxException {
        LEParsedEntity entity = splitExpression(expressionPart.substring(1, expressionPart.length() - 1));
        if (entity.getOperator().length() != 1)
            throw new InvalidOperatorException(entity.getOperator());
        node.setOperator(convertToOperator(entity.getOperator().charAt(0)), entity.getOperator().charAt(0));
        node.setLeftChild(parseRecursive(entity.getFirstPart()));
        node.setRightChild(parseRecursive(entity.getSecondPart()));
    }

    private LEParsedEntity splitExpression(String expression) throws InvalidOperatorException {
        LEParsedEntity result = new LEParsedEntity();
        StringBuilder operator = new StringBuilder();
        AtomicInteger index = new AtomicInteger(0);
        result.setFirstPart(extractExpressionPart(expression, index));
        if (index.get() == expression.length())
            throw new InvalidOperatorException(expression);
        if (expression.charAt(index.get()) == NEGATION)
            throw new InvalidOperatorException(NEGATION);

        while (expression.charAt(index.get()) != OPEN_BRACKET) {
            operator.append(expression.charAt(index.get()));
            index.incrementAndGet();
        }
        result.setOperator(operator.toString());

        result.setSecondPart(extractExpressionPart(expression, index));
        return result;
    }

    private String extractExpressionPart(String expression, AtomicInteger index) {
        int counter = 0;
        StringBuilder part = new StringBuilder();
        do {
            char c = expression.charAt(index.get());
            if (c == OPEN_BRACKET)
                counter++;
            if (c == CLOSE_BRACKET)
                counter--;
            part.append(c);
            index.incrementAndGet();
        } while (counter != 0);
        return part.toString();
    }

    private BiPredicate<LENode, LENode> convertToOperator(Character sign) throws InvalidOperatorException {
        return switch (sign) {
            case CONJUNCTION -> (a, b) -> a.calcValue(null) && b.calcValue(null);
            case DISJUNCTION -> (a, b) -> a.calcValue(null) || b.calcValue(null);
            case EQUALITY -> (a, b) -> a.calcValue(null) == b.calcValue(null);
            case NEGATION -> (a, b) -> !b.calcValue(null);
            case IMPLICIT -> (a, b) -> (!a.calcValue(null)) || b.calcValue(null);
            default -> throw new InvalidOperatorException(sign);
        };
    }
}
