package net.ice.artifact.gui;

import io.qt.widgets.*;
import net.ice.heirloom.event.EventListener;
import net.ice.relic.common.events.EntityEvent;
import net.ice.relic.core.ecs.entity.Entity;
import net.ice.relic.core.scene.Scene;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityTree extends QTreeWidget {

	private final Map<Entity, QTreeWidgetItem> items = new ConcurrentHashMap<>();
	private final QTreeWidgetItem root;

	private final Scene scene;

	public EntityTree(QWidget parent, Scene scene) {
		super(parent);

		this.scene = scene;

		this.root = new QTreeWidgetItem(this);
		this.root.setText(0, "Root");
		this.items.put(scene.getEntityRoot(), root);

		this.setColumnCount(1);
		this.setHeaderLabel("Scene");
	}

	@EventListener
	public void onEntityCreate(EntityEvent.onEntityCreate event) {
		Entity entity = event.entity();
		Entity entityParent = entity.getParent().orElse(scene.getEntityRoot());

		QTreeWidgetItem qTreeWidgetItem = new QTreeWidgetItem(items.get(entityParent));
		qTreeWidgetItem.setText(0, entity.getName());

		items.put(event.entity(), qTreeWidgetItem);
		for(Entity entityChild : entity.getChildren()){
			if(!items.containsKey(entityChild)){
				onEntityCreate(new EntityEvent.onEntityCreate(entityChild));
			}
		}

	}
}
