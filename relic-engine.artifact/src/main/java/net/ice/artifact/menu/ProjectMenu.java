package net.ice.artifact.menu;

import io.qt.widgets.QMainWindow;
import net.ice.artifact.gui.EntityTree;
import net.ice.artifact.gui.MenuBar;

public class ProjectMenu {

	private QMainWindow window;
	private EntityTree entityTree;
	private MenuBar menuBar;

	public ProjectMenu() {
		this.window = new QMainWindow();
		this.window.setWindowTitle("Artifact Editor");
		this.window.resize(1280, 720);

		this.menuBar = new MenuBar();
		menuBar.draw(window);
		//this.entityTree = new EntityTree(window, );

		window.show();
	}


}
