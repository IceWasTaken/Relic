package net.ice.relic.editor;

import net.ice.heirloom.Version;
import net.ice.heirloom.ApplicationProperties;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.editor.scene.DummyScene;

public class RelicEditor extends RelicApplication {

	protected RelicEditor(String[] arguments) {
		super(new ApplicationProperties(
				"Relic Editor",
				new Version(0, 0, 1),
				new Version(0, 5, 1),
				arguments
		));

	}

	public static void main(String[] args) {
		new RelicEditor(args).run();
	}

	@Override
	protected void init(RelicApplication application) {
		loadScene(new DummyScene("DummyScene", application));
	}

	@Override
	protected void update(RelicApplication application) {

	}

	@Override
	protected void render(RelicApplication application) {

	}

	@Override
	protected void cleanup(RelicApplication application) {

	}
}
