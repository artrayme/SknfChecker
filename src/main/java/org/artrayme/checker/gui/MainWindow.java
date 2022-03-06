package org.artrayme.checker.gui;

import org.artrayme.checker.exceptions.InvalidAtomicExpressionSyntaxException;
import org.artrayme.checker.exceptions.InvalidBracketsException;
import org.artrayme.checker.exceptions.InvalidOperatorException;
import org.artrayme.checker.exceptions.InvalidSyntaxCharacterException;
import org.artrayme.checker.parser.LEParser;
import org.artrayme.checker.tree.LETree;
import org.artrayme.checker.util.SknfUtil;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

    private final JTextField expressionField = new JTextField();
    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final JPanel instrumentationPanel = new JPanel(new GridLayout(1, 2));

    private final JPanel buttonsPanel = new JPanel(new GridLayout(3, 2));
    private final JButton conjunctionButton = new JButton("∧");
    private final JButton disjunctionButton = new JButton("∨");
    private final JButton negationButton = new JButton("¬");
    private final JButton equalityButton = new JButton("~");
    private final JButton implicationButton = new JButton("→");

    private final JPanel informationPanel = new JPanel(new GridLayout(5, 2));
    private final JLabel bracketsValidityLabel = new JLabel("Are brackets valid: ");
    private final JLabel bracketsValidityStatusLabel = new JLabel("");
    private final JLabel syntaxValidityLabel = new JLabel("Is syntax valid: ");
    private final JLabel syntaxValidityStatusLabel = new JLabel("");
    private final JLabel atomicSyntaxValidityLabel = new JLabel("Is syntax of Atomic expressions valid: ");
    private final JLabel atomicSyntaxValidityStatusLabel = new JLabel("");
    private final JLabel operatorSyntaxValidityLabel = new JLabel("Are operators valid: ");
    private final JLabel operatorSyntaxValidityStatusLabel = new JLabel("");
    private final JLabel sknfValidityLabel = new JLabel("Is SKNF valid: ");
    private final JLabel sknfValidityStatusLabel = new JLabel("");


    public MainWindow() throws HeadlessException {
        Font font1 = new Font("SansSerif", Font.BOLD, 20);
        expressionField.setFont(font1);
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
        MainWindow window = new MainWindow();
        window.setBounds(700, 400, 600, 400);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButtons() {
        conjunctionButton.addActionListener(e -> {
            setSymbolToCurrentPosition('∧');
        });
        disjunctionButton.addActionListener(e -> {
            setSymbolToCurrentPosition('∨');
        });
        negationButton.addActionListener(e -> {
            setSymbolToCurrentPosition('¬');
        });
        equalityButton.addActionListener(e -> {
            setSymbolToCurrentPosition('~');
        });
        implicationButton.addActionListener(e -> {
            setSymbolToCurrentPosition('→');
        });

        buttonsPanel.add(conjunctionButton);
        buttonsPanel.add(disjunctionButton);
        buttonsPanel.add(negationButton);
        //        buttonsPanel.add(equalityButton);
        //        buttonsPanel.add(implicationButton);
    }

    private void setSymbolToCurrentPosition(char symbol) {
        StringBuilder text = new StringBuilder(expressionField.getText());
        int startPosition = expressionField.getCaretPosition();
        text.insert(startPosition, symbol);
        expressionField.setText(text.toString());
        expressionField.setCaretPosition(startPosition + 1);
    }

    private void addLabels() {
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
        }

        private void setAllLEValidityStatus(String status) {
            bracketsValidityStatusLabel.setText(status);
            syntaxValidityStatusLabel.setText(status);
            atomicSyntaxValidityStatusLabel.setText(status);
            operatorSyntaxValidityStatusLabel.setText(status);
        }

        private void checkExpression() {
            clearAllStatuses();
            if (expressionField.getText().isEmpty())
                return;
            try {
                LETree expressionTree = LEParser.valueOf(expressionField.getText());
                setAllLEValidityStatus("Yes");
                if (SknfUtil.isSknf(expressionTree)) {
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
