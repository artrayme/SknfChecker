package org.artrayme.checker.util;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.Constants;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LENode;
import org.artrayme.checker.tree.LETree;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class PcnfUtil {
    private PcnfUtil() {
    }

    public static LETree createPcnf(LETree expression) throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        if (!isPcnfSyntaxValid(expression.getRoot().getExpression()))
            throw new InvalidSyntaxCharacterException("This syntax is invalid for PCMF", ' ');
        List<LENode> leaves = new ArrayList<>();
        recursivelyIteration(expression.getRoot(), leaves);
        leaves = leaves.stream().distinct().toList();
        String resultExpression = generatePcnfExpressionByTruthTable(leaves, expression.getRoot());
        if (resultExpression.isEmpty())
            return new LETree(new LENode(""));
        return LEParser.valueOf(resultExpression);
    }

    public static boolean isPcnf(LETree expression) {
        if (!isPcnfSyntaxValid(expression.getRoot().getExpression()))
            return false;
        List<LENode> conjunctionParts = getConjunctionParts(expression.getRoot());
        List<LENode> leaves = new ArrayList<>();
        recursivelyIteration(expression.getRoot(), leaves);
        leaves = leaves.stream().distinct().toList();
        for (LENode part : conjunctionParts) {
            List<LENode> partLeaves = new ArrayList<>();
            recursivelyIteration(part, partLeaves);
            if (leaves.size() != partLeaves.size())
                return false;
        }
        List<List<LENode>> disjunctionParts = new ArrayList<>();
        for (int i = 0; i < conjunctionParts.size(); i++) {
            List<LENode> currentDisjunctionPart = getDisjunctionParts(conjunctionParts.get(i));
            for (LENode node : currentDisjunctionPart) {
                if (!checkIsDisjunctionFlat(node))
                    return false;
            }

            disjunctionParts.add(currentDisjunctionPart);
        }
        return checkDisjunctionDuplicates(disjunctionParts);
    }

    private static boolean checkIsDisjunctionFlat(LENode node) {
        if (node.getLeftChild() == null) {
            if (node.getRightChild() == null) {
                return true;
            } else {
                return checkIsDisjunctionFlat(node.getRightChild());
            }
        }
        if (node.getLeftChild().getOperatorSymbol() == Constants.NEGATION
                && node.getLeftChild().getRightChild().getOperatorSymbol() != '\u0000')
            return true;
        if (node.getRightChild().getOperatorSymbol() == Constants.NEGATION
                && node.getRightChild().getRightChild().getOperatorSymbol() != '\u0000')
            return true;
        return false;
    }

    private static boolean checkDisjunctionDuplicates(List<List<LENode>> disjunctions) {
        var uniqDisjunctions = disjunctions
                .stream()
                .map(e -> e.stream()
                        .map(node -> String.valueOf(node.getExpression()))
                        .sorted()
                        .reduce((a, b) -> a + b))
                .filter(Optional::isPresent)
                .distinct()
                .toList();
        return uniqDisjunctions.size() == disjunctions.size();
    }

    private static List<LENode> getConjunctionParts(LENode expression) {
        List<LENode> result = new ArrayList<>();
        recAddLeavesForThisOperatorToList(result, expression, Constants.CONJUNCTION);
        return result;
    }

    private static List<LENode> getDisjunctionParts(LENode expression) {
        List<LENode> result = new ArrayList<>();
        recAddLeavesForThisOperatorToList(result, expression, Constants.DISJUNCTION);
        return result;
    }

    private static void recAddLeavesForThisOperatorToList(List<LENode> nodes, LENode currentNode, Character operator) {
        if (currentNode.getOperatorSymbol() != operator) {
            nodes.add(currentNode);
            return;
        }
        if (currentNode.getLeftChild() != null) {
            if (currentNode.getLeftChild().getOperatorSymbol() == operator) {
                recAddLeavesForThisOperatorToList(nodes, currentNode.getLeftChild(), operator);
                return;
            } else {
                nodes.add(currentNode.getLeftChild());
            }
        }
        if (currentNode.getRightChild() != null) {
            if (currentNode.getRightChild().getOperatorSymbol() == operator) {
                recAddLeavesForThisOperatorToList(nodes, currentNode.getRightChild(), operator);
            } else {
                nodes.add(currentNode.getRightChild());
            }
        }
    }

    private static void recursivelyIteration(LENode node, Collection<LENode> leaves) {
        if (node.getLeftChild() == null
                && node.getRightChild() == null
                && !node.getExpression().isEmpty()
                && node.getExpression().charAt(0) != Constants.TRUE
                && node.getExpression().charAt(0) != Constants.FALSE)
            leaves.add(node);

        if (node.getLeftChild() != null)
            recursivelyIteration(node.getLeftChild(), leaves);
        if (node.getRightChild() != null)
            recursivelyIteration(node.getRightChild(), leaves);
    }

    private static String generatePcnfExpressionByTruthTable(List<LENode> leaves, LENode root) {
        List<String> listOfConjunctionParts = new ArrayList<>();
        StatesGenerator generator = new StatesGenerator();
        List<Boolean> states = generator.getStates();
        for (int i = 0; i < Math.pow(2, leaves.size()); i++) {
            Map<String, Boolean> values = new HashMap<>();
            for (int j = 0; j < leaves.size(); j++) {
                values.put(leaves.get(j).getExpression(), states.get(j));
            }
            boolean expressionResult = root.calcValue(values);
            if (!expressionResult) {
                List<Map.Entry<String, Boolean>> someList = new ArrayList<>();
                values.forEach((k, v) -> {
                    if (k.charAt(0) != Constants.TRUE && k.charAt(0) != Constants.FALSE)
                        someList.add(new AbstractMap.SimpleImmutableEntry<>(k, v));
                });
                String part = recursivelyDisjunctionBracketsEncapsulation(someList);
                listOfConjunctionParts.add(part);
            }
            states = generator.incrementAndGet();
        }
        return recursivelyConjunctionBracketsEncapsulation(listOfConjunctionParts);
    }

    private static String recursivelyConjunctionBracketsEncapsulation(List<String> part) {
        if (part.size() > 2) {
            return "("
                    + recursivelyConjunctionBracketsEncapsulation(part.subList(0, 1))
                    + Constants.CONJUNCTION
                    + recursivelyConjunctionBracketsEncapsulation(part.subList(1, part.size()))
                    + ")";
        } else if (part.size() == 2) {
            return "("
                    + part.get(0)
                    + Constants.CONJUNCTION
                    + part.get(1)
                    + ")";
        } else if (part.size() == 1) {
            return part.get(0);
        }
        return "";
    }

    private static String recursivelyDisjunctionBracketsEncapsulation(List<Map.Entry<String, Boolean>> values) {
        if (values.size() > 2) {
            return "("
                    + recursivelyDisjunctionBracketsEncapsulation(values.subList(0, 1))
                    + Constants.DISJUNCTION
                    + recursivelyDisjunctionBracketsEncapsulation(values.subList(1, values.size()))
                    + ")";
        } else if (values.size() == 2) {
            return "("
                    + getAtomicPart(values.get(0).getKey(), values.get(0).getValue())
                    + Constants.DISJUNCTION
                    + getAtomicPart(values.get(1).getKey(), values.get(1).getValue())
                    + ")";
        } else if (values.size() == 1) {
            return getAtomicPart(values.get(0).getKey(), values.get(0).getValue());
        }
        throw new RuntimeException("recursivelyBracketsEncapsulation -- something wrong");
    }

    private static String getAtomicPart(String expression, Boolean value) {
        if (!value)
            return expression;
        else
            return "(" + Constants.NEGATION + expression + ")";
    }

    private static boolean isPcnfSyntaxValid(String expression) {
        return !(expression.contains(String.valueOf(Constants.TRUE)) || expression.contains(String.valueOf(Constants.FALSE)));
    }

}
