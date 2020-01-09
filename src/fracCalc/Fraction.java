/*
Sebastian Law
2019.11.19
 */

package fracCalc;

public class Fraction {
    
    private int numerator;
    private int denominator;
    
    /**
     * Takes in a string in the correct format
     * @param formatted the formatted FracCalc string
     */
    public Fraction(String formatted){
        formatted = FracCalc.stripParentheses(formatted);
    
        boolean isNegative = formatted.indexOf('-') == 0;
        if (isNegative) formatted = formatted.substring(1);
    
        String[] wholePart = formatted.split("_");
        String[] fractionPart = wholePart[wholePart.length - 1].split("/");
    
        // denominator is denominator (1 if whole number only)
        this.denominator = fractionPart.length == 2 ? Integer.parseInt(fractionPart[1]) : 1;
        // numerator is numerator (same if only whole number)
        this.numerator = Integer.parseInt(fractionPart[0]);
        // Add the whole to the numerator if it's a mixed number
        if (wholePart.length == 2) this.numerator += Integer.parseInt(wholePart[0]) * this.denominator;
    
        if (isNegative) this.numerator *= -1;
    }
    
    /**
     * Constructor for only one whole number
     * @param wholeNumber the whole number
     */
    public Fraction(int wholeNumber){
        this.numerator = wholeNumber;
        this.denominator = 1;
    }
    
    /**
     * Constructor for numerator and denominator
     * @param numerator the numerator
     * @param denominator the denominator
     */
    public Fraction(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    /**
     * Constructor for mixed number with known values
     * @param wholeNumber he whole number
     * @param numerator the numerator
     * @param denominator the denominator
     */
    public Fraction(int wholeNumber, int numerator, int denominator){
        this.denominator = denominator;
        this.numerator = numerator + (wholeNumber * denominator);
    }
    
    /**
     * Multiplies two Fractions by each other and returns a new Fraction with the result
     * @param factor1 the first Fraction to multiply
     * @param factor2 the second Fraction to multiply
     */
    public static Fraction multiply(Fraction factor1, Fraction factor2){
        int numerator = factor1.numerator * factor2.numerator;
        int denominator = factor1.denominator * factor2.denominator;
        
        return new Fraction(numerator, denominator);
    }
    
    /**
     * Divides one Fraction by another and returns a new Fraction with the result
     * @param dividend the dividend of the equation
     * @param divisor the divisor of the equation
     */
    public static Fraction divide(Fraction dividend, Fraction divisor){
        int numerator = dividend.numerator * divisor.denominator;
        int denominator = dividend.denominator * divisor.numerator;
        
        return new Fraction(numerator, denominator);
    }
    
    /**
     * Adds two Fractions to each other and returns a new Fraction with the result
     * @param addend1 the first Fraction to add
     * @param addend2 the second Fraction to add
     */
    public static Fraction add(Fraction addend1, Fraction addend2){
        int firstNumerator = addend1.numerator * addend2.denominator;
        int secondNumerator = addend2.numerator * addend1.denominator;
    
        int numerator = firstNumerator + secondNumerator;
        int denominator = addend1.denominator * addend2.denominator;
        
        return new Fraction(numerator, denominator);
    }
    
    /**
     * Subtracts one Fraction from another and returns a new Fraction with the result
     * @param minuend the minuend of the equation
     * @param subtrahend the subtrahend of the equation
     */
    public static Fraction subtract(Fraction minuend, Fraction subtrahend){
        // Just addition but negative
        Fraction negativeSub = new Fraction(subtrahend.numerator * -1, subtrahend.denominator);
        return add(minuend, negativeSub);
    }
    
    /**
     * @return the Fraction numerator
     */
    public int getNumerator() {
        return numerator;
    }
    
    /**
     * @return the Fraction denominator
     */
    public int getDenominator() {
        return denominator;
    }
    
    /**
     * @return the simplified string format of the Fraction
     */
    public String toString() {
        // simplify fraction
        int gcf = getGCF(numerator, denominator);
        numerator /= gcf;
        denominator /= gcf;
        
        // negative sign if negative fraction, else empty string
        String stringFormat = (numerator * denominator < 0) ? "-" : "";
        numerator = Math.abs(numerator);
        denominator = Math.abs(denominator);
    
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
     * Gets the greatest common denominator of two numbers
     * @param a first number
     * @param b second number
     * @return the greatest common denominator
     */
    private static int getGCF(int a, int b){
        return (b == 0) ? a : getGCF(b, a % b);
    }
}
