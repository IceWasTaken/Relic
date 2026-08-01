package net.ice.relic.core.ecs.component.components.rendering.model;

import net.ice.relic.core.ecs.component.components.rendering.ModelComponent;
import net.ice.relic.core.model.Model;

import java.util.ArrayList;
import java.util.List;

public class StaticModelComponent extends ModelComponent {

	public static final List<StaticModelComponent> COMPONENTS = new ArrayList<>();

	private final Model model;

	public StaticModelComponent(Model model) {
		this.model = model;

		COMPONENTS.add(this);
	}

	public Model getModel() {
		return model;
	}

	public static List<Model> getAllModels() {
		List<Model> models = new ArrayList<>();
		for(StaticModelComponent component : COMPONENTS) {
			models.add(component.getModel());
		}
		return models;
	}

	@Override
	public void update() {

	}
}
