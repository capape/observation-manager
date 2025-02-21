package de.lehmannet.om.ui.theme;

import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

public class NightVisionTheme extends DefaultMetalTheme {

    // Red shades
    // Active internal window borders
    private final ColorUIResource primary1 = new ColorUIResource(170, 30, 30);
    // Highlighting to indicate activation (for example, of menu titles and menu
    // items); indication of keyboard focus
    private final ColorUIResource primary2 = new ColorUIResource(195, 34, 34);
    // Large colored areas (for example, the active title bar)
    private final ColorUIResource primary3 = new ColorUIResource(255, 45, 45);
    private final ColorUIResource secondary1 = new ColorUIResource(92, 50, 50);
    // Inactive internal window borders; dimmed button borders
    private final ColorUIResource secondary2 = new ColorUIResource(124, 68, 68);
    // Canvas color (that is, normal background color); inactive title bar
    private final ColorUIResource secondary3 = new ColorUIResource(181, 99, 99);
    private final ColorUIResource white = new ColorUIResource(255, 175, 175);

    @Override
    public String getName() {

        return "Night Vision";
    }

    @Override
    protected ColorUIResource getPrimary1() {

        return primary1;
    }

    @Override
    protected ColorUIResource getPrimary2() {

        return primary2;
    }

    @Override
    protected ColorUIResource getPrimary3() {

        return primary3;
    }

    @Override
    protected ColorUIResource getSecondary1() {
        return secondary1;
    }

    @Override
    protected ColorUIResource getSecondary2() {
        return secondary2;
    }

    @Override
    protected ColorUIResource getSecondary3() {
        return secondary3;
    }

    @Override
    protected ColorUIResource getWhite() {
        return white;
    }
}
