package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LoanPaymentAdapter extends RecyclerView.Adapter<LoanPaymentAdapter.PaymentViewHolder> {

    private final List<LoanPayment> payments;

    public LoanPaymentAdapter(List<LoanPayment> payments) {
        this.payments = payments;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.loan_payment_item, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        LoanPayment payment = payments.get(position);
        holder.monthText.setText("Mėn. " + payment.getMonth());
        holder.remainderText.setText("Likutis: " + String.format("%.2f", payment.getRemainder()));
        holder.paymentText.setText("Įmoka: " + String.format("%.2f", payment.getPayment()));
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView monthText, remainderText, paymentText;

        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            monthText = itemView.findViewById(R.id.monthText);
            remainderText = itemView.findViewById(R.id.remainderText);
            paymentText = itemView.findViewById(R.id.paymentText);
        }
    }
}
