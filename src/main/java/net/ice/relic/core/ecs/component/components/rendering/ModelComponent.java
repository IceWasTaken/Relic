package net.ice.relic.core.ecs.component.components.rendering;

import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.model.Model;

public class ModelComponent extends Component {

	private Model model;

	public ModelComponent(Model model) {
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
