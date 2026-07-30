package view.components;

import controller.RevenueController;
import model.MonthlyRevenue;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class RevenueChartPanel extends ContentCard {

    private static final String REVENUE_SERIES =
            "Doanh thu";

    private final RevenueController controller;
    private final DefaultCategoryDataset dataset;

    private final JLabel totalRevenueLabel;
    private final JLabel statusLabel;

    public RevenueChartPanel() {
        controller = new RevenueController();
        dataset = new DefaultCategoryDataset();

        totalRevenueLabel = new JLabel("0 đ");
        statusLabel = new JLabel(
                "Dữ liệu 6 tháng gần nhất"
        );

        initializeView();
    }

    private void initializeView() {
        setLayout(
                new BorderLayout(0, 12)
        );

        setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        16,
                        20
                )
        );

        add(
                createHeaderPanel(),
                BorderLayout.NORTH
        );

        add(
                createChartPanel(),
                BorderLayout.CENTER
        );

        setMinimumSize(
                new Dimension(300, 290)
        );

        setPreferredSize(
                new Dimension(700, 320)
        );
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(
                new BorderLayout(12, 0)
        );

        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(
                new BorderLayout(0, 4)
        );

        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Biểu đồ doanh thu"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                "Doanh thu từ các khoản thanh toán đã hoàn tất"
        );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        titlePanel.add(
                titleLabel,
                BorderLayout.NORTH
        );

        titlePanel.add(
                descriptionLabel,
                BorderLayout.SOUTH
        );

        JPanel summaryPanel = new JPanel(
                new BorderLayout(0, 3)
        );

        summaryPanel.setOpaque(false);

        JLabel totalTitleLabel = new JLabel(
                "Tổng 6 tháng",
                SwingConstants.RIGHT
        );

        totalTitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        totalTitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalRevenueLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        totalRevenueLabel.setFont(
                UIConstants.FONT_HEADING.deriveFont(
                        Font.BOLD
                )
        );

        totalRevenueLabel.setForeground(
                UIConstants.SUCCESS
        );

        summaryPanel.add(
                totalTitleLabel,
                BorderLayout.NORTH
        );

        summaryPanel.add(
                totalRevenueLabel,
                BorderLayout.CENTER
        );

        headerPanel.add(
                titlePanel,
                BorderLayout.CENTER
        );

        headerPanel.add(
                summaryPanel,
                BorderLayout.EAST
        );

        return headerPanel;
    }

    private ChartPanel createChartPanel() {
        JFreeChart chart =
                ChartFactory.createLineChart(
                        null,
                        null,
                        null,
                        dataset
                );

        chart.setBackgroundPaint(
                Color.WHITE
        );

        chart.setBorderVisible(false);
        chart.removeLegend();

        CategoryPlot plot =
                chart.getCategoryPlot();

        configurePlot(plot);
        configureRenderer(plot);
        configureAxes(plot);

        ChartPanel chartPanel =
                new ChartPanel(chart);

        chartPanel.setLayout(
                new BorderLayout()
        );

        chartPanel.setBorder(null);
        chartPanel.setBackground(Color.WHITE);

        chartPanel.setPopupMenu(null);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);

        chartPanel.setMinimumSize(
                new Dimension(250, 220)
        );

        chartPanel.setPreferredSize(
                new Dimension(600, 250)
        );

        configureStatusLabel();

        chartPanel.add(
                statusLabel,
                BorderLayout.SOUTH
        );

        return chartPanel;
    }

    private void configurePlot(
            CategoryPlot plot
    ) {
        plot.setBackgroundPaint(
                Color.WHITE
        );

        plot.setOutlineVisible(false);

        plot.setDomainGridlinesVisible(
                false
        );

        plot.setRangeGridlinesVisible(
                true
        );

        plot.setRangeGridlinePaint(
                UIConstants.BORDER
        );

        plot.setRangeGridlineStroke(
                new BasicStroke(1f)
        );

        plot.setAxisOffset(
                new org.jfree.chart.ui.RectangleInsets(
                        8,
                        6,
                        4,
                        6
                )
        );
    }

    private void configureRenderer(
            CategoryPlot plot
    ) {
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
                new BasicStroke(
                        2.6f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND
                )
        );

        renderer.setSeriesShape(
                0,
                new Ellipse2D.Double(
                        -4,
                        -4,
                        8,
                        8
                )
        );

        renderer.setSeriesShapesVisible(
                0,
                true
        );

        renderer.setSeriesShapesFilled(
                0,
                true
        );

        renderer.setUseFillPaint(true);

        renderer.setSeriesFillPaint(
                0,
                Color.WHITE
        );

        renderer.setSeriesOutlinePaint(
                0,
                UIConstants.PRIMARY
        );

        renderer.setSeriesOutlineStroke(
                0,
                new BasicStroke(2f)
        );

        renderer.setDrawOutlines(true);

        renderer.setDefaultItemLabelsVisible(
                true
        );

        renderer.setDefaultItemLabelFont(
                UIConstants.FONT_SMALL
        );

        renderer.setDefaultItemLabelPaint(
                UIConstants.TEXT_SECONDARY
        );

        renderer.setDefaultItemLabelGenerator(
                new StandardCategoryItemLabelGenerator(
                        "{2} đ",
                        new DecimalFormat("#,##0")
                )
        );

        plot.setRenderer(renderer);
    }

    private void configureAxes(
            CategoryPlot plot
    ) {
        CategoryAxis domainAxis =
                plot.getDomainAxis();

        domainAxis.setTickLabelFont(
                UIConstants.FONT_SMALL
        );

        domainAxis.setTickLabelPaint(
                UIConstants.TEXT_SECONDARY
        );

        domainAxis.setAxisLineVisible(false);
        domainAxis.setTickMarksVisible(false);

        domainAxis.setLowerMargin(0.05);
        domainAxis.setUpperMargin(0.05);
        domainAxis.setCategoryMargin(0.18);

        NumberAxis rangeAxis =
                (NumberAxis) plot.getRangeAxis();

        rangeAxis.setTickLabelFont(
                UIConstants.FONT_SMALL
        );

        rangeAxis.setTickLabelPaint(
                UIConstants.TEXT_SECONDARY
        );

        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setTickMarksVisible(false);

        rangeAxis.setAutoRangeIncludesZero(
                true
        );

        rangeAxis.setLowerMargin(0.08);
        rangeAxis.setUpperMargin(0.20);

        rangeAxis.setNumberFormatOverride(
                new CompactCurrencyFormat()
        );
    }

    private void configureStatusLabel() {
        statusLabel.setFont(
                UIConstants.FONT_SMALL
        );

        statusLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        statusLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        statusLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        0,
                        0,
                        4
                )
        );
    }

    public void loadRevenue() {
        dataset.clear();

        totalRevenueLabel.setText("0 đ");
        statusLabel.setText(
                "Đang tải dữ liệu..."
        );

        try {
            List<MonthlyRevenue> revenues =
                    controller.getLastSixMonths();

            if (revenues == null) {
                revenues = Collections.emptyList();
            }

            if (revenues.isEmpty()) {
                showEmptyData();
                return;
            }

            double totalRevenue = 0;
            int validMonthCount = 0;

            for (MonthlyRevenue revenue : revenues) {
                if (
                        revenue == null
                                || revenue.getAmount() == null
                ) {
                    continue;
                }

                String monthLabel =
                        normalizeMonthLabel(
                                revenue.getMonthLabel()
                        );

                Number amount =
                        revenue.getAmount();

                dataset.addValue(
                        amount,
                        REVENUE_SERIES,
                        monthLabel
                );

                totalRevenue +=
                        amount.doubleValue();

                validMonthCount++;
            }

            if (validMonthCount == 0) {
                showEmptyData();
                return;
            }

            totalRevenueLabel.setText(
                    formatCurrency(totalRevenue)
            );

            statusLabel.setText(
                    "Đã cập nhật "
                            + validMonthCount
                            + " tháng gần nhất"
            );

        } catch (SQLException exception) {
            showErrorData();

            System.err.println(
                    "Không thể tải biểu đồ doanh thu: "
                            + exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showErrorData();

            System.err.println(
                    "Lỗi khi hiển thị biểu đồ doanh thu: "
                            + exception.getMessage()
            );
        }
    }

    private void showEmptyData() {
        dataset.clear();

        dataset.addValue(
                0,
                REVENUE_SERIES,
                "Chưa có dữ liệu"
        );

        totalRevenueLabel.setText("0 đ");

        statusLabel.setText(
                "Chưa có khoản thanh toán hoàn tất"
        );
    }

    private void showErrorData() {
        dataset.clear();

        dataset.addValue(
                0,
                REVENUE_SERIES,
                "Lỗi dữ liệu"
        );

        totalRevenueLabel.setText("--");

        statusLabel.setText(
                "Không thể tải dữ liệu doanh thu"
        );
    }

    private String normalizeMonthLabel(
            String monthLabel
    ) {
        if (
                monthLabel == null
                        || monthLabel.isBlank()
        ) {
            return "Không xác định";
        }

        return monthLabel.trim();
    }

    private String formatCurrency(
            double value
    ) {
        return new DecimalFormat(
                "#,##0 đ"
        ).format(value);
    }

    /**
     * Định dạng giá trị trên trục tung theo dạng ngắn:
     * 1.000.000 -> 1 Tr
     * 1.000 -> 1 N
     */
    private static class CompactCurrencyFormat
            extends DecimalFormat {

        private final DecimalFormat decimalFormat =
                new DecimalFormat("#,##0.#");

        @Override
        public StringBuffer format(
                double number,
                StringBuffer result,
                java.text.FieldPosition fieldPosition
        ) {
            double absoluteValue =
                    Math.abs(number);

            if (absoluteValue >= 1_000_000_000) {
                result.append(
                        decimalFormat.format(
                                number / 1_000_000_000
                        )
                ).append(" Tỷ");

            } else if (absoluteValue >= 1_000_000) {
                result.append(
                        decimalFormat.format(
                                number / 1_000_000
                        )
                ).append(" Tr");

            } else if (absoluteValue >= 1_000) {
                result.append(
                        decimalFormat.format(
                                number / 1_000
                        )
                ).append(" N");

            } else {
                result.append(
                        decimalFormat.format(number)
                );
            }

            return result;
        }

        @Override
        public StringBuffer format(
                long number,
                StringBuffer result,
                java.text.FieldPosition fieldPosition
        ) {
            return format(
                    (double) number,
                    result,
                    fieldPosition
            );
        }
    }
}