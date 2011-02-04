package no.ntnu.online.onlineguru.plugin.model;

public interface PluginWithDependencies extends Plugin {
	public String[] getDependencies();
	public void loadDependency(Plugin plugin);
}
