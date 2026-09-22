package net.ice.artifact;

import io.qt.widgets.QApplication;
import io.qt.widgets.QMainWindow;
import net.ice.artifact.menu.StartMenu;
import net.ice.heirloom.Version;
import net.ice.heirloom.application.Application;
import net.ice.heirloom.application.ApplicationProperties;

public class Artifact extends Application {

	public Artifact(String[] args) {
		super(new ApplicationProperties(
				"Artifact",
				new Version(0, 0, 1),
				new Version(0, 0, 0),
				args
		));
	}

	static void main(String[] args) {
		Artifact artifact = new Artifact(args);
		artifact.initApplication(artifact);
	}

	@Override
	protected void initApplication(Application application) {
		QApplication.initialize(properties.arguments());

		new StartMenu();

		QApplication.exec();
		QApplication.shutdown();
	}

	@Override
	protected void updateApplication(Application application) {
	}

	@Override
	protected void cleanupApplication(Application application) {

	}
}
