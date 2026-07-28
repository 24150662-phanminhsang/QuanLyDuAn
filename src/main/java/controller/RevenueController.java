package controller;

import model.MonthlyRevenue;
import service.RevenueService;

import java.sql.SQLException;
import java.util.List;

public class RevenueController {

    private final RevenueService revenueService;

    public RevenueController() {
        revenueService = new RevenueService();
    }

    public List<MonthlyRevenue> getLastSixMonths()
            throws SQLException {

        return revenueService.getLastSixMonths();
    }
}