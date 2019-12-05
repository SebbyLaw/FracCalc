/*
Sebastian Law
2019.11.19
 */

package fracCalc;

import java.util.ArrayList;
import java.util.Scanner;

public class FracCalc {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input;
        boolean keepGoing;

        do {
            System.out.print("Enter Mathematical Expression (\'quit\' to quit): ");
            input = scanner.nextLine();
            keepGoing = !input.equalsIgnoreCase("quit");

            if (keepGoing) System.out.println(produceAnswer(input));

        } while (keepGoing);
        scanner.close();
    }
    
    /**
     * Produces an answer based on an expression input
     * @param input the expression
     * @return the evaluation of the expression
     */
    public static String produceAnswer(String input) {
        String errorMessage = testValidOperation(input);
        if (errorMessage.toLowerCase().contains("error")) return errorMessage;
    
        while (input.contains(")")) { // while parentheses exist
            input = doParentheses(input);
        }
        
        return (evaluateExpression(input));
    }
    
    /**
     * Evaluates all the parentheses in an expression
     * @param expression the expression to evaluate
     * @return the evaluated expression (may not be fully evaluated)
     */
    private static String doParentheses(String expression){
        boolean indexesFound = false;
        int openingIndex = 0;
        int closingIndex = expression.length();
        
        // a for loop with break would be better, but apparently breaking is evil
        int i = 0;
        while (!indexesFound) {
            char character = expression.charAt(i);
            if (character == '(') openingIndex = i;
            else if (character == ')') {
                closingIndex = i;
                indexesFound = true;
            }
            i++;
        }
        
        String nested = evaluateExpression(expression.substring(openingIndex, closingIndex));
        return expression.substring(0, openingIndex) + nested + expression.substring(closingIndex + 1);
    }
    
    /**
     * Evaluates an expression ignoring parentheses
     * @param expression the expression to be evaluated
     * @return the fraction in mixed number string form
     */
    private static String evaluateExpression(String expression) {
        char[][] priorityLevels = {{'*', '/'}, {'+', '-'}};
        ArrayList<Fraction> operands = extractOperands(expression);
        ArrayList<Character> operators = extractOperators(expression);
        // iterate over priority in order of operations
        for (char[] priority : priorityLevels) {
            for (int j = 0; j < operators.size(); j++) {
                if (charInArray(operators.get(j), priority)) {
                    doOperation(operands, operators, j--);
                }
            }
        }
        return operands.get(0).toString();
    }
    
    /**
     * Does one operation. You must manually decrement the operators array
     * @param operands the operands ArrayList
     * @param operators the operators ArrayList
     * @param index the index of the operator ArrayList to evaluate
     */
    private static void doOperation(ArrayList<Fraction> operands, ArrayList<Character> operators, int index){
        char op = operators.get(index);
        Fraction secondNum = operands.get(index + 1);
        if (op == '*'){
            operands.get(index).multiplyBy(secondNum);
        } else if (op == '/'){
            operands.get(index).divideBy(secondNum);
        } else if (op == '+'){
            operands.get(index).addedBy(secondNum);
        } else { // subtraction
            operands.get(index).subtractedBy(secondNum);
        }
        operands.remove(index + 1);
        operators.remove(index);
    }
    
    /**
     * returns the operands of a string expression
     * @param expression the expression string
     * @return an ArrayList of MixedNumbers within the expression representing operands
     */
    private static ArrayList<Fraction> extractOperands(String expression){
        String[] expressionTerms = expression.split(" ");
        ArrayList<Fraction> operands = new ArrayList<Fraction>();
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 0){
                operands.add(new Fraction(expressionTerms[i]));
            }
        }
        return operands;
    }
    
    /**
     * returns the operators of a string expression
     * @param expression the expression string
     * @return an ArrayList of chars within the expression representing operators
     */
    private static ArrayList<Character> extractOperators(String expression){
        String[] expressionTerms = expression.split(" ");
        ArrayList<Character> operators = new ArrayList<Character>();
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 1){
                operators.add((expressionTerms[i]).charAt(0));
            }
        }
        return operators;
    }
    
    /**
     * Searches a character array and returns true if a character is found in it
     * @param c the character to parse
     * @param characters the character array to search
     * @return True if c is found in the array characters
     */
    private static boolean charInArray(char c, char[] characters){
        for (char character : characters){
            if (character == c) return true;
        }
        return false;
    }
    
    /**
     * Strips the parentheses from the front and back of a string
     * @param string the string to strip
     * @return the stripped string
     */
    static String stripParentheses(String string) {
        while (string.length() > 0 && string.charAt(0) == '('){
            string = string.substring(1);
        }
        while (string.length() > 0 && string.charAt(string.length() - 1) == ')') {
            string = string.substring(0, string.length() - 1);
        }
        
        return string;
    }
    
    /**
     * Checks whether a string expression is in valid FracCalc format
     * @param input the expression to parse
     * @return The error if there is one, else an empty string
     */
    private static String testValidOperation(String input) {
        String[] inputTerms = input.split(" ");
        // The terms must be in the format {operand operator operand} (odd number of terms)
        if (inputTerms.length % 2 == 0 || input.length() < 5) return "Error: Input is in an invalid format";
        // make sure the number of opening and closing parenthesis are equal
        if (!areParenthesesValid(input)) return "Error: Parentheses are in an invalid format";
        ArrayList<Fraction> operands = new ArrayList<Fraction>();
        ArrayList<Character> operators = new ArrayList<Character>();
        
        // loop over terms
        for (int termNumber = 0; termNumber < inputTerms.length; termNumber++) {
            String term = inputTerms[termNumber];
            if (termNumber % 2 == 0){ // OPERAND TERMS
                if (!isValidOperand(term)) return String.format("Error: Invalid Operand %s", term);
                operands.add(new Fraction(term));
            } else { // OPERATOR TERMS
                if (!isValidOperator(term)) return String.format("Error: Invalid Operator %s", term);
                operators.add(term.charAt(0));
            }
        }
        // this line is reached only if all the terms are in a valid format
        // the following lines check for division by zero
        for (int i = 0; i < operands.size(); i++) {
            Fraction operand = operands.get(i);
            if (operand.getDenominator() == 0) {
                return String.format("Error: denominator is zero for operand %s", inputTerms[i * 2]);
            }
        }
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '/' && operands.get(i + 1).getNumerator() == 0) return "Error: division by zero";
        }
        
        return ""; // if nothing raised an error, return empty string
    }
    
    /**
     * Checks whether a string expression has valid parentheses format
     * @param expression the expression to parse
     * @return True if the parentheses are in a valid format
     */
    private static boolean areParenthesesValid(String expression){
        boolean termHasParen = false;
        int open = 0;
        
        for (char character : expression.toCharArray()){
            if (character == ' ') termHasParen = false;
            // reset term
            
            if (character == '(') {
                termHasParen = true;
                open++; // increment the number of unclosed pairs
            }
            
            if (character == ')'){
                if (termHasParen) return false;
                // parentheses cannot be around a single term
                if (open > 0) {
                    open--; // decrement the number of unclosed pairs
                } else return false;
            }
        }
        return open == 0;
    }
    
    /**
     * Checks if a string is entirely made up of integers
     * @param string the string to parse
     * @return True if the string only contains integers
     */
    private static boolean isOnlyIntegers(String string){
        if (string.length() == 0) return false; // cannot start as an empty string
        for (char c : string.toCharArray()){
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
    
    /**
     * Checks if an operand string is in a valid format
     * @param operand the operand string to parse
     * @return True if the operand is in a valid format
     */
    private static boolean isValidOperand(String operand){
        // remove parentheses and negative sign
        operand = stripParentheses(operand);
        if (operand.length() == 0) return false;
        if (operand.charAt(0) == '-') operand = operand.substring(1);
    
        String[] wholePart = operand.split("_");
        String[] fractionPart = wholePart[wholePart.length - 1].split("/");
    
        if (!operand.contains("_")) { // if not mixed number
            if (fractionPart.length == 2){ // if fraction only
                for (String num : fractionPart) if (!isOnlyIntegers(num)) return false;
            } else { // the term must be a whole number
                return isOnlyIntegers(operand);
            }
        } else { // if mixed number
            // whole number is integers
            if (!isOnlyIntegers(operand.substring(0, operand.indexOf('_')))) return false;
            // fraction parts are integers
            if (fractionPart.length == 2) { // if fraction only
                for (String num : fractionPart) if (!isOnlyIntegers(num)) return false;
            } else return false; // fraction must be length of 2
        }
        return true;
    }
    
    /**
     * Checks if an operator string is in a valid format
     * @param operator the operator string to parse
     * @return True if the operator is in a valid format
     */
    private static boolean isValidOperator(String operator){
        if (operator.length() > 1) return false;
        char[] validOperators = {'*', '/', '+', '-'};
        return charInArray(operator.charAt(0), validOperators);
    }
}
