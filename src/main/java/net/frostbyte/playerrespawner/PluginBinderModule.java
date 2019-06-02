package net.frostbyte.playerrespawner;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.util.logging.Logger;

public class PluginBinderModule extends AbstractModule
{
	private Main plugin;
	private Logger logger;
	private BukkitCommandManager commandManager;

	PluginBinderModule(Main plugin, Logger logger, BukkitCommandManager commandManager) {
		this.plugin = plugin;
		this.logger = logger;
		this.commandManager = commandManager;
	}

	/**
	 * Generate the Guice Injector for this Module
	 *
	 * @return The guice injector used to retrieve bound instances and create new instances
	 * based upon the implementations bound to their specified contract class
	 */
	public Injector createInjector()
	{
		return Guice.createInjector(this);
	}

	/**
	 * Configure the Injections and Bindings for our Guice Module
	 * Binds classes to specific instances.
	 * Creates the Configuration Factory and associated implementations
	 */
	@Override protected void configure()
	{
		bind(Main.class).toInstance(plugin);
		bind(BukkitCommandManager.class).toInstance(commandManager);
		bind(Logger.class)
			.annotatedWith(Names.named("RespawnerLogger"))
			.toInstance(logger);
	}
}
