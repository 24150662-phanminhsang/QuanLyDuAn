package model;

import java.util.Date;

public class Payment {

    private int paymentId;

    private int studentId;
    private Integer enrollmentId;

    // Học phí gốc
    private double amount;
    private double originalAmount;

    // Giảm giá
    private double discountAmount;
    private Integer discountId;

    // Số tiền cuối cùng phải trả
    private double finalAmount;

    // Số tiền đã thanh toán
    private double paidAmount;

    private Date paymentDate;
    private Date dueDate;

    private String paymentMethod;
    private String status;
    private String note;
    private String transactionCode;

    public Payment() {
    }

    // =========================
    // TÍNH TOÁN
    // =========================

    public double getRemainingAmount() {
        return Math.max(0, finalAmount - paidAmount);
    }

    public boolean isPaid() {
        return paidAmount >= finalAmount && finalAmount > 0;
    }

    public boolean isPartiallyPaid() {
        return paidAmount > 0 && paidAmount < finalAmount;
    }

    // =========================
    // GETTER / SETTER
    // =========================

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Integer getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Integer discountId) {
        this.discountId = discountId;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}