
package edu.univ.model;

import java.time.LocalDateTime;

public class Payment {
    public enum Type { BILL, PAY, REFUND }
    private final LocalDateTime at = LocalDateTime.now();
    private final Type type;
    private final double amount;
    private final String note;

    private Payment(Type t, double amt, String note){
        this.type = t; this.amount = amt; this.note = note;
    }
    public static Payment bill(double a, String n){ return new Payment(Type.BILL, a, n); }
    public static Payment pay(double a, String n){ return new Payment(Type.PAY, a, n); }
    public static Payment refund(double a, String n){ return new Payment(Type.REFUND, a, n); }
    public LocalDateTime getAt(){ return at; }
    public Type getType(){ return type; }
    public double getAmount(){ return amount; }
    public String getNote(){ return note; }
}
