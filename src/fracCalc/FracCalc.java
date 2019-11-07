/*
Sebastian Law
2019.11.6
 */

package fracCalc;

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
    
    public static String produceAnswer(String input) {
        if (!isValidOperation(input)) return "ERROR: Invalid input";
    
        while (input.contains(")")) { // while parentheses exist
            input = doParentheses(input);
        }
        
        return (evaluateExpression(input));
    }
    
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
        MixedNumber[] operands = extractOperands(expression);
        char[] operators = extractOperators(expression);
        // run over priority in order of operations
        for (char[] priority : priorityLevels) {
            for (int j = 0; j < operators.length; j++) {
                if (charInArray(operators[j], priority)) {
                    operands = doOperation(operands, operators, j);
                    operators = removedIndex(operators, j--);
                }
            }
        }
        return operands[0].toString();
    }
    
    /**
     * Does one operation. You must manually decrement the operators array
     * @param operands the operands array
     * @param operators the operators array
     * @param index the index of the operator array to evaluate
     * @return the updated operand array
     */
    private static MixedNumber[] doOperation(MixedNumber[] operands, char[] operators, int index){
        char op = operators[index];
        MixedNumber secondNum = operands[index + 1];
        if (op == '*'){
            operands[index].multiplyBy(secondNum);
        } else if (op == '/'){
            operands[index].divideBy(secondNum);
        } else if (op == '+'){
            operands[index].addedBy(secondNum);
        } else { // subtraction
            operands[index].subtractedBy(secondNum);
        }
        return removedIndex(operands, index + 1);
    }
    
    // returns the operands of a string expression
    private static MixedNumber[] extractOperands(String expression){
        String[] expressionTerms = expression.split(" ");
        MixedNumber[] operands = new MixedNumber[expressionTerms.length / 2 + 1];
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 0){
                operands[i / 2] = new MixedNumber(expressionTerms[i]);
            }
        }
        return operands;
    }
    
    // returns the operators of a string expression
    private static char[] extractOperators(String expression){
        String[] expressionTerms = expression.split(" ");
        char[] operators = new char[expressionTerms.length / 2];
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 1){
                operators[i / 2] = (expressionTerms[i]).charAt(0);
            }
        }
        return operators;
    }
    
    // if a string contains ANY character in a character array
    private static boolean charInArray(char c, char[] characters){
        for (char character : characters){
            if (character == c) return true;
        }
        return false;
    }
    
    // strips off parentheses from a string
    static String stripParentheses(String string) {
        while (string.length() > 0 && string.charAt(0) == '('){
            string = string.substring(1);
        }
        while (string.length() > 0 && string.charAt(string.length() - 1) == ')') {
            string = string.substring(0, string.length() - 1);
        }
        
        return string;
    }
    
    // removes an index from a operand array
    private static MixedNumber[] removedIndex(MixedNumber[] fractionArray, int index) {
        MixedNumber[] shifted = new MixedNumber[fractionArray.length - 1];
        for (int i = 0, j = 0; i < fractionArray.length; i++) {
            if (i != index) shifted[j++] = fractionArray[i];
        }
        return shifted;
    }
    
    // removes an index from a operator array
    private static char[] removedIndex(char[] operatorArray, int index){
        char[] shifted = new char[operatorArray.length - 1];
        for (int i = 0, j = 0; i < operatorArray.length; i++) {
            if (i != index) shifted[j++] = operatorArray[i];
        }
        return shifted;
    }
    
    // Checks if the string input is a valid FracCalc operation
    private static boolean isValidOperation(String input) {
        String[] inputTerms = input.split(" ");
        // The terms must be in the format {operand operator operand} (odd number of terms)
        if (inputTerms.length % 2 == 0 || input.length() < 5) return false;
        // make sure the number of opening and closing parenthesis are equal
        if (!areParenthesesValid(input)) return false;
        
        // loop over terms
        for (int termNumber = 0; termNumber < inputTerms.length; termNumber++) {
            String term = inputTerms[termNumber];
            if (termNumber % 2 == 0){ // OPERAND TERMS
                if (!isValidOperand(term)) return false;
            } else { // OPERATOR TERMS
                if (!isValidOperator(term)) return false;
            }
        }
        // this line is reached only if all the terms are in a valid format
        // the following lines check for division by zero
        MixedNumber[] operands = extractOperands(input);
        char[] operators = extractOperators(input);
        for (MixedNumber operand : operands) if (operand.getDenominator() == 0) return false;
        for (int i = 0; i < operators.length; i++) {
            if (operators[i] == '/' && operands[i + 1].getNumerator() == 0) return false;
        }
        
        return true; // if nothing raised an error, return true
    }
    
    // Separated parentheses error handling for isValidOperation
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
    
    // Checks if a string is only integers
    private static boolean isOnlyIntegers(String string){
        if (string.length() == 0) return false; // cannot start as an empty string
        char[] integers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (char c : string.toCharArray()){
            if (!charInArray(c, integers)) return false;
        }
        return true;
    }
    
    // Separated operand error handling for isValidOperation
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
    
    // Separated operator error handling for isValidOperation
    private static boolean isValidOperator(String operator){
        if (operator.length() > 1) return false;
        char[] validOperators = {'*', '/', '+', '-'};
        return charInArray(operator.charAt(0), validOperators);
    }
}
