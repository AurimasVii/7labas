package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Anuites extends Loan {

    public Anuites(double amount, int years, int months, int delay, int deferralStartMonth, double proc) {
        super(amount, years, months, delay, deferralStartMonth, proc);
    }

    @Override
    public List<LoanPayment> calculatePayments() {
        List<LoanPayment> payments = new ArrayList<>();
        int totalMonths = years * 12 + months;
        double interestRate = proc / 100.0 / 12;
        double remainingLoan = amount;

        double monthlyPayment = 0;
        int nonDeferredMonths = totalMonths - delay;

        if (interestRate != 0) {
            monthlyPayment = amount * (interestRate * Math.pow(1 + interestRate, nonDeferredMonths)) /
                    (Math.pow(1 + interestRate, nonDeferredMonths) - 1);
        } else {
            monthlyPayment = amount / nonDeferredMonths;
        }

        for (int i = 1; i <= totalMonths; i++) {
            boolean isDeferred = (i >= deferralStartMonth) && (i < deferralStartMonth + delay);
            if (isDeferred) {
                double interestPayment = remainingLoan * interestRate;
                payments.add(new LoanPayment(i, remainingLoan, interestPayment));
            } else {
                double interestPayment = remainingLoan * interestRate;
                double principalPayment = monthlyPayment - interestPayment;
                remainingLoan -= principalPayment;
                if (remainingLoan < 0) remainingLoan = 0;
                payments.add(new LoanPayment(i, remainingLoan, monthlyPayment));
            }
        }

        return payments;
    }
}
