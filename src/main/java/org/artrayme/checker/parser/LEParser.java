package org.artrayme.checker.parser;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.exceptions.invalidOperatorException;
import org.artrayme.checker.tree.LENode;
import org.artrayme.checker.tree.LETree;

import java.util.Optional;
import java.util.function.BiPredicate;

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

    public static LETree valueOf(String expression) throws InvalidBracketsException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, invalidOperatorException {
        if (expression.length() < 4)
            throw new InvalidSyntaxCharacterException(' ');
        LEParser LEParser = new LEParser();
        if (!checkBrackets(expression)) {
            throw new InvalidBracketsException();
        }
        Optional<Character> invalidSymbol = checkSymbolsValidity(expression);
        if (invalidSymbol.isPresent()) {
            throw new InvalidSyntaxCharacterException(invalidSymbol.get());
        }
        LETree leTree = new LETree(LEParser.parseRecursive(expression));
        return leTree;
    }

    public static boolean checkBrackets(String expression) {
        if (expression.isEmpty())
            return true;
        if (expression.charAt(0) != OPEN_BRACKET || expression.charAt(expression.length() - 1) != CLOSE_BRACKET) {
            return false;
        }

        int counter = 0;
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == OPEN_BRACKET)
                counter++;
            if (c == CLOSE_BRACKET)
                counter--;
            if (counter < 0)
                return false;
            if (counter == 0 && i < expression.length() - 1)
                return false;
        }

        return counter == 0;
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
        if (expression.isEmpty())
            return false;
        if (expression.charAt(0) < 'A' || expression.charAt(0) > 'Z')
            return false;
        //        if (expression.length() > 1) {
        //            if (expression.charAt(1) == '0') {
        //                return false;
        //            }
        //            try {
        //                Integer.parseInt(expression.substring(1));
        //            } catch (NumberFormatException e) {
        //                return false;
        //            }
        //        }
        return true;
    }

    private LENode parseRecursive(String expressionPart) throws InvalidAtomicExpressionSyntaxException, invalidOperatorException {
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

        StringBuilder leftPart = new StringBuilder();
        StringBuilder rightPart = new StringBuilder();

        LENode node = new LENode(expressionPart);
        char[] charArray = expressionPart.toCharArray();

        int index = 0;
        if (expressionPart.charAt(1) == NEGATION) {
            node.setOperator(convertToOperator(NEGATION), NEGATION);
            node.setRightChild(parseRecursive(expressionPart.substring(2, expressionPart.length() - 1)));
        } else {
            index = cutPart(1, charArray, leftPart);
            node.setOperator(convertToOperator(charArray[index]), charArray[index]);
            index = cutPart(++index, charArray, rightPart);

            node.setLeftChild(parseRecursive(leftPart.toString()));
            node.setRightChild(parseRecursive(rightPart.toString()));
        }

        return node;
    }

    private int cutPart(int index, char[] charArray, StringBuilder resultPart) {
        int counter = 0;
        do {
            char c = charArray[index];
            if (c == OPEN_BRACKET)
                counter++;
            if (c == CLOSE_BRACKET)
                counter--;
            resultPart.append(c);
            index++;
        } while (counter != 0);

        return index;
    }


    private BiPredicate<LENode, LENode> convertToOperator(Character sign) throws invalidOperatorException {
        return switch (sign) {
            case CONJUNCTION -> (a, b) -> a.calcValue(null) && b.calcValue(null);
            case DISJUNCTION -> (a, b) -> a.calcValue(null) || b.calcValue(null);
            case EQUALITY -> (a, b) -> a.calcValue(null) == b.calcValue(null);
            case NEGATION -> (a, b) -> !b.calcValue(null);
            case IMPLICIT -> (a, b) -> (!a.calcValue(null)) || b.calcValue(null);
            default -> throw new invalidOperatorException(sign);
        };
    }
}
