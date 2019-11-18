/*
Sebastian Law
2019.11.18
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
     * Creates a Fraction in the numerator/denominator format
     * @param numerator the numerator
     * @param denominator the denominator
     */
    public Fraction(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    /**
     * Directly multiplies (similar to *=) a Fraction by another
     * @param factor the Fraction to multiply by
     */
    public void multiplyBy(Fraction factor){
        this.numerator *= factor.numerator;
        this.denominator *= factor.denominator;
    }
    
    /**
     * Directly divides (similar to /=) a Fraction by another
     * @param divisor the Fraction to divide by
     */
    public void divideBy(Fraction divisor){
        this.numerator *= divisor.denominator;
        this.denominator *= divisor.numerator;
    }
    
    /**
     * Directly adds (similar to +=) a Fraction to another
     * @param addend the Fraction to add
     */
    public void addedBy(Fraction addend){
        int firstNumerator = this.numerator * addend.denominator;
        int secondNumerator = addend.numerator * this.denominator;
    
        this.numerator = firstNumerator + secondNumerator;
        this.denominator *= addend.denominator;
    }
    
    /**
     * Directly subtracts (similar to -=) a Fraction by another
     * @param subtrahend the Fraction to subtract by
     */
    public void subtractedBy(Fraction subtrahend){
        subtrahend.numerator *= -1;
        this.addedBy(subtrahend);
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
