package no.ntnu.online.onlineguru.plugin.control;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import no.ntnu.online.onlineguru.plugin.model.Plugin;
import no.ntnu.online.onlineguru.plugin.model.PluginWithDependencies;

public class DependencyManager {

	private HashMap<String, Plugin> loadedPlugins;
	private Node rootNode = new Node();
	
	public DependencyManager(HashMap<String, Plugin> loadedPlugins) {
		this.loadedPlugins = loadedPlugins;
		
		updateDependencies();
		printDependencyTree();
	}
	
	public void updateDependencies() {
		for (Plugin dependantPlugin : loadedPlugins.values()) {
			// dependencies is an array holding all Plugins that "plugin" depends on
			if(dependantPlugin instanceof PluginWithDependencies) {
				PluginWithDependencies plugin = (PluginWithDependencies)dependantPlugin;
				String[] dependencies = plugin.getDependencies();

				/* In the for loop, the dependencies are added to "plugin". 
				 * The graph is being updated so that a Plugin, represented by a Node in the graph,
				 * holds all of it's dependant Plugins, represented by their own nodes, in a HashMap.
				 * When we later start to unload plugins, the unloaded plugin can be retrieved from the 
				 * unloaded plugins children,
				 * and we can loop through them to alert them that one of their dependencies have been unloaded.
				 * This can be done recursively throughout the tree.
				 */ 
				if (dependencies != null) {
					for (String dependency : dependencies) {
						Plugin dependencyPlugin = getPlugin(dependency);
						plugin.loadDependency(dependencyPlugin);

						Node dependencyNode = rootNode.getOrCreateChild(dependencyPlugin);
						dependencyNode.getOrCreateChild(dependantPlugin);	
					}
				}
			}
		}
	}
	
	private Plugin getPlugin(String pluginClassName) {
		pluginClassName = pluginClassName.toUpperCase();
		if(loadedPlugins.containsKey(pluginClassName)) {
			return loadedPlugins.get(pluginClassName);
		}
		else {
			return null;
		}
	}
	
	private void printDependencyTree() {
		
		Queue<Node> nodes = new LinkedList<Node>();
		
		while(!nodes.isEmpty()) {
			
		}
	}
	
	private class Node {
		
//		private boolean enabled = true;
		private HashMap<Plugin, Node> children = new HashMap<Plugin, Node>(); // dependant plugins 
		
		public boolean hasChild(Plugin p) {
			return children.containsKey(p);
		}
		
		public Node getOrCreateChild(Plugin p) {
			if (this.hasChild(p)) {
				return children.get(p);
			}
			else {
				Node newChild = new Node();
				children.put(p, newChild);
				return newChild;
			}
		}
	}
}
