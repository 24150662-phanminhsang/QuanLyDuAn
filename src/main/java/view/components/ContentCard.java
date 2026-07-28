package view.components;

import util.UIConstants;

import javax.swing.JPanel;

public class ContentCard extends JPanel {

    public ContentCard() {
        setBackground(UIConstants.CARD_BACKGROUND);

        putClientProperty(
                "FlatLaf.style",
                """
                arc: 18;
                borderWidth: 0;
                """
        );
    }
}