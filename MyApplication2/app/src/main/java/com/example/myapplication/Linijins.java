package com.example.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Linijins extends Loan {

    public Linijins(double amount, int years, int months, int delay, int deferralStartMonth, double proc) {
        super(amount, years, months, delay, deferralStartMonth, proc);
    }

    @Override
    public List<LoanPayment> calculatePayments() {
        List<LoanPayment> payments = new ArrayList<>();
        int totalMonths = years * 12 + months;
        double interestRate = proc / 100.0 / 12;

        int nonDeferredMonths = totalMonths - delay;
        double monthlyPrincipal = amount / nonDeferredMonths;
        double remainingLoan = amount;

        for (int i = 1; i <= totalMonths; i++) {
            boolean isDeferred = (i >= deferralStartMonth) && (i < deferralStartMonth + delay);
            if (isDeferred) {
                double interestOnly = remainingLoan * interestRate;
                payments.add(new LoanPayment(i, remainingLoan, interestOnly));
            } else {
                double interest = remainingLoan * interestRate;
                double fullPayment = monthlyPrincipal + interest;
                remainingLoan -= monthlyPrincipal;
                if (remainingLoan < 0) remainingLoan = 0;
                payments.add(new LoanPayment(i, remainingLoan, fullPayment));
            }
        }

        return payments;
    }
}
