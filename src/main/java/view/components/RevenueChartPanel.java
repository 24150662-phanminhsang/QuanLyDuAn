package view.components;

import controller.RevenueController;
import model.MonthlyRevenue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class RevenueChartPanel extends ContentCard {

    private final RevenueController controller;
    private final DefaultCategoryDataset dataset;

    public RevenueChartPanel() {
        controller = new RevenueController();
        dataset = new DefaultCategoryDataset();

        initializeView();
    }

    private void initializeView() {
        setLayout(new BorderLayout(0, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        15,
                        20
                )
        );

        JPanel headerPanel =
                new JPanel(new BorderLayout(0, 4));

        headerPanel.setOpaque(false);

        JLabel titleLabel =
                new JLabel("Biểu đồ doanh thu");

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                "Tổng doanh thu đã thanh toán trong 6 tháng gần nhất"
        );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        headerPanel.add(
                descriptionLabel,
                BorderLayout.SOUTH
        );

        add(headerPanel, BorderLayout.NORTH);
        add(createChartPanel(), BorderLayout.CENTER);

        /*
         * Không đặt chiều rộng lớn.
         */
        setMinimumSize(
                new Dimension(300, 300)
        );
    }

    private ChartPanel createChartPanel() {
        JFreeChart chart =
                ChartFactory.createLineChart(
                        null,
                        null,
                        null,
                        dataset
                );

        chart.setBackgroundPaint(Color.WHITE);
        chart.removeLegend();

        CategoryPlot plot =
                chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        plot.setDomainGridlinesVisible(false);

        plot.setRangeGridlinePaint(
                UIConstants.BORDER
        );

        plot.getDomainAxis().setTickLabelFont(
                UIConstants.FONT_SMALL
        );

        plot.getDomainAxis().setTickLabelPaint(
                UIConstants.TEXT_SECONDARY
        );

        plot.getRangeAxis().setTickLabelFont(
                UIConstants.FONT_SMALL
        );

        plot.getRangeAxis().setTickLabelPaint(
                UIConstants.TEXT_SECONDARY
        );

        LineAndShapeRenderer renderer =
                new LineAndShapeRenderer(
                        true,
                        true
                );

        renderer.setSeriesPaint(
                0,
                UIConstants.PRIMARY
        );

        renderer.setSeriesStroke(
                0,
                new BasicStroke(2.5f)
        );

        renderer.setSeriesShapesVisible(
                0,
                true
        );

        renderer.setSeriesShapesFilled(
                0,
                true
        );

        renderer.setDefaultItemLabelsVisible(true);

        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                        "{2} đ",
                        new DecimalFormat("#,##0")
                )
        );

        plot.setRenderer(renderer);

        if (
                plot.getRangeAxis()
                        instanceof NumberAxis numberAxis
        ) {
            numberAxis.setAutoRangeIncludesZero(true);

            numberAxis.setNumberFormatOverride(
                    new DecimalFormat("#,##0 đ")
            );
        }

        ChartPanel chartPanel =
                new ChartPanel(chart);

        chartPanel.setBorder(null);
        chartPanel.setBackground(Color.WHITE);

        chartPanel.setPopupMenu(null);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        chartPanel.setMinimumSize(
                new Dimension(250, 230)
        );

        /*
         * Chiều rộng nhỏ để chart tự co.
         */
        chartPanel.setPreferredSize(
                new Dimension(300, 260)
        );

        return chartPanel;
    }

    public void loadRevenue() {
        dataset.clear();

        try {
            List<MonthlyRevenue> revenues =
                    controller.getLastSixMonths();

            if (revenues.isEmpty()) {
                dataset.addValue(
                        0,
                        "Doanh thu",
                        "Chưa có dữ liệu"
                );

                return;
            }

            for (MonthlyRevenue revenue : revenues) {
                dataset.addValue(
                        revenue.getAmount(),
                        "Doanh thu",
                        revenue.getMonthLabel()
                );
            }

        } catch (SQLException exception) {
            dataset.addValue(
                    0,
                    "Doanh thu",
                    "Lỗi dữ liệu"
            );

            System.err.println(
                    "Không thể tải biểu đồ doanh thu: "
                            + exception.getMessage()
            );
        }
    }
}