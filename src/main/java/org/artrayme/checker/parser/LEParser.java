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

import static org.artrayme.checker.parser.Constants.CLOSE_BRACKET;
import static org.artrayme.checker.parser.Constants.CONJUNCTION;
import static org.artrayme.checker.parser.Constants.DISJUNCTION;
import static org.artrayme.checker.parser.Constants.EQUALITY;
import static org.artrayme.checker.parser.Constants.IMPLICIT;
import static org.artrayme.checker.parser.Constants.NEGATION;
import static org.artrayme.checker.parser.Constants.OPEN_BRACKET;

public class LEParser {


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
                    && !isOperatorSymbol(c)
                    && c != OPEN_BRACKET
                    && c != CLOSE_BRACKET
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

    private static boolean isOperatorSymbol(Character symbol) {
        return symbol == CONJUNCTION
                || symbol == DISJUNCTION
                || symbol == NEGATION
                || symbol == EQUALITY
                || symbol == IMPLICIT;
    }

    private LENode parseRecursive(String expressionPart) throws InvalidAtomicExpressionSyntaxException, InvalidOperatorException, InvalidBracketsException {
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
        String operator = getPartByRule(index, expressionPart, e -> (e < 'A' || e > 'Z') && e != NEGATION);
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

    private void deepExpression(String expressionPart, LENode node) throws InvalidOperatorException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        LEParsedEntity entity = splitExpression(expressionPart.substring(1, expressionPart.length() - 1));
        if (entity.getOperator().length() != 1)
            throw new InvalidOperatorException(entity.getOperator());
        node.setOperator(convertToOperator(entity.getOperator().charAt(0)), entity.getOperator().charAt(0));
        node.setLeftChild(parseRecursive(entity.getFirstPart()));
        node.setRightChild(parseRecursive(entity.getSecondPart()));
    }

    private LEParsedEntity splitExpression(String expression) throws InvalidOperatorException, InvalidBracketsException {
        LEParsedEntity result = new LEParsedEntity();
        StringBuilder operator = new StringBuilder();
        AtomicInteger index = new AtomicInteger(0);
        result.setFirstPart(extractExpressionPart(expression, index));
        if (index.get() == expression.length())
            throw new InvalidOperatorException(expression);
        if (expression.charAt(index.get()) == NEGATION)
            throw new InvalidOperatorException(NEGATION);

        while (LEParser.isOperatorSymbol(expression.charAt(index.get()))) {
            operator.append(expression.charAt(index.get()));
            index.incrementAndGet();
            if (index.get() == expression.length())
                throw new InvalidOperatorException(expression);
        }
        result.setOperator(operator.toString());

        result.setSecondPart(extractExpressionPart(expression, index));
        return result;
    }

    private String extractExpressionPart(String expression, AtomicInteger index) throws InvalidBracketsException {
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
            if (counter < 0)
                throw new InvalidBracketsException();
        } while (counter != 0);
        return part.toString();
    }

    private BiPredicate<Boolean, Boolean> convertToOperator(Character sign) throws InvalidOperatorException {
        return switch (sign) {
            case CONJUNCTION -> (a, b) -> a && b;
            case DISJUNCTION -> (a, b) -> a || b;
            case EQUALITY -> (a, b) -> a == b;
            case NEGATION -> (a, b) -> !b;
            case IMPLICIT -> (a, b) -> (!a) || b;
            default -> throw new InvalidOperatorException(sign);
        };
    }
}
