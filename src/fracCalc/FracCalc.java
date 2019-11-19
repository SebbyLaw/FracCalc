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
    
    public static String produceAnswer(String input) {
        String errorMessage = testValidOperation(input);
        if (errorMessage.toLowerCase().contains("error")) return errorMessage;
    
    
        while (input.contains(")")) { // while parentheses exist
            input = doParenthesis(input);
        }
        
        return (evaluateExpression(input));
    }
    
    private static String doParenthesis(String expression){
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
        ArrayList<int[]> operands = extractOperands(expression);
        ArrayList<Character> operators = extractOperators(expression);
        // run over priority in order of operations
        for (char[] priority : priorityLevels) {
            for (int j = 0; j < operators.size(); j++) {
                if (charInArray(operators.get(j), priority)) {
                    doOperation(operands, operators, j--);
                }
            }
        }
        return toMixedNumberForm(operands.get(0));
    }
    
    /**
     * Does one operation. You must manually decrement the operators array
     * @param operands the operands ArrayList
     * @param operators the operators ArrayList
     * @param index the index of the operator array to evaluate
     */
    private static void doOperation(ArrayList<int[]> operands, ArrayList<Character> operators, int index){
        char op = operators.get(index);
        int[] secondOperand = operands.get(index + 1);
        int[] answer;
        if (op == '*'){
            answer = doMultiplication(operands.get(index), secondOperand);
        } else if (op == '/'){
            answer = doDivision(operands.get(index), secondOperand);
        } else if (op == '+'){
            answer = doAddition(operands.get(index), secondOperand);
        } else { // subtraction
            answer = doSubtraction(operands.get(index), secondOperand);
        }
        operands.set(index, answer);
        operands.remove(index + 1);
        operators.remove(index);
    }
    
    // multiplies one number with another in the form of fraction arrays
    private static int[] doMultiplication(int[] firstFactor, int[] secondFactor){
        int[] product = new int[2];
        
        product[0] = firstFactor[0] * secondFactor[0];
        product[1] = firstFactor[1] * secondFactor[1];
        
        return product;
    }
    
    // divides one number with another in the form of fraction arrays
    private static int[] doDivision(int[] dividend, int[] divisor){
        int[] quotient = new int[2];
        
        quotient[0] = dividend[0] * divisor[1];
        quotient[1] = divisor[0] * dividend[1];
        
        return quotient;
    }
    
    // adds two numbers in the form of fraction arrays
    private static int[] doAddition(int[] firstAddend, int[] secondAddend){
        int[] sum = new int[2];
        
        int firstNumerator = firstAddend[0] * secondAddend[1];
        int secondNumerator = secondAddend[0] * firstAddend[1];
        
        sum[0] = firstNumerator + secondNumerator;
        sum[1] = firstAddend[1] * secondAddend[1];
        
        return sum;
    }
    
    // subtracts one number from another in the form of fraction arrays
    private static int[] doSubtraction(int[] minuend, int[] subtrahend){
        subtrahend[0] *= -1;
        // addition but negative
        return doAddition(minuend, subtrahend);
    }
    
    // returns the operands of a string expression
    private static ArrayList<int[]> extractOperands(String expression){
        String[] expressionTerms = expression.split(" ");
        ArrayList<int[]> operands = new ArrayList<int[]>();
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 0){
                operands.add(toFractionForm(expressionTerms[i]));
            }
        }
        return operands;
    }
    
    // returns the operators of a string expression
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
     * Converts the MixedNumber string format to an integer fraction array.
     * @param formatted the formatted mixed number string
     * @return An integer array with length of 2. Represents a fraction as [numerator, denominator]
     */
    private static int[] toFractionForm(String formatted){
        int[] fraction = new int[2];
        
        formatted = stripParentheses(formatted);
        
        boolean isNegative = formatted.indexOf('-') == 0;
        if (isNegative) formatted = formatted.substring(1);
        
        String[] wholePart = formatted.split("_");
        String[] fractionPart = wholePart[wholePart.length - 1].split("/");
        
        // denominator is denominator (1 if whole number only)
        fraction[1] = fractionPart.length == 2 ? Integer.parseInt(fractionPart[1]) : 1;
        // numerator is numerator (same if only whole number)
        fraction[0] = Integer.parseInt(fractionPart[0]);
        // Add the whole to the numerator if it's a mixed number
        if (wholePart.length == 2) fraction[0] += Integer.parseInt(wholePart[0]) * fraction[1];
        
        if (isNegative) fraction[0] *= -1;
        return fraction;
    }
    
    /**
     * Converts the fraction array into an acceptable String format
     * @param fraction the array representing the fraction
     * @return the FracCalc output form String
     */
    private static String toMixedNumberForm(int[] fraction){
        // simplify
        int gcf = getGCF(fraction[0], fraction[1]);
        fraction[0] /= gcf;
        fraction[1] /= gcf;
    
        // negative sign if negative fraction, else empty string
        String stringFormat = (fraction[0] * fraction[1] < 0) ? "-" : "";
        int numerator = Math.abs(fraction[0]);
        int denominator = Math.abs(fraction[1]);
        
        if (numerator % denominator == 0) { // if whole number ONLY
            stringFormat += numerator / denominator;
        } else {
            if (numerator > denominator){ // if mixed number
                stringFormat += numerator / denominator + "_";
                numerator %= denominator;
            }
            stringFormat += numerator + "/" + denominator;
        }
        
        return stringFormat;
    }
    
    // gets the greatest common factor of two numbers
    private static int getGCF(int a, int b){
        return (b == 0) ? a : getGCF(b, a % b);
    }
    
    // if a string contains ANY character in a character array
    private static boolean charInArray(char c, char[] characters){
        for (char character : characters){
            if (character == c) return true;
        }
        return false;
    }
    
    // strips off parentheses from a string
    private static String stripParentheses(String string) {
        while (string.length() > 0 && string.charAt(0) == '('){
            string = string.substring(1);
        }
        while (string.length() > 0 && string.charAt(string.length() - 1) == ')') {
            string = string.substring(0, string.length() - 1);
        }
        
        return string;
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
    
    // Checks if the string input is a valid FracCalc operation
    private static String testValidOperation(String input) {
        String[] inputTerms = input.split(" ");
        // The terms must be in the format {operand operator operand} (odd number of terms)
        if (inputTerms.length % 2 == 0 || input.length() < 5) return "Error: Input is in an invalid format";
        // make sure the number of opening and closing parenthesis are equal
        if (!areParenthesesValid(input)) return "Error: Parentheses are in an invalid format";
        
        ArrayList<int[]> operands = new ArrayList<int[]>();
        ArrayList<Character> operators = new ArrayList<Character>();
        
        // loop over terms
        for (int termNumber = 0; termNumber < inputTerms.length; termNumber++) {
            String term = inputTerms[termNumber];
            if (termNumber % 2 == 0){ // OPERAND TERMS
                if (!isValidOperand(term)) return String.format("Error: Invalid Operand %s", term);
                operands.add(toFractionForm(term));
            } else { // OPERATOR TERMS
                if (!isValidOperator(term)) return String.format("Error: Invalid Operator %s", term);
                operators.add(term.charAt(0));
            }
        }
        // this line is reached only if all the terms are in a valid format
        // the following lines check for division by zero
        for (int i = 0; i < operands.size(); i++) {
            int[] operand = operands.get(i);
            if (operand[1] == 0) {
                return String.format("Error: denominator is zero for operand %s", inputTerms[i * 2]);
            }
        }
        for (int i = 0; i < operators.size(); i++) {
            if (operators.get(i) == '/' && operands.get(i + 1)[0] == 0) return "Error: division by zero";
        }
        
        return ""; // if nothing raised an error, return empty string
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
