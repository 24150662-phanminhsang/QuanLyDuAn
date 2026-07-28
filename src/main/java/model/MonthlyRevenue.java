package model;

import java.math.BigDecimal;

public class MonthlyRevenue {

    private final String monthLabel;
    private final BigDecimal amount;

    public MonthlyRevenue(
            String monthLabel,
            BigDecimal amount
    ) {
        this.monthLabel = monthLabel;
        this.amount = amount;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}