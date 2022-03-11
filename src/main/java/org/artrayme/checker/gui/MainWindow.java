package org.artrayme.checker.gui;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.Constants;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LETree;
import org.artrayme.checker.util.PcnfUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;

////////////////////////////////////////////
//Лабораторная работа №1-2 по дисциплине ЛОИС
//Выполнено студентом группы 921703
//Василевский Артемий Дмитриевич
//Использованные источники:
//1) Справочно система по дисциплине ЛОИС

public class MainWindow extends JFrame {

    public static final String GUI_CONJUNCTION = "/\\";
    public static final String GUI_DISJUNCTION = "\\/";
    public static final String GUI_NEGATION = "!";
    public static final String GUI_EQUALITY = "~";
    public static final String GUI_IMPLICATION = "->";
    private final JTextField expressionField = new JTextField();
    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final JPanel instrumentationPanel = new JPanel(new GridLayout(1, 2));

    private final JPanel buttonsPanel = new JPanel(new GridLayout(3, 2));
    private final JButton conjunctionButton = new JButton("∧");
    private final JButton disjunctionButton = new JButton("∨");
    private final JButton negationButton = new JButton("¬");
    private final JButton equalityButton = new JButton("~");
    private final JButton implicationButton = new JButton("→");
    private final JButton createPcnfButton = new JButton("Create");

    private final JPanel informationPanel = new JPanel(new GridLayout(5, 2));
    private final JLabel bracketsValidityLabel = new JLabel("Brackets: ");
    private final JLabel bracketsValidityStatusLabel = new JLabel("");
    private final JLabel syntaxValidityLabel = new JLabel("Syntax: ");
    private final JLabel syntaxValidityStatusLabel = new JLabel("");
    private final JLabel atomicSyntaxValidityLabel = new JLabel("Atomic: ");
    private final JLabel atomicSyntaxValidityStatusLabel = new JLabel("");
    private final JLabel operatorSyntaxValidityLabel = new JLabel("Operators: ");
    private final JLabel operatorSyntaxValidityStatusLabel = new JLabel("");
    private final JLabel sknfValidityLabel = new JLabel("PCNF: ");
    private final JLabel sknfValidityStatusLabel = new JLabel("");
    private final Font mainFont;

    private int offset = 0;


    public MainWindow() throws HeadlessException {
        mainFont = new Font("SansSerif", Font.BOLD, 20);
        expressionField.setFont(mainFont);
        expressionField.getDocument().addDocumentListener(new ExpressionFieldListener());
        addButtons();
        addLabels();
        instrumentationPanel.add(buttonsPanel, 0);
        instrumentationPanel.add(informationPanel, 1);
        mainPanel.add(expressionField, BorderLayout.NORTH);
        mainPanel.add(instrumentationPanel, BorderLayout.CENTER);

        this.add(mainPanel);
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        MainWindow window = new MainWindow();

        window.setBounds(400, 400, 900, 400);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButtons() {
        conjunctionButton.addActionListener(e -> {
            setSymbolToCurrentPosition(GUI_CONJUNCTION);
            offset = 2;
        });
        conjunctionButton.setFont(mainFont);
        disjunctionButton.addActionListener(e -> {
            setSymbolToCurrentPosition(GUI_DISJUNCTION);
            offset = 2;
        });
        disjunctionButton.setFont(mainFont);

        negationButton.addActionListener(e -> {
            setSymbolToCurrentPosition(GUI_NEGATION);
            offset = 1;
        });
        negationButton.setFont(mainFont);

        equalityButton.addActionListener(e -> {
            setSymbolToCurrentPosition(GUI_EQUALITY);
            offset = 1;
        });
        equalityButton.setFont(mainFont);

        implicationButton.addActionListener(e -> {
            setSymbolToCurrentPosition(GUI_IMPLICATION);
            offset = 2;
        });
        implicationButton.setFont(mainFont);

        createPcnfButton.addActionListener(e -> {
            try {
                JOptionPane.showMessageDialog(this, replaceOperatorsToGui(
                        PcnfUtil.createPcnf(LEParser.valueOf(replaceOperatorsToEngine(expressionField.getText()))).getRoot().getExpression()));
            } catch (InvalidOperatorException | InvalidSyntaxCharacterException | InvalidAtomicExpressionSyntaxException | InvalidBracketsException ex) {
            }
        });
        createPcnfButton.setEnabled(false);
        createPcnfButton.setFont(mainFont);

        buttonsPanel.add(conjunctionButton);
        buttonsPanel.add(disjunctionButton);
        buttonsPanel.add(negationButton);
        buttonsPanel.add(equalityButton);
        buttonsPanel.add(implicationButton);
        buttonsPanel.add(createPcnfButton);
    }

    private void setSymbolToCurrentPosition(String symbol) {
        StringBuilder text = new StringBuilder(expressionField.getText());
        int startPosition = expressionField.getCaretPosition();
        text.insert(startPosition, symbol);
        expressionField.setText(text.toString());
        expressionField.setCaretPosition(startPosition + offset);
    }

    private void addLabels() {
        bracketsValidityLabel.setFont(mainFont);
        bracketsValidityStatusLabel.setFont(mainFont);
        syntaxValidityLabel.setFont(mainFont);
        syntaxValidityStatusLabel.setFont(mainFont);
        atomicSyntaxValidityLabel.setFont(mainFont);
        atomicSyntaxValidityStatusLabel.setFont(mainFont);
        operatorSyntaxValidityLabel.setFont(mainFont);
        operatorSyntaxValidityStatusLabel.setFont(mainFont);
        sknfValidityLabel.setFont(mainFont);
        sknfValidityStatusLabel.setFont(mainFont);

        informationPanel.add(bracketsValidityLabel);
        informationPanel.add(bracketsValidityStatusLabel);
        informationPanel.add(syntaxValidityLabel);
        informationPanel.add(syntaxValidityStatusLabel);
        informationPanel.add(atomicSyntaxValidityLabel);
        informationPanel.add(atomicSyntaxValidityStatusLabel);
        informationPanel.add(operatorSyntaxValidityLabel);
        informationPanel.add(operatorSyntaxValidityStatusLabel);
        informationPanel.add(sknfValidityLabel);
        informationPanel.add(sknfValidityStatusLabel);

    }

    private String replaceOperatorsToEngine(String expression) {
        return expression.replace(MainWindow.GUI_CONJUNCTION, String.valueOf(Constants.CONJUNCTION))
                .replace(MainWindow.GUI_DISJUNCTION, String.valueOf(Constants.DISJUNCTION))
                .replace(MainWindow.GUI_IMPLICATION, String.valueOf(Constants.IMPLICIT));
    }

    private String replaceOperatorsToGui(String expression) {
        return expression.replace(String.valueOf(Constants.CONJUNCTION), MainWindow.GUI_CONJUNCTION)
                .replace(String.valueOf(Constants.DISJUNCTION), MainWindow.GUI_DISJUNCTION)
                .replace(String.valueOf(Constants.IMPLICIT), MainWindow.GUI_IMPLICATION);
    }

    private class ExpressionFieldListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent documentEvent) {
            checkExpression();
        }

