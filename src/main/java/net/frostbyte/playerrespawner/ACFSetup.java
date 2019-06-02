package net.frostbyte.playerrespawner;

import co.aikar.commands.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.bukkit.World;

import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
class ACFSetup
{
	private final BukkitCommandManager commandManager;
	private final Logger logger;

	@SuppressWarnings( { "FieldCanBeLocal", "unused" })
	private final Main plugin;

	@Inject
	public ACFSetup(
		BukkitCommandManager commandManager,
		@Named("RespawnerLogger") Logger logger,
		Main plugin
	) {
		this.commandManager = commandManager;
		this.logger = logger;
		this.plugin = plugin;
	}

	public void register() {
		logger.info("ACF Setup: Registering Commands...");
		registerCommandCompletions();
		registerCommandContexts();
	}

	/**
	 * Defines Completions which can be referenced by any Command registered with ACF,
	 * which allows the user to use the tab key to provide suggestions or complete
	 * partially typed input
	 */
	private void registerCommandCompletions()
	{
		logger.info("ACF Setup: Registering command completions");

		CommandCompletions<BukkitCommandCompletionContext> comp = commandManager.getCommandCompletions();

		comp.registerCompletion(
			"worlds",
			c -> plugin.getServer().getWorlds().stream().map(World::getName).collect(Collectors.toList())
		);
	}

	/**
	 * Provides ACF with contexts for converting string input to commands into
	 * object instances, based upon various annotations, Flags, for example.
	 * Compares the parameters defined for a command and converts the input strings
	 * provided by the command issuer into a relevant object instance.
	 * <p>
	 * Specifying an IssuerAware context allows the CommandSender or initiator of the
	 * command to be treated as an instance of a specific object.
	 */
	private void registerCommandContexts()
	{
		logger.info("ACF Setup: Registering command contexts");

		CommandContexts<BukkitCommandExecutionContext> con = commandManager.getCommandContexts();

		con.registerContext(
			World.class,
			c -> {
				String worldName = c.popFirstArg();
				logger.info("ACFSetup.registerContext.World: name: " + worldName);
				if (worldName == null || worldName.isEmpty())
				{
					throw new InvalidCommandArgument("Invalid World Name!");
				}

				for(World w : plugin.getServer().getWorlds()) {
					if (w.getName().equalsIgnoreCase(worldName)) {
						return w;
					}
				}

				throw new InvalidCommandArgument("World not Found!");
			}
		);
	}

	public void registerCommand(BaseCommand command) {
		if (command == null)
			return;
		logger.info("Registering " + command.getName() + " command...");
		commandManager.registerCommand(command);
	}
}
