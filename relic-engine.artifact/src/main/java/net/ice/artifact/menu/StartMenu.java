package net.ice.artifact.menu;

import io.qt.widgets.QMainWindow;
import net.ice.artifact.gui.MenuBar;
import net.ice.artifact.widget.Menu;

public class StartMenu {

	private final QMainWindow window;

	public StartMenu() {
		this.window = new QMainWindow();
		this.window.setWindowTitle("Artifact Editor");
		this.window.resize(1280, 720);

		MenuBar menuBar = new MenuBar();
		menuBar.draw(window);

		Menu menu = new Menu("test", "test", window);
		menu.addButton("tes", "Test", this::openProject);





		window.show();
	}

	private void openProject() {
		window.close();

		new ProjectMenu();
	}

}
