package com.example.myapplication;

import java.util.List;

public abstract class Loan {
    protected double amount;
    protected int years;
    protected int months;
    protected int delay;
    protected int deferralStartMonth;
    protected double proc;

    public Loan(double amount, int years, int months, int delay, int deferralStartMonth, double proc) {
        this.amount = amount;
        this.years = years;
        this.months = months;
        this.delay = delay;
        this.deferralStartMonth = deferralStartMonth;
        this.proc = proc;
    }

    public abstract List<LoanPayment> calculatePayments();

    public double getAmount() {
        return amount;
    }

    public int getYears() {
        return years;
    }

    public int getMonths() {
        return months;
    }

    public int getDelay() {
        return delay;
    }

    public int getDeferralStartMonth() {
        return deferralStartMonth;
    }

    public double getProc() {
        return proc;
    }
}
