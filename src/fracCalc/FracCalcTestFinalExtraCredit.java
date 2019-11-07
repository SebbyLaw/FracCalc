package fracCalc;

/*
Unit tests for FracCalc extra credit
by Sebastian Law
 */

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FracCalcTestFinalExtraCredit {
    /* Multiple operations is already handled by FracCalcTestFinal */
    
    // No parenthesis
    @Test public void OrderOfOps1() {assertEquals("9_11/21", FracCalc.produceAnswer("1_2/3 + 5/4 + 5_5/4 - 2_2/4 / -5_6/3"));}
    @Test public void OrderOfOps2() {assertEquals("5", FracCalc.produceAnswer("1 + 3 + -3 / -3"));}
    @Test public void OrderOfOps3() {assertEquals("3_11/12", FracCalc.produceAnswer("20/8 + 3_1/3 * 4/5 - 5/4"));}
    @Test public void OrderOfOps4() {assertEquals("2", FracCalc.produceAnswer("12345 - 12345 + 12345 / 12345 + 1"));}
    @Test public void OrderOfOps5() {assertEquals("4_1/2", FracCalc.produceAnswer("16/4 / 3/2 * 3/2 + 1/2"));}
    @Test public void OrderOfOps6() {assertEquals("0", FracCalc.produceAnswer("12457 / -1 + 12457"));}
    @Test public void OrderOfOps7() {assertEquals("46", FracCalc.produceAnswer("5_3/4 - -6_8/8 * 5_3/4"));}
    @Test public void OrderOfOps8() {assertEquals("-1_1/4", FracCalc.produceAnswer("2 * 3 - 6 + -1_1/2 * -1/2 - -1/2 - -1/2 - 3"));}
    @Test public void OrderOfOps9() {assertEquals("12", FracCalc.produceAnswer("20 / 5 - -1 * 8"));}
    
    // Error Handling (check if the produced answer contains "error")
    @Test public void ErrorHandling1() {assertTrue(FracCalc.produceAnswer("1 + + 1/2").toLowerCase().contains("error"));}
    @Test public void ErrorHandling2() {assertTrue(FracCalc.produceAnswer("12").toLowerCase().contains("error"));}
    @Test public void ErrorHandling3() {assertTrue(FracCalc.produceAnswer("1 +2").toLowerCase().contains("error"));}
    @Test public void ErrorHandling4() {assertTrue(FracCalc.produceAnswer("5_ + 3_1/2").toLowerCase().contains("error"));}
    @Test public void ErrorHandling5() {assertTrue(FracCalc.produceAnswer("5_2/ + 3").toLowerCase().contains("error"));}
    @Test public void ErrorHandling6() {assertTrue(FracCalc.produceAnswer("                     ").toLowerCase().contains("error"));}
    @Test public void ErrorHandling7() {assertTrue(FracCalc.produceAnswer("").toLowerCase().contains("error"));}
    @Test public void ErrorHandling8() {assertTrue(FracCalc.produceAnswer("1/2 + 1_2").toLowerCase().contains("error"));}
    @Test public void ErrorHandling9() {assertTrue(FracCalc.produceAnswer("_1/2 - 13").toLowerCase().contains("error"));}
    @Test public void ErrorHandling10() {assertTrue(FracCalc.produceAnswer("100 / 10_").toLowerCase().contains("error"));}
    @Test public void ErrorHandling11() {assertTrue(FracCalc.produceAnswer("1/32 -1/8").toLowerCase().contains("error"));}
    @Test public void ErrorHandling12() {assertTrue(FracCalc.produceAnswer("( + 0 / 0 / /0 / 0// 0 // / )").toLowerCase().contains("error"));}
    // Division by zero errors (Also error handling)
    @Test public void DivByZero1() {assertTrue(FracCalc.produceAnswer("1/0 + 1").toLowerCase().contains("error"));}
    @Test public void DivByZero2() {assertTrue(FracCalc.produceAnswer("12 + 15_3/0").toLowerCase().contains("error"));}
    @Test public void DivByZero3() {assertTrue(FracCalc.produceAnswer("1 / 11/0").toLowerCase().contains("error"));}
    @Test public void DivByZero4() {assertTrue(FracCalc.produceAnswer("12 / 0/3").toLowerCase().contains("error"));}
    @Test public void DivByZero5() {assertTrue(FracCalc.produceAnswer("123 / 0_0/1").toLowerCase().contains("error"));}
    @Test public void DivByZero6() {assertTrue(FracCalc.produceAnswer("1337 / 0/0").toLowerCase().contains("error"));}
    @Test public void DivByZero7() {assertTrue(FracCalc.produceAnswer("0/0 + 3/2").toLowerCase().contains("error"));}
    @Test public void DivByZero8() {assertTrue(FracCalc.produceAnswer("0 + 1_1/0").toLowerCase().contains("error"));}
    @Test public void DivByZero9() {assertTrue(FracCalc.produceAnswer("100/0 + 100/0").toLowerCase().contains("error"));}
    
    // Parentheses
    @Test public void Parenthesis1() {assertEquals("-3", FracCalc.produceAnswer("3 - (12 - 6)"));}
    @Test public void Parenthesis2() {assertEquals("-7", FracCalc.produceAnswer("3 - (12 - 2)"));}
    @Test public void Parenthesis3() {assertEquals("-11", FracCalc.produceAnswer("3 - (12 + 2)"));}
    @Test public void Parenthesis4() {assertEquals("0", FracCalc.produceAnswer("2 * (1_1/2 - 3/2)"));}
    @Test public void Parenthesis5() {assertEquals("7/8", FracCalc.produceAnswer("(1 - 1/2) * 1_3/4"));}
    @Test public void Parenthesis6() {assertEquals("1/2", FracCalc.produceAnswer("(1/2 + 1/2 + 1/2) / 3"));}
    @Test public void Parenthesis7() {assertEquals("191", FracCalc.produceAnswer("1337 / (191 - 184)"));}
    @Test public void Parenthesis8() {assertEquals("0", FracCalc.produceAnswer("(1/3 * 4 + 3) * 0 * (1 + 18_1/23)"));}
    @Test public void Parenthesis9() {assertEquals("147_49/69", FracCalc.produceAnswer("(1/3 * 4 + 3) * (2 * (1 + 18_1/23) - 4)"));}
    @Test public void Parenthesis10() {assertEquals("9", FracCalc.produceAnswer("(1 + 2) * 3"));}
    // Error Handling (With parentheses!)
    @Test public void ParenthesisError1() {assertTrue(FracCalc.produceAnswer("(1/2 + 1").toLowerCase().contains("error"));}
    @Test public void ParenthesisError2() {assertTrue(FracCalc.produceAnswer("(1/2) + 1").toLowerCase().contains("error"));}
    @Test public void ParenthesisError3() {assertTrue(FracCalc.produceAnswer("(1/2 +) 1").toLowerCase().contains("error"));}
    @Test public void ParenthesisError4() {assertTrue(FracCalc.produceAnswer("(1/3 + 1) - 3)").toLowerCase().contains("error"));}
    @Test public void ParenthesisError5() {assertTrue(FracCalc.produceAnswer("(1 + )3 - 4").toLowerCase().contains("error"));}
    @Test public void ParenthesisError6() {assertTrue(FracCalc.produceAnswer("").toLowerCase().contains("error"));}
    @Test public void ParenthesisError7() {assertTrue(FracCalc.produceAnswer("(((2 - 2) * 2)").toLowerCase().contains("error"));}
    @Test public void ParenthesisError8() {assertTrue(FracCalc.produceAnswer("((()))").toLowerCase().contains("error"));}
    @Test public void ParenthesisError9() {assertTrue(FracCalc.produceAnswer("(2) + (2)").toLowerCase().contains("error"));}
    @Test public void ParenthesisError10() {assertTrue(FracCalc.produceAnswer("11) + (3").toLowerCase().contains("error"));}
    
}
