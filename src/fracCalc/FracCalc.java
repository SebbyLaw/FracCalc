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
        
        return (evaluate(input));
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
        
        String nested = evaluate(expression.substring(openingIndex, closingIndex));
        return expression.substring(0, openingIndex) + nested + expression.substring(closingIndex + 1);
    }
    
    /**
     * Evaluates an expression ignoring parentheses
     * @param expression the expression to be evaluated
     * @return the fraction in mixed number string form
     */
    private static String evaluate(String expression) {
        char[][] priorityLevels = {{'*', '/'}, {'+', '-'}};
        int[][] operands = extractOperands(expression);
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
        return toMixedNumberForm(operands[0]);
    }
    
    /**
     * Does one operation. You must manually decrement the operators array
     * (using Objects would solve this!)
     * @param operands the operands array
     * @param operators the operators array
     * @param index the index of the operator array to evaluate
     * @return the updated operand array
     */
    private static int[][] doOperation(int[][] operands, char[] operators, int index){
        char op = operators[index];
        if (op == '*'){
            operands[index] = multiplication(operands[index], operands[index + 1]);
        } else if (op == '/'){
            operands[index] = division(operands[index], operands[index + 1]);
        } else if (op == '+'){
            operands[index] = addition(operands[index], operands[index + 1]);
        } else { // subtraction
            operands[index] = subtraction(operands[index], operands[index + 1]);
        }
        return removedIndex(operands, index + 1);
    }
    
    // multiplies one number with another in the form of fraction arrays
    private static int[] multiplication(int[] firstFactor, int[] secondFactor){
        int[] product = new int[2];
        
        product[0] = firstFactor[0] * secondFactor[0];
        product[1] = firstFactor[1] * secondFactor[1];
        
        return product;
    }
    
    // divides one number with another in the form of fraction arrays
    private static int[] division(int[] dividend, int[] divisor){
        int[] quotient = new int[2];
        
        quotient[0] = dividend[0] * divisor[1];
        quotient[1] = divisor[0] * dividend[1];
        
        return quotient;
    }
    
    // adds two numbers in the form of fraction arrays
    private static int[] addition(int[] firstAddend, int[] secondAddend){
        int[] sum = new int[2];
        
        int firstNumerator = firstAddend[0] * secondAddend[1];
        int secondNumerator = secondAddend[0] * firstAddend[1];
        
        sum[0] = firstNumerator + secondNumerator;
        sum[1] = firstAddend[1] * secondAddend[1];
        
        return sum;
    }
    
    // subtracts one number from another in the form of fraction arrays
    private static int[] subtraction(int[] minuend, int[] subtrahend){
        subtrahend[0] *= -1;
        // addition but negative
        return addition(minuend, subtrahend);
    }
    
    // returns the operands of a string expression
    private static int[][] extractOperands(String expression){
        String[] expressionTerms = expression.split(" ");
        int[][] operands = new int[expressionTerms.length / 2 + 1][2];
        for (int i = 0; i < expressionTerms.length; i++) {
            if (i % 2 == 0){
                operands[i / 2] = toFractionForm(expressionTerms[i]);
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
    
    /**
     * Converts the fraction array into an acceptable String format
     * @param fraction the array representing the fraction
     * @return the FracCalc output form String
     */
    private static String toMixedNumberForm(int[] fraction){
        simplifyFraction(fraction);
        int numerator = Math.abs(fraction[0]);
        int denominator = Math.abs(fraction[1]);
        
        String stringFormat = (fraction[0] * fraction[1] < 0) ? "-" : "";
        // negative sign if negative fraction, else empty string
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
        simplifyFraction(fraction);
        return fraction;
    }
    
    // returns a simplified a fraction array
    private static void simplifyFraction(int[] fraction){
        if (fraction[0] < 0 && fraction[1] < 0){
            fraction[0] = Math.abs(fraction[0]);
            fraction[1] = Math.abs(fraction[1]);
        }
        for (int i = Math.max(fraction[0], fraction[1]); i > 1; i--) {
            while (fraction[0] % i == 0 && fraction[1] % i == 0) {
                fraction[0] /= i;
                fraction[1] /= i;
            }
        }
    }
    
    // if a string contains ANY character in a character array
    private static boolean charInArray(char c, char[] characters){
        for (char character : characters){
            if (character == c) return true;
        }
        return false;
    }
    
    // cuts off the front of the string if it contains a cutoff character
    private static String cutOffFront(String string, char[] cutoffs){
        boolean keepGoing = true;
        while (keepGoing){
            if (string.length() > 0 && charInArray(string.charAt(0), cutoffs)) {
                string = string.substring(1);
            } else {
                keepGoing = false;
            }
        }
        return string;
    }
    
    // cuts off parentheses from a string
    private static String stripParentheses(String string) {
        string = cutOffFront(string, new char[]{'('});
        if (string.length() > 0) {
            while (string.charAt(string.length() - 1) == ')') {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
    
    // counts the number of a char in the string
    private static int countCharIn(String string, char c){
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) count++;
        }
        return count;
    }
    
    // removes an index from a operand array
    private static int[][] removedIndex(int[][] fractionArray, int index) {
        int[][] shifted = new int[fractionArray.length - 1][2];
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
    
    // Returns an array from one index to another - similar to substring
    private static int[][] subArray(int[][] array, int start, int end){
        int[][] sub = new int[end - start][];
        for (int i = start, j = 0; i < end; i++) {
            sub[j++] = array[i];
        }
        return sub;
    }
    
    // Checks if a string is only integers
    private static boolean notInteger(String string){
        if (string.length() == 0) return true; // cannot start as an empty string
        char[] integers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        string = cutOffFront(string, integers);
        return string.length() != 0;
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
        int[][] operands = extractOperands(input);
        char[] operators = extractOperators(input);
        for (int[] operand : operands) if (operand[1] == 0) return false;
        for (int i = 0; i < operators.length; i++) {
            if (operators[i] == '/' && operands[i + 1][0] == 0) return false;
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
    
    // Separated operand error handling for isValidOperation
    private static boolean isValidOperand(String operand){
        // remove parentheses and negative sign
        operand = stripParentheses(operand);
        if (operand.length() == 0) return false;
        if (operand.charAt(0) == '-') operand = operand.substring(1);
    
        String[] wholePart = operand.split("_");
        String[] fractionPart = wholePart[wholePart.length - 1].split("/");
    
        if (operand.indexOf('_') == -1) { // if not mixed number
            if (fractionPart.length == 2){ // if fraction only
                for (String num : fractionPart) if (notInteger(num)) return false;
            } else { // the term must be a whole number
                return !notInteger(operand);
            }
        } else { // if mixed number
            // whole number is integers
            if (notInteger(operand.substring(0, operand.indexOf('_')))) return false;
            // fraction parts are integers
            if (fractionPart.length == 2) { // if fraction only
                for (String num : fractionPart) if (notInteger(num)) return false;
            } else return false; // fraction must be length of 2
        }
        return true;
    }
    
    // Separated operator error handling for isValidOperation
    private static boolean isValidOperator(String term){
        if (term.length() > 1) return false;
        char[] validOperators = {'*', '/', '+', '-'};
        return charInArray(term.charAt(0), validOperators);
    }
}
