package net.ice.relic.common.test.gui;

import imgui.type.ImFloat;
import net.ice.relic.application.RelicApplication;
import net.ice.relic.core.ecs.component.Component;
import net.ice.relic.core.ecs.component.Editable;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.gui.GuiContext;
import net.ice.relic.core.gui.drawable.GuiWindow;
import org.joml.Vector3f;

import java.lang.reflect.Field;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiWindowFlags.*;

public class ECSGui extends GuiWindow {

	public ECSGui(RelicApplication relicApplication) {
		super(new GuiInfo(
				"ECS Viewer",
				NoResize | NoScrollbar | AlwaysAutoResize
		), relicApplication);
	}

	@Override
	protected void draw(GuiContext ctx) {
		tree(ctx);
	}

	private void tree(GuiContext ctx) {
		Entity currEntity = ctx.getCurrentScene().getEntityRoot();
		if(treeNode("rootEntity", "Scene Root")) {
			for(Entity entity : currEntity.getChildren()) {
				drawEntity(entity);
			}
			treePop();
		}
	}

	private void drawEntity(Entity entity) {
		if(treeNode(entity.getName(), entity.getName())) {
			for(Entity child : entity.getChildren()) {
				drawEntity(child);
			}
			for(Component component : entity.getComponents()) {
				if(component != null) {
					drawComponent(entity, component);
				}
			}
			treePop();
		}
	}

	private void drawComponent(Entity entity, Component component) {
		String componentName = component.getClass().getSimpleName();

		if(treeNode(entity.getName() + "." + componentName, componentName)) {
			for(Field field : component.getClass().getDeclaredFields()) {
				if(field.isAnnotationPresent(Editable.class)){
					text(field.getName());
					if(field.getType() == Vector3f.class && field.getName().equals("position")) {
						try {
							editPos(component, field, 0.1f);
						} catch (Exception e) {
							throw new RuntimeException("Failed to set value of field: " + field.getName(), e);
						}
					}
				}
			}
			treePop();
		}
	}

	private boolean changed(Vector3f oldVec, Vector3f newVec) {
		return !oldVec.equals(newVec);
	}

	private void editPos(Component component, Field field, float speed) throws IllegalAccessException {
		field.setAccessible(true);

		Vector3f currPos = (Vector3f) field.get(component);

		boolean changed = false;

		float[] x = new float[]{currPos.x};
		float[] y = new float[]{currPos.y};
		float[] z = new float[]{currPos.z};

		if(dragFloat("x", x, 0.1f)) {
			changed = true;
		}

		if(dragFloat("y", y, 0.1f)) {
			changed = true;
		}

		if(dragFloat("z", z, 0.1f)) {
			changed = true;
		}

		if(changed) {
			field.set(component, new Vector3f(x[0], y[0], z[0]));
			component.update();
		}
	}





}
