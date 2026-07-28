package view.components;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.Rectangle;

public class ScrollablePanel extends JPanel
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
        return 20;
    }

    @Override
    public int getScrollableBlockIncrement(
            Rectangle visibleRectangle,
            int orientation,
            int direction
    ) {
        return 140;
    }

    /**
     * Nội dung luôn co theo chiều rộng viewport.
     * Không làm mất phần bên phải.
     */
    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    /**
     * Nội dung có thể dài hơn màn hình để cuộn dọc.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
}