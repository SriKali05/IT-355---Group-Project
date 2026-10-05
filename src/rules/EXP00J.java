package src.rules;

import java.math.BigDecimal;

/** 
 * Demonstrates CERT rule EXP00-J (Do not ignore values returned by methods). 
 * 
 * BigDecimal objects cannot be changed after they are created. Methods such 
 * as add() return a new BigDecimal instead of changing the original value. 
 * The noncompliant example ignores the value returned by add(), so the 
 * balance does not change. 
 * 
 * The compliant example saves the returned value, so the balance is updated. */
public class EXP00J {
    /**
     * Runs the noncompliant and compliant examples. 
     * 
     * The noncompliant example calls add() but does not save the new value. 
     * The compliant example saves the value returned by add(). 
     * 
     * @param args command-line arguments (not used) 
     */
    public static void main(String[] args) {
        BigDecimal deposit = new BigDecimal("25.50");

        // NONCOMPLIANT: add() returns a new value, but it is thrown away
        BigDecimal badBalance = new BigDecimal("100.00");
        badBalance.add(deposit);
        System.out.println("Noncompliant balance: " + badBalance);

        // COMPLIANT: the returned value is saved
        BigDecimal goodBalance = new BigDecimal("100.00");
        goodBalance = goodBalance.add(deposit);
        System.out.println("Compliant balance:    " + goodBalance);
    }
}
