package dao;

import model.MonthlyRevenue;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RevenueDAO {

    private static final String SQL = """
            SELECT TOP 6
                YEAR(payment_date) AS revenue_year,
                MONTH(payment_date) AS revenue_month,
                SUM(amount) AS total_revenue
            FROM dbo.Payments
            WHERE status = 'PAID'
              AND payment_date IS NOT NULL
            GROUP BY
                YEAR(payment_date),
                MONTH(payment_date)
            ORDER BY
                YEAR(payment_date) DESC,
                MONTH(payment_date) DESC
            """;

    public List<MonthlyRevenue> findLastSixMonths()
            throws SQLException {

        List<MonthlyRevenue> result =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(SQL);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int year =
                        resultSet.getInt("revenue_year");

                int month =
                        resultSet.getInt("revenue_month");

                BigDecimal amount =
                        resultSet.getBigDecimal(
                                "total_revenue"
                        );

                result.add(
                        new MonthlyRevenue(
                                String.format(
                                        "%02d/%d",
                                        month,
                                        year
                                ),
                                amount == null
                                        ? BigDecimal.ZERO
                                        : amount
                        )
                );
            }
        }

        Collections.reverse(result);

        return result;
    }
}