package net.ice.relic.core.ecs.component.components.rendering;

import net.ice.relic.core.ecs.component.Component;

import java.util.ArrayList;
import java.util.List;

public class RenderableComponent extends Component {

	public static final List<RenderableComponent> COMPONENTS = new ArrayList<>();

	@Override
	public void update() {

	}

	public RenderableComponent() {
		super();
	}
}
