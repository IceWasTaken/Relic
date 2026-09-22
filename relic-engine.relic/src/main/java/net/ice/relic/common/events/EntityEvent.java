package net.ice.relic.common.events;

import net.ice.heirloom.event.Event;
import net.ice.heirloom.event.EventRegistry;
import net.ice.relic.core.ecs.entity.Entity;

public class EntityEvent {

	public record onEntityCreate(Entity entity) implements Event {

		@Override
		public void register(EventRegistry registry) {

		}

	}
	public record onEntityModify(Entity entity) implements Event {

		@Override
		public void register(EventRegistry registry) {

		}

	}
	public record onEntityDelete(Entity entity) implements Event {

		@Override
		public void register(EventRegistry registry) {

		}

	}

}
