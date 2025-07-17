package net.ice.relic.common.gui;

import net.ice.relic.RelicApplication;

public interface Gui {

    void draw();

    boolean input(RelicApplication relicApplication);
}
