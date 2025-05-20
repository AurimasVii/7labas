package com.example.myapplication;

public class LoanPayment {
    private final int month;
    private final double remainder;
    private final double payment;

    public LoanPayment(int month, double remainder, double payment) {
        this.month = month;
        this.remainder = remainder;
        this.payment = payment;
    }

    public int getMonth() {
        return month;
    }

    public double getRemainder() {
        return remainder;
    }

    public double getPayment() {
        return payment;
    }
}
