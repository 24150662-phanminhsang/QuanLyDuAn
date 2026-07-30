package model;

import java.util.Date;

public class Payment {
    private int paymentId;
    private int enrollmentId;
    private double amount;
    private Date paymentDate;
    private String status;

    public Payment() {}

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}