        @Override
        public void removeUpdate(DocumentEvent documentEvent) {
            checkExpression();
        }

        @Override
        public void changedUpdate(DocumentEvent documentEvent) {
            checkExpression();
        }

        private void clearAllStatuses() {
            bracketsValidityStatusLabel.setText("");
            syntaxValidityStatusLabel.setText("");
            atomicSyntaxValidityStatusLabel.setText("");
            operatorSyntaxValidityStatusLabel.setText("");
            sknfValidityStatusLabel.setText("");
            createPcnfButton.setEnabled(false);

        }

        private void setAllLEValidityStatus(String status) {
            bracketsValidityStatusLabel.setText(status);
            syntaxValidityStatusLabel.setText(status);
            atomicSyntaxValidityStatusLabel.setText(status);
            operatorSyntaxValidityStatusLabel.setText(status);
            createPcnfButton.setEnabled(true);

        }

        private void checkExpression() {
            clearAllStatuses();
            if (expressionField.getText().isEmpty())
                return;
            try {
                LETree expressionTree = LEParser.valueOf(replaceOperatorsToEngine(expressionField.getText()));
                setAllLEValidityStatus("Yes");

                if (PcnfUtil.isPcnf(expressionTree)) {
                    sknfValidityStatusLabel.setText("Yes");
                } else {
                    sknfValidityStatusLabel.setText("No");
                }

            } catch (InvalidBracketsException e) {
                bracketsValidityStatusLabel.setText("No");
            } catch (InvalidSyntaxCharacterException e) {
                bracketsValidityStatusLabel.setText("Yes");
                syntaxValidityStatusLabel.setText("No -- " + e.getInvalidCharacter());
            } catch (InvalidAtomicExpressionSyntaxException e) {
                bracketsValidityStatusLabel.setText("Yes");
                syntaxValidityStatusLabel.setText("Yes");
                atomicSyntaxValidityStatusLabel.setText("No -- " + e.getExpression());
            } catch (InvalidOperatorException e) {
                bracketsValidityStatusLabel.setText("Yes");
                syntaxValidityStatusLabel.setText("Yes");
                atomicSyntaxValidityStatusLabel.setText("Yes");
                operatorSyntaxValidityStatusLabel.setText("No -- " + e.getInvalidOperator());
            }
        }
    }
}
