package controller;

import model.Statistics;
import service.StatisticsService;

import java.sql.SQLException;

public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController() {
        this.statisticsService =
                new StatisticsService();
    }

    public Statistics getOverview()
            throws SQLException {

        return statisticsService.getOverview();
    }
}