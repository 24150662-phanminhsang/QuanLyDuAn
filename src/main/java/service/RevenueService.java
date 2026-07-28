package service;

import dao.RevenueDAO;
import model.MonthlyRevenue;

import java.sql.SQLException;
import java.util.List;

public class RevenueService {

    private final RevenueDAO revenueDAO;

    public RevenueService() {
        revenueDAO = new RevenueDAO();
    }

    public List<MonthlyRevenue> getLastSixMonths()
            throws SQLException {

        return revenueDAO.findLastSixMonths();
    }
}