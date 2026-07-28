package view.components;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

public class SidebarPanel extends JPanel
        implements Scrollable {

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(
            Rectangle visibleRectangle,
            int orientation,
            int direction
    ) {
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(
            Rectangle visibleRectangle,
            int orientation,
            int direction
    ) {
        return 100;
    }

    /**
     * Sidebar luôn co theo chiều rộng của viewport.
     * Nhờ vậy không bị cắt logo, menu hoặc tài khoản.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Cho phép sidebar dài hơn chiều cao màn hình
     * và cuộn theo chiều dọc.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}