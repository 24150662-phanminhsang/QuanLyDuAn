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

public class StatisticsView extends ScrollablePanel {

    private final StatisticsController controller;

    private final JPanel cardsPanel;
    private final JPanel bottomPanel;

    private final List<StatCard> cards;

    private final RevenueChartPanel revenueChartPanel;
    private final ActivityPanel activityPanel;
    private final QuickActionPanel quickActionPanel;

    private int currentCardColumns = -1;
    private int currentBottomColumns = -1;

    private boolean loading;

    public StatisticsView(
            NotificationService notificationService
    ) {
        controller = new StatisticsController();

        cardsPanel = new JPanel();
        cardsPanel.setOpaque(false);

        bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        StatCard usersCard = new StatCard(
                "Tổng tài khoản",
                "Người dùng trong hệ thống",
                FontAwesomeSolid.USERS,
                UIConstants.PRIMARY,
                UIConstants.PRIMARY_LIGHT
        );

        StatCard activeCard = new StatCard(
                "Đang hoạt động",
                "Tài khoản ACTIVE",
                FontAwesomeSolid.USER_CHECK,
                UIConstants.SUCCESS,
                UIConstants.SUCCESS_LIGHT
        );

        StatCard studentsCard = new StatCard(
                "Sinh viên",
                "Tổng số sinh viên",
                FontAwesomeSolid.USER_GRADUATE,
                UIConstants.PURPLE,
                UIConstants.PURPLE_LIGHT
        );

        StatCard teachersCard = new StatCard(
                "Giảng viên",
                "Tổng số giảng viên",
                FontAwesomeSolid.CHALKBOARD_TEACHER,
                UIConstants.WARNING,
                UIConstants.WARNING_LIGHT
        );

        StatCard coursesCard = new StatCard(
                "Khóa học",
                "Tổng số khóa học",
                FontAwesomeSolid.BOOK_OPEN,
                UIConstants.PRIMARY,
                UIConstants.PRIMARY_LIGHT
        );

        StatCard classesCard = new StatCard(
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

        revenueChartPanel = new RevenueChartPanel();

        activityPanel = new ActivityPanel(
                notificationService
        );

        quickActionPanel = new QuickActionPanel();

        initializeView();
        registerResponsiveListener();

        /*
         * Dựng layout ngay sau khi component đã có kích thước.
         */
        SwingUtilities.invokeLater(() -> {
            rebuildResponsiveLayout();
            loadStatistics();
        });
    }

    private void initializeView() {
        setBackground(UIConstants.BACKGROUND);

        setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 16",
                        "[grow, fill]",
                        "[]14[]14[]"
                )
        );

        add(
                cardsPanel,
                "growx"
        );

        add(
                revenueChartPanel,
                "growx, height 300:320:350"
        );

        add(
                bottomPanel,
                "growx"
        );
    }

    private void registerResponsiveListener() {
        addComponentListener(
                new ComponentAdapter() {
                    @Override
                    public void componentResized(
                            ComponentEvent event
                    ) {
                        rebuildResponsiveLayout();
                    }

                    @Override
                    public void componentShown(
                            ComponentEvent event
                    ) {
                        rebuildResponsiveLayout();
                    }
                }
        );
    }

    public void setQuickActionHandler(
            Consumer<String> handler
    ) {
        quickActionPanel.setActionHandler(
                handler
        );
    }

    public void refreshActivities() {
        activityPanel.refreshActivities();
    }

    private void rebuildResponsiveLayout() {
        int width = getWidth();

        if (width <= 0) {
            return;
        }

        int cardColumns = calculateCardColumns(width);
        int bottomColumns = width >= 820 ? 2 : 1;

        if (cardColumns != currentCardColumns) {
            rebuildCards(cardColumns);
            currentCardColumns = cardColumns;
        }

        if (bottomColumns != currentBottomColumns) {
            rebuildBottomPanel(bottomColumns);
            currentBottomColumns = bottomColumns;
        }

        updateActivityLimit();
    }

    private int calculateCardColumns(
            int width
    ) {
        /*
         * Sau khi trừ sidebar, chiều rộng thực tế thường nhỏ hơn
         * kích thước JFrame. Vì vậy mốc 1120 phù hợp hơn 1350.
         */
        if (width >= 1120) {
            return 6;
        }

        if (width >= 760) {
            return 3;
        }

        if (width >= 500) {
            return 2;
        }

        return 1;
    }

    private void updateActivityLimit() {
        int visibleHeight = getVisibleRect().height;

        if (visibleHeight >= 760) {
            activityPanel.setVisibleLimit(5);
        } else if (visibleHeight >= 620) {
            activityPanel.setVisibleLimit(4);
        } else {
            activityPanel.setVisibleLimit(3);
        }
    }

    private void rebuildCards(
            int columns
    ) {
        cardsPanel.removeAll();

        int rows = (int) Math.ceil(
                cards.size() / (double) columns
        );

        cardsPanel.setLayout(
                new GridLayout(
                        rows,
                        columns,
                        12,
                        12
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
            /*
             * Hoạt động gần đây rộng hơn phần thao tác nhanh,
             * gần với bố cục Dashboard mẫu.
             */
            bottomPanel.setLayout(
                    new MigLayout(
                            "fillx, insets 0",
                            "[grow 60, fill]12[grow 40, fill]",
                            "[230!]"
                    )
            );

            bottomPanel.add(
                    activityPanel,
                    "grow, height 230!"
            );

            bottomPanel.add(
                    quickActionPanel,
                    "grow, height 230!"
            );

        } else {
            bottomPanel.setLayout(
                    new MigLayout(
                            "fillx, wrap 1, insets 0",
                            "[grow, fill]",
                            "[230!]12[230!]"
                    )
            );

            bottomPanel.add(
                    activityPanel,
                    "growx, height 230!"
            );

            bottomPanel.add(
                    quickActionPanel,
                    "growx, height 230!"
            );
        }

        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    public void loadStatistics() {
        if (loading) {
            return;
        }

        loading = true;

        try {
            Statistics statistics = controller.getOverview();

            if (statistics == null) {
                throw new SQLException(
                        "Không nhận được dữ liệu thống kê."
                );
            }

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

        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Đã xảy ra lỗi khi tải trang tổng quan.\n"
                            + exception.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            loading = false;
        }
    }
}