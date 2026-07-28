package view;

import controller.StatisticsController;
import model.Statistics;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import service.NotificationService;
import util.UIConstants;
import view.components.ActivityPanel;
import view.components.QuickActionPanel;
import view.components.RevenueChartPanel;
import view.components.ScrollablePanel;
import view.components.StatCard;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class StatisticsView
        extends ScrollablePanel {

    private final StatisticsController controller;

    private final JPanel cardsPanel;
    private final JPanel bottomPanel;

    private final List<StatCard> cards;

    private final RevenueChartPanel
            revenueChartPanel;

    private final ActivityPanel
            activityPanel;

    private final QuickActionPanel
            quickActionPanel;

    private int currentCardColumns = -1;
    private int currentBottomColumns = -1;

    public StatisticsView(
            NotificationService notificationService
    ) {
        controller =
                new StatisticsController();

        cardsPanel = new JPanel();
        cardsPanel.setOpaque(false);

        bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        StatCard usersCard =
                new StatCard(
                        "Tổng tài khoản",
                        "Người dùng trong hệ thống",
                        FontAwesomeSolid.USERS,
                        UIConstants.PRIMARY,
                        UIConstants.PRIMARY_LIGHT
                );

        StatCard activeCard =
                new StatCard(
                        "Đang hoạt động",
                        "Tài khoản ACTIVE",
                        FontAwesomeSolid.USER,
                        UIConstants.SUCCESS,
                        UIConstants.SUCCESS_LIGHT
                );

        StatCard studentsCard =
                new StatCard(
                        "Sinh viên",
                        "Tổng số sinh viên",
                        FontAwesomeSolid.USER_GRADUATE,
                        UIConstants.PURPLE,
                        UIConstants.PURPLE_LIGHT
                );

        StatCard teachersCard =
                new StatCard(
                        "Giảng viên",
                        "Tổng số giảng viên",
                        FontAwesomeSolid.CHALKBOARD_TEACHER,
                        UIConstants.WARNING,
                        UIConstants.WARNING_LIGHT
                );

        StatCard coursesCard =
                new StatCard(
                        "Khóa học",
                        "Tổng số khóa học",
                        FontAwesomeSolid.BOOK_OPEN,
                        UIConstants.PRIMARY,
                        UIConstants.PRIMARY_LIGHT
                );

        StatCard classesCard =
                new StatCard(
                        "Lớp học",
                        "Lớp đang quản lý",
                        FontAwesomeSolid.SCHOOL,
                        UIConstants.DANGER,
                        UIConstants.DANGER_LIGHT
                );

        cards = List.of(
                usersCard,
                activeCard,
                studentsCard,
                teachersCard,
                coursesCard,
                classesCard
        );

        revenueChartPanel =
                new RevenueChartPanel();

        activityPanel =
                new ActivityPanel(
                        notificationService
                );

        quickActionPanel =
                new QuickActionPanel();

        initializeView();

        addComponentListener(
                new ComponentAdapter() {

                    @Override
                    public void componentResized(
                            ComponentEvent event
                    ) {
                        rebuildResponsiveLayout();
                    }
                }
        );

        SwingUtilities.invokeLater(
                this::rebuildResponsiveLayout
        );

        loadStatistics();
    }

    private void initializeView() {
        setBackground(
                UIConstants.BACKGROUND
        );

        setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 12",
                        "[grow, fill]",
                        "[]10[]10[]"
                )
        );

        add(
                cardsPanel,
                "growx"
        );

        add(
                revenueChartPanel,
                "growx, height 290:315:340"
        );

        add(
                bottomPanel,
                "growx"
        );
    }

    public void setQuickActionHandler(
            Consumer<String> handler
    ) {
        quickActionPanel
                .setActionHandler(
                        handler
                );
    }

    public void refreshActivities() {
        activityPanel
                .refreshActivities();
    }

    private void rebuildResponsiveLayout() {
        int width = getWidth();

        if (width <= 0) {
            return;
        }

        int cardColumns;

        if (width >= 1350) {
            cardColumns = 6;

        } else if (width >= 720) {
            cardColumns = 3;

        } else if (width >= 470) {
            cardColumns = 2;

        } else {
            cardColumns = 1;
        }

        int bottomColumns =
                width >= 850
                        ? 2
                        : 1;

        if (
                cardColumns
                        != currentCardColumns
        ) {
            rebuildCards(cardColumns);

            currentCardColumns =
                    cardColumns;
        }

        if (
                bottomColumns
                        != currentBottomColumns
        ) {
            rebuildBottomPanel(
                    bottomColumns
            );

            currentBottomColumns =
                    bottomColumns;
        }

        int visibleHeight =
                getVisibleRect().height;

        if (visibleHeight >= 720) {
            activityPanel
                    .setVisibleLimit(4);
        } else {
            activityPanel
                    .setVisibleLimit(3);
        }
    }

    private void rebuildCards(
            int columns
    ) {
        cardsPanel.removeAll();

        int rows =
                (int) Math.ceil(
                        cards.size()
                                / (double) columns
                );

        cardsPanel.setLayout(
                new GridLayout(
                        rows,
                        columns,
                        10,
                        10
                )
        );

        for (StatCard card : cards) {
            cardsPanel.add(card);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void rebuildBottomPanel(
            int columns
    ) {
        bottomPanel.removeAll();

        if (columns == 2) {
            bottomPanel.setLayout(
                    new MigLayout(
                            "fillx, insets 0",
                            "[grow, fill][grow, fill]",
                            "[220!]"
                    )
            );

            bottomPanel.add(
                    activityPanel,
                    "grow, height 220!"
            );

            bottomPanel.add(
                    quickActionPanel,
                    "grow, height 220!"
            );

        } else {
            bottomPanel.setLayout(
                    new MigLayout(
                            "fillx, wrap 1, insets 0",
                            "[grow, fill]",
                            "[220!]10[220!]"
                    )
            );

            bottomPanel.add(
                    activityPanel,
                    "growx, height 220!"
            );

            bottomPanel.add(
                    quickActionPanel,
                    "growx, height 220!"
            );
        }

        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    public void loadStatistics() {
        try {
            Statistics statistics =
                    controller.getOverview();

            cards.get(0).setValue(
                    statistics.getTotalUsers()
            );

            cards.get(1).setValue(
                    statistics.getActiveUsers()
            );

            cards.get(2).setValue(
                    statistics.getTotalStudents()
            );

            cards.get(3).setValue(
                    statistics.getTotalTeachers()
            );

            cards.get(4).setValue(
                    statistics.getTotalCourses()
            );

            cards.get(5).setValue(
                    statistics.getTotalClasses()
            );

            revenueChartPanel.loadRevenue();
            activityPanel.refreshActivities();

        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải dữ liệu tổng quan.\n"
                            + exception.getMessage(),
                    "Lỗi database",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}