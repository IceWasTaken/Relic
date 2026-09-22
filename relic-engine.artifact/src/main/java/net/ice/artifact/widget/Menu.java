package net.ice.artifact.widget;

import io.qt.widgets.QMainWindow;
import io.qt.widgets.QMenu;

import java.awt.*;

public class Menu {

	private QMenu menu;

	public Menu(String title, String objName) {
		this.menu = new QMenu(title);
		this.menu.setObjectName(objName);
	}

	public Menu(String title, String objName, QMainWindow window) {
		this.menu = window.menuBar().addMenu(title);
		this.menu.setObjectName(objName);
		window.menuBar().addMenu(menu);
	}

	public Menu addMenu(Menu menu) {
		this.menu.addMenu(menu.menu);
		return menu;
	}

	public Menu addButton(String text, String name, Runnable onClick) {
		new Button(text, name) {
			@Override
			public void onPress() {
				onClick.run();
			}
		}.draw(menu);

		return this;
	}

	public QMenu getQMenu() {
		return menu;
	}
}
