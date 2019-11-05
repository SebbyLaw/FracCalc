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
    
    // DO NOT EDIT THIS HEADER!!! All unit tests run off this method
    public static String produceAnswer(String input) {
        if (!isValidOperation(input)) return "ERROR: Invalid input";
        return toMixedNumberForm(evaluate(input));
    }
    
    /**
     * Evaluates an expression ignoring parentheses
     * @param expression the expression to be evaluated
     * @return the fraction represented as an integer array
     */
    private static int[] evaluate(String expression){
        int[][] operands = extractOperands(expression);
        char[] operators = extractOperators(expression);
        // run over priority in order of operations first
        while (charInArray('*', operators) || charInArray('/', operators)){
            for (int i = 0; i < operators.length; i++) {
                if (operators[i] == '*' || operators[i] == '/') {
                    operands = doOperation(operands, operators, i);
                    operators = removeIndex(operators, i--);
                }
            }
        }
        // run over every operator after
        for (int i = 0; i < operators.length; i++) {
            operands = doOperation(operands, operators, i);
            operators = removeIndex(operators, i--);
        }
        return operands[0];
    }
    
    // does one operation. You must manually decrement the operators array (using Objects would solve this!)
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
        return removeIndex(operands, index + 1);
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
        
        formatted = cutOffFront(formatted, '(');
        while (formatted.charAt(formatted.length() - 1) == ')') {
            formatted = formatted.substring(0, formatted.length() - 1);
        }
        
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
    
    // if a string DOES NOT contain ANY character in a character array
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
    
    // cuts off the front of the string, only takes in one char
    private static String cutOffFront(String string, char cutoff) {
        char[] c = {cutoff};
        return cutOffFront(string, c);
    }
    
    // counts the number of a char in the string
    private static int countCharIn(String string, char c){
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) count++;
        }
        return count;
    }
    
    // returns the last index of a character in a string since String.lastIndexOf() is illegal :(
    private static int[] indexesOf(String string, char c){
        int[] indexes = new int[countCharIn(string, c)];
        for (int i = 0, j = 0; i < string.length(); i++) {
            if (string.charAt(i) == c) indexes[j++] = i;
        }
        return indexes;
    }
    
    // removes an index from a operand array
    private static int[][] removeIndex(int[][] fractionArray, int index) {
        int[][] shifted = new int[fractionArray.length - 1][2];
        for (int i = 0, j = 0; i < fractionArray.length; i++) {
            if (i != index) shifted[j++] = fractionArray[i];
        }
        return shifted;
    }
    
    // removes an index from an operation array
    private static char[] removeIndex(char[] operationArray, int index){
        char[] shifted = new char[operationArray.length - 1];
        for (int i = 0, j = 0; i < operationArray.length; i++) {
            if (i != index) shifted[j++] = operationArray[i];
        }
        return shifted;
    }
    
    // Checks if the string input is a valid FracCalc operation
    private static boolean isValidOperation(String input) {
        // TODO: Fix this
        // make sure the number of opening and closing parenthesis are equal
        if (countCharIn(input, '(') != countCharIn(input, ')')) return false;
        
        String[] inputTerms = input.split(" ");
        
        // The terms must be in the format {operand operator operand} (odd number of terms)
        if (inputTerms.length % 2 == 0 || inputTerms.length < 3) return false;
        
        /* termNumber is the index of the term split by spaces
        even termNumber indicates operands
        odd termNumber indicates operators.
        
        This for loop checks each term by what it is indicated to be by position.
        */
        for (int termNumber = 0; termNumber < inputTerms.length; termNumber++) {
            String term = inputTerms[termNumber];
            
            if (termNumber % 2 == 0){ // OPERAND TERMS
                /*
                if (!term.matches("\\(*-?\\d+(_\\d+/\\d+|/\\d+)?\\)*")) return false;
                /*
                This is the best way to do it, but regex is illegal :(
                so it's hardcoded :)
                 */
                
                char[] validChars = {'-', '_', '/', '(', ')'};
                char[] integers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
                
                term = cutOffFront(term, '(');
                if (countCharIn(term, '(') > 0) return false;
                
                // make sure there are ONLY valid characters/digits
                for (int i = 0; i < term.length(); i++) {
                    char c = term.charAt(i);
                    if (!charInArray(c, validChars) && !charInArray(c, integers)) return false;
                }
                
                if (countCharIn(term, '-') > 0) { // if term has a negative sign
                    if (term.indexOf('-') != 0) return false; // if negative is not the first character
                    term = term.substring(1);
                    
                    if (term.length() == 0) return false;
                    // term MUST continue after the negative sign
                }
                
                if (!charInArray(term.charAt(0), integers)) return false;
                
                term = cutOffFront(term, integers);
                
                // if not a whole integer number (or closing parenthesis
                if (term.length() > 0) {
                    if (countCharIn(term, '_') > 0){ // if mixed number
                        if (term.indexOf('_') > 0) return false;
                        
                        term = term.substring(1);
                        if (term.length() == 0) return false;
                        // term MUST continue after the underscore
                        
                        if (!charInArray(term.charAt(0), integers)) return false;
                        
                        term = cutOffFront(term, integers);
                        if (term.length() == 0) return false;
                    }
                    
                    if (term.indexOf(')') == 0){
                        term = cutOffFront(term, ')');
                    } else {
                        // the next char MUST be a divisional operator
                        if (term.charAt(0) != '/') return false;
                        term = term.substring(1);
                        
                        if (term.length() == 0) return false;
                        // term MUST continue after the divisional operator
                        
                        // check to make sure denominator is NOT == 0
                        int denominatorTotal = 0;
                        for (int i = 0; i < term.length() - cutOffFront(term, integers).length(); i++) {
                            // iterate over all integer characters and add them to the total
                            denominatorTotal += Integer.parseInt(term.substring(i, i + 1));
                        }
                        if (denominatorTotal == 0) return false; // if all the denominator digits are 0
                        
                        term = cutOffFront(term, integers);
                        term = cutOffFront(term, ')');
                    }
                }
                // by now the string has to be empty
                if (term.length() != 0) return false;
                
            } else { // OPERATOR TERMS
                if (term.length() > 1) return false;
                char[] validOperators = {'*', '/', '+', '-'};
                if (!charInArray(term.charAt(0), validOperators)) return false;
            }
        }
        
        // check if division by zero
        int[][] operands = extractOperands(input);
        char[] operators = extractOperators(input);
        for (int i = 0; i < operators.length; i++) {
            if (operators[i] == '/' && operands[i + 1][0] == 0) {
                return false;
            }
        }
        // if nothing raised an error, return true
        return true;
    }
}
