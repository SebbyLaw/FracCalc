package fracCalc;

public class MixedNumber {
    
    private int numerator;
    private int denominator;
    
    // Takes in a string in the correct format
    public MixedNumber(String formatted){
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
    
    private MixedNumber(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public void multiplyBy(MixedNumber factor){
        this.numerator *= factor.numerator;
        this.denominator *= factor.denominator;
    }
    
    public void divideBy(MixedNumber divisor){
        this.numerator *= divisor.denominator;
        this.denominator *= divisor.numerator;
    }
    
    public void addedBy(MixedNumber addend){
        int firstNumerator = this.numerator * addend.denominator;
        int secondNumerator = addend.numerator * this.denominator;
    
        this.numerator = firstNumerator + secondNumerator;
        this.denominator *= addend.denominator;
    }
    
    public void subtractedBy(MixedNumber subtrahend){
        MixedNumber NegativeSub = new MixedNumber(subtrahend.numerator * -1, subtrahend.denominator);
        this.addedBy(NegativeSub);
    }
    
    public int getNumerator() {
        return numerator;
    }
    
    public int getDenominator() {
        return denominator;
    }
    
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
    
    // gets the greatest common factor of two numbers
    private static int getGCF(int a, int b){
        return (b == 0) ? a : getGCF(b, a % b);
    }
}
