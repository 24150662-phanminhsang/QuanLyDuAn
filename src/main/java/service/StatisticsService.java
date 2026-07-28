package service;

import dao.StatisticsDAO;
import model.Statistics;

import java.sql.SQLException;

public class StatisticsService {

    private final StatisticsDAO statisticsDAO;

    public StatisticsService() {
        this.statisticsDAO = new StatisticsDAO();
    }

    public Statistics getOverview() throws SQLException {
        return statisticsDAO.getOverview();
    }
}