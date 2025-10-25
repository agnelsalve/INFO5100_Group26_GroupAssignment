
package edu.univ.model;

import java.util.*;

public class Account {
    private double balance = 0.0;
    private final List<Payment> history = new ArrayList<>();

    public double getBalance(){ return balance; }
    public List<Payment> getHistory(){ return history; }

    public void bill(double amt, String note){
        balance += amt;
        history.add(Payment.bill(amt, note));
    }
    public void pay(double amt, String note){
        balance -= amt;
        history.add(Payment.pay(amt, note));
    }
    public void refund(double amt, String note){
        balance -= amt; // negative charge to refund (reduces balance)
        history.add(Payment.refund(amt, note));
    }
}
