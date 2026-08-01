package view.components.student;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentResultPanel extends ContentCard {

    private final JPanel resultListPanel;
    private final JLabel averageScoreLabel;

    private List<ResultItem> results =
            new ArrayList<>();

    public StudentResultPanel() {
        resultListPanel = new JPanel();
        resultListPanel.setOpaque(false);

        averageScoreLabel = new JLabel(
                "0.0",
                SwingConstants.CENTER
        );

        initializeView();
        refreshResults();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[]"
                )
        );

        setMinimumSize(
                new Dimension(340, 260)
        );

        setPreferredSize(
                new Dimension(480, 350)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        resultListPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]10[]10[]"
                )
        );

        add(
                resultListPanel,
                "growx"
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[][grow][]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.CHART_LINE,
                        16,
                        UIConstants.PURPLE
                )
        );

        JLabel titleLabel = new JLabel(
                "Kết quả học tập"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JPanel scorePanel =
                createAverageScorePanel();

        panel.add(iconLabel);

        panel.add(
                titleLabel,
                "gapleft 7"
        );

        panel.add(
                scorePanel,
                "alignx right"
        );

        return panel;
    }

    private JPanel createAverageScorePanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, insets 6 10",
                        "[][grow]",
                        "[center]"
                )
        );

        panel.setBackground(
                UIConstants.PURPLE_LIGHT
        );

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 999; borderWidth: 0"
        );

        JLabel textLabel = new JLabel(
                "TB:"
        );

        textLabel.setFont(
                UIConstants.FONT_SMALL
        );

        textLabel.setForeground(
                UIConstants.PURPLE
        );

        averageScoreLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        averageScoreLabel.setForeground(
                UIConstants.PURPLE
        );

        panel.add(textLabel);
        panel.add(averageScoreLabel);

        return panel;
    }

    public void setResults(
            List<ResultItem> resultItems
    ) {
        results =
                resultItems == null
                        ? new ArrayList<>()
                        : new ArrayList<>(resultItems);

        refreshResults();
    }

    public void addResult(
            ResultItem result
    ) {
        if (result == null) {
            return;
        }

        results.add(result);
        refreshResults();
    }

    public void clearResults() {
        results.clear();
        refreshResults();
    }

    public List<ResultItem> getResults() {
        return Collections.unmodifiableList(
                results
        );
    }

    public void setAverageScore(
            double averageScore
    ) {
        double safeScore =
                Math.max(
                        0,
                        Math.min(averageScore, 10)
                );

        averageScoreLabel.setText(
                String.format("%.1f", safeScore)
        );
    }

    public void refreshResults() {
        resultListPanel.removeAll();

        if (results.isEmpty()) {
            setAverageScore(0);

            resultListPanel.add(
                    createEmptyState(),
                    "growx"
            );
        } else {
            double totalScore = 0;

            for (ResultItem result : results) {
                totalScore += result.score();

                resultListPanel.add(
                        createResultRow(result),
                        "growx"
                );
            }

            setAverageScore(
                    totalScore / results.size()
            );
        }

        resultListPanel.revalidate();
        resultListPanel.repaint();
    }

    private JPanel createResultRow(
            ResultItem result
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 11 12",
                        "[grow, fill]12[70!]",
                        "[]4[]"
                )
        );

        row.setBackground(
                UIConstants.BACKGROUND
        );

        row.putClientProperty(
                "FlatLaf.style",
                "arc: 12; borderWidth: 0"
        );

        JLabel courseLabel = new JLabel(
                safeText(
                        result.courseName(),
                        "Khóa học"
                )
        );

        courseLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        courseLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        courseLabel.setToolTipText(
                courseLabel.getText()
        );

        JLabel typeLabel = new JLabel(
                safeText(
                        result.resultType(),
                        "Điểm trung bình"
                )
        );

        typeLabel.setFont(
                UIConstants.FONT_SMALL
        );

        typeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        typeLabel.setToolTipText(
                typeLabel.getText()
        );

        JLabel scoreLabel = new JLabel(
                formatScore(
                        result.score()
                ),
                SwingConstants.CENTER
        );

        scoreLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD,
                        14f
                )
        );

        scoreLabel.setForeground(
                getScoreColor(
                        result.score()
                )
        );

        scoreLabel.setBackground(
                getScoreBackground(
                        result.score()
                )
        );

        scoreLabel.setOpaque(true);

        scoreLabel.setPreferredSize(
                new Dimension(66, 34)
        );

        scoreLabel.setMinimumSize(
                new Dimension(66, 34)
        );

        scoreLabel.putClientProperty(
                "FlatLaf.style",
                "arc: 999; borderWidth: 0"
        );

        row.add(
                courseLabel,
                "growx"
        );

        row.add(
                scoreLabel,
                "span 1 2, alignx right, aligny center"
        );

        row.add(
                typeLabel,
                "growx"
        );

        return row;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 28",
                        "[center]",
                        "[]10[]"
                )
        );

        panel.setOpaque(false);

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.CHART_BAR,
                                27,
                                UIConstants.TEXT_SECONDARY
                        )
                )
        );

        JLabel messageLabel = new JLabel(
                "Chưa có kết quả học tập"
        );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(messageLabel);

        return panel;
    }

    private String formatScore(
            double score
    ) {
        double safeScore =
                Math.max(
                        0,
                        Math.min(score, 10)
                );

        return String.format("%.1f", safeScore);
    }

    private Color getScoreColor(
            double score
    ) {
        if (score >= 8) {
            return UIConstants.SUCCESS;
        }

        if (score >= 5) {
            return UIConstants.WARNING;
        }

        return UIConstants.DANGER;
    }

    private Color getScoreBackground(
            double score
    ) {
        if (score >= 8) {
            return UIConstants.SUCCESS_LIGHT;
        }

        if (score >= 5) {
            return UIConstants.WARNING_LIGHT;
        }

        return UIConstants.DANGER_LIGHT;
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value.trim();
    }

    public record ResultItem(
            String courseName,
            String resultType,
            double score
    ) {
    }
}