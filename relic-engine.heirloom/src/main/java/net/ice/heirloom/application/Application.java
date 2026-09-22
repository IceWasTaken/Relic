package net.ice.heirloom.application;

public abstract class Application {

	protected final ApplicationProperties properties;

	protected abstract void initApplication(Application application);
	protected abstract void updateApplication(Application application);
	protected abstract void cleanupApplication(Application application);

	protected Application(ApplicationProperties properties) {
		this.properties = properties;
	}

	public ApplicationProperties getProperties() {
		return properties;
	}
}
