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
        this.simplify();
    }
    
    private MixedNumber(int numerator, int denominator){
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public void multiplyBy(MixedNumber factor){
        this.numerator *= factor.numerator;
        this.denominator *= factor.denominator;
        this.simplify();
    }
    
    public void divideBy(MixedNumber divisor){
        this.numerator *= divisor.denominator;
        this.denominator *= divisor.numerator;
        this.simplify();
    }
    
    public void addedBy(MixedNumber addend){
        int firstNumerator = this.numerator * addend.denominator;
        int secondNumerator = addend.numerator * this.denominator;
    
        this.numerator = firstNumerator + secondNumerator;
        this.denominator *= addend.denominator;
    
        this.simplify();
    }
    
    public void subtractedBy(MixedNumber subtrahend){
        MixedNumber NegativeSub = new MixedNumber(subtrahend.numerator * -1, subtrahend.denominator);
        this.addedBy(NegativeSub);
    }
    
    private void simplify(){
        if (numerator < 0 && denominator < 0){
            numerator = Math.abs(numerator);
            denominator = Math.abs(denominator);
        }
        for (int i = Math.max(numerator, denominator); i > 1; i--) {
            while (numerator % i == 0 && denominator % i == 0) {
                numerator /= i;
                denominator /= i;
            }
        }
    }
    
    public int getNumerator() {
        return numerator;
    }
    
    public int getDenominator() {
        return denominator;
    }
    
    public String toString() {
        this.simplify();
        String stringFormat = (numerator * denominator < 0) ? "-" : "";
        
        numerator = Math.abs(numerator);
        denominator = Math.abs(denominator);
    
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
}
