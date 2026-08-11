package net.ice.relic.core.ecs.component.components.rendering.model;

import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.model.Model;

public class StaticModelComponent extends ModelComponent {

	private Model model;

	public StaticModelComponent(Model model) {
		this.model = model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public void update() {

	}
}
