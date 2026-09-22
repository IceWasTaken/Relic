package net.ice.artifact.gui;

import io.qt.widgets.*;
import net.ice.artifact.menu.NewProjectMenu;
import net.ice.artifact.widget.Menu;

import java.awt.*;
import java.io.*;

//file, edit, view, etc
public class MenuBar implements IQtGui {

	@Override
	public void draw(QMainWindow window) {
		fileMenu(window);

		QMenu editMenu = window.menuBar().addMenu("Edit");
		QMenu viewMenu = window.menuBar().addMenu("View");
		QMenu helpMenu = window.menuBar().addMenu("Help");
	}

	private void fileMenu(QMainWindow window) {
		Menu fileMenu = new Menu("File", "file", window);

		Menu fileNewMenu = fileMenu.addMenu(new Menu("New", "new"))
				.addButton("Project...", "file-new-project", () -> new NewProjectMenu(window).exec())
				.addButton("Project from VCS...", "file-new-project-from-vcs", () -> {});

		fileMenu.addButton("Open...", "file-open", () -> {
			QFileDialog.Result<String> result = QFileDialog.getOpenFileName(
					window,
					"Select a File",
					System.getProperty("user.home"),
					"Text Files (*.txt);;All Files (*.*)"
			);

			if (result.result != null) {
				String selectedFilePath = result.result;
				System.out.println("Selected file: " + selectedFilePath);
			}
		});

		Menu fileRecentProjectsMenu = fileMenu.addMenu(new Menu("Recent Projects", "file-recent-projects"));
		//todo: save list of recent projects, and list them here

		fileMenu.addButton("Close Project", "file-close-project", () -> {});

		fileMenu.getQMenu().addSeparator();

		fileMenu
				.addButton("Settings...", "file-settings", () -> {})
				.addButton("Exit", "file-exit", window::close);

	}




}
