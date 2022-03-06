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

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС
public class SknfUtil {
    private SknfUtil() {
    }

    public static LETree createSknf(LETree expression) throws InvalidOperatorException, InvalidSyntaxCharacterException, InvalidAtomicExpressionSyntaxException, InvalidBracketsException {
        List<LENode> leaves = new ArrayList<>();
        recursivelyIteration(expression.getRoot(), leaves);
        leaves = leaves.stream().distinct().toList();
        List<Map<String, Boolean>> result = new ArrayList<>();
        StatesGenerator generator = new StatesGenerator();
        List<Boolean> states = generator.getStates();
        for (int i = 0; i < Math.pow(2, leaves.size()); i++) {
            Map<String, Boolean> values = new HashMap<>();
            for (int j = 0; j < leaves.size(); j++) {
                values.put(leaves.get(j).getExpression(), states.get(j));
            }
            values.put(String.valueOf(Constants.TRUE), true);
            values.put(String.valueOf(Constants.FALSE), false);
            boolean expressionResult = expression.getRoot().calcValue(values);
            if (expressionResult)
                result.add(values);
            states = generator.incrementAndGet();
        }
        String resultExpression = generateSknfExpressionByTruthTable(result);
        if (resultExpression.isEmpty())
            return new LETree(new LENode(""));
        return LEParser.valueOf(resultExpression);
    }

    public static boolean isSknf(LETree expression) {
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
            //            for (LENode node : currentDisjunctionPart) {
            //                if (!checkIsDisjunctionFlat(node))
            //                    return false;
            //            }

            disjunctionParts.add(currentDisjunctionPart);
        }
        return checkDisjunctionDuplicates(disjunctionParts);
    }

    private static boolean checkIsDisjunctionFlat(LENode node) {
        if (node.getLeftChild() == null || node.getRightChild() == null)
            return false;
        if (node.getLeftChild().getOperatorSymbol() == Constants.NEGATION
                && node.getLeftChild().getRightChild().getOperatorSymbol() != ' ')
            return false;
        if (node.getRightChild().getOperatorSymbol() == Constants.NEGATION
                && node.getRightChild().getRightChild().getOperatorSymbol() != ' ')
            return false;
        return true;
    }

    private static boolean checkDisjunctionDuplicates(List<List<LENode>> disjunctions) {
        var uniqDisjunctions = disjunctions
                .stream()
                .map(e -> e.stream()
                        .map(node -> String.valueOf(node.getOperatorSymbol()))
                        .sorted()
                        .reduce((a, b) -> a + b)
                        .get())
                .distinct();
        return uniqDisjunctions.toList().size() == disjunctions.size();
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

    private static String generateSknfExpressionByTruthTable(List<Map<String, Boolean>> table) {
        List<String> listOfConjunctionParts = new ArrayList<>();
        for (Map<String, Boolean> truthExpression : table) {
            List<Map.Entry<String, Boolean>> someLists = new ArrayList<>();
            truthExpression.forEach((k, v) -> {
                if (k.charAt(0) != Constants.TRUE && k.charAt(0) != Constants.FALSE)
                    someLists.add(new AbstractMap.SimpleImmutableEntry<>(k, v));
            });
            String part = recursivelyDisjunctionBracketsEncapsulation(someLists);
            listOfConjunctionParts.add(part);
        }

        return recursivelyConjunctionBracketsEncapsulation(listOfConjunctionParts);
    }

    private static String recursivelyConjunctionBracketsEncapsulation(List<String> part) {
        if (part.size() > 2) {
            return "("
                    + recursivelyConjunctionBracketsEncapsulation(part.subList(0, 1))
                    + Constants.CONJUNCTION
                    + recursivelyConjunctionBracketsEncapsulation(part.subList(2, part.size()))
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

    private static String recursivelyDisjunctionBracketsEncapsulation(List<Map.Entry<String, Boolean>> stringBooleanMap) {
        if (stringBooleanMap.size() > 2) {
            return "("
                    + recursivelyDisjunctionBracketsEncapsulation(stringBooleanMap.subList(0, 1))
                    + Constants.DISJUNCTION
                    + recursivelyDisjunctionBracketsEncapsulation(stringBooleanMap.subList(1, stringBooleanMap.size()))
                    + ")";
        } else if (stringBooleanMap.size() == 2) {
            return "("
                    + getAtomicPart(stringBooleanMap.get(0).getKey(), stringBooleanMap.get(0).getValue())
                    + Constants.DISJUNCTION
                    + getAtomicPart(stringBooleanMap.get(1).getKey(), stringBooleanMap.get(1).getValue())
                    + ")";
        } else if (stringBooleanMap.size() == 1) {
            return getAtomicPart(stringBooleanMap.get(0).getKey(), stringBooleanMap.get(0).getValue());
        }
        throw new RuntimeException("recursivelyBracketsEncapsulation -- something wrong");
    }

    private static String getAtomicPart(String expression, Boolean value) {
        if (value)
            return expression;
        else
            return "(" + Constants.NEGATION + expression + ")";
    }

}
