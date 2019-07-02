package net.frostbyte.playerrespawner;

import co.aikar.commands.BukkitCommandManager;
import com.google.inject.Injector;

import net.frostbyte.playerrespawner.commands.PluginCommand;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * The Player Respawner Plugin
 *
 * @author frost-byte
 */
@SuppressWarnings("unused")
public class Main extends JavaPlugin implements Listener
{
	/**
	 * When a player logs in and is located in a world
	 * that is in this list, then they are teleported to the destination world.
	 */
	private List<String> worldsToCheck;

	/**
	 * The name of the destination world.
	 * Players will be teleported to this world's spawn point, if they login
	 * and are in one of the worlds that is being checked.
	 */
	private String destinationWorld;

	/**
	 * An instance of the destination world
	 */
	private World destination;

	/**
	 * Guice Injector
	 */
	private Injector injector;

	/**
	 * Is the plugin currently active and processing player login events?
	 */
	private boolean isActive = true;

	/**
	 * ACF CommandManager for Bukkit used for
	 * registering in game and console commands, contexts,
	 * completions, and replacements
	 *
	 * @see co.aikar.commands.BukkitCommandManager
	 * @see co.aikar.commands.CommandManager
	 */
	private BukkitCommandManager commandManager;

	/**
	 * Handles the enabling of the bukkit plugin.
	 */
	@Override
	public void onEnable()
	{
		// Initialize the ACF Command Manger
		commandManager = new BukkitCommandManager(this);
		//noinspection deprecation
		commandManager.enableUnstableAPI("help");

		// Create the Guice Bindings.
		createBindings();

		// Create the default config.yml
		this.saveDefaultConfig();

		readConfig(false);

		getServer().getPluginManager().registerEvents(this, this);

		registerCommands();
	}

	/**
	 * Removes the specified world from the list of worlds to be checked
	 *
	 * @param world The world to remove
	 * @return true if the world was removed from the list, false otherwise
	 */
	public boolean removeCheckedWorld(World world) {
		if (!isCheckedWorld(world))
			return false;

		worldsToCheck.remove(world.getName());
		getConfig().set("worlds", worldsToCheck);
		saveConfig();

		return true;
	}

	/**
	 * Adds the specified world to the list of worlds being checked
	 *
	 * @param world The world to add
	 * @return true if the world was added to the list, otherwise false
	 */
	public boolean addCheckedWorld(World world) {
		if (!isDestinationWorld(world) && !isCheckedWorld(world)) {
			worldsToCheck.add(world.getName());
			getConfig().set("worlds", worldsToCheck);
			saveConfig();
			return true;
		}
		return false;
	}

	/**
	 * Process a command sender's request to toggle the plugin's state between active
	 * and inactive.
	 *
	 * @param sender The issuer of the command
	 */
	public void toggleActive(CommandSender sender) {
		String toggled = "Plugin is now " + (isActive ? "deactivated" : "activated") + "!";
		isActive = !isActive;
		getConfig().set("active", isActive);
		saveConfig();
	}

	/**
	 * Process a command sender's request to view the list of
	 * worlds that are checked against the player's world, when they
	 * login.
	 *
	 * @param sender The issuer of the command
	 */
	public void displayCheckedWorlds(CommandSender sender) {
		if (sender == null) return;

		StringBuilder builder = new StringBuilder();
		builder
			.append("Checked Worlds\n")
			.append("--------------\n");

		for (String name : worldsToCheck) {
			builder.append(name).append("\n");
		}
		sender.sendMessage(builder.toString());
	}

	/**
	 * Process a command sender's request to view the destination
	 * world that players are teleported to when they
	 * login and are in one of the worlds in the checked list.
	 *
	 * @param sender The issuer of the command
	 */
	public void showDestination(CommandSender sender) {
		if (sender == null) return;

		sender.sendMessage("Destination: " + destinationWorld);
	}

	/**
	 * Determines if the specified world is in the list of checked worlds.
	 *
	 * @param world The world to check
	 * @return true if the world is in the list of worlds to check.
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private boolean isCheckedWorld(World world)
	{
		return world != null &&
			   worldsToCheck != null &&
			   !worldsToCheck.isEmpty() &&
			   worldsToCheck.contains(world.getName());
	}

	/**
	 * Determines if the specified world is the current destination world.
	 *
	 * @param world The World to compare with the destination world.
	 * @return true if the world is the same as the destination world.
	 */
	private boolean isDestinationWorld(World world) {
		return (world != null && world.getName().equalsIgnoreCase(destinationWorld));
	}

	/**
	 * Sets the destination world
	 *
	 * @param world The world where players will be teleported, if they log on while located
	 *              in one of the worlds in the checked list.
	 *
	 * @return true if the destination world was updated.
	 */
	public boolean setDestinationWorld(World world) {
		if (isDestinationWorld(world))
			return false;

		if (world != null) {
			destination = world;
			destinationWorld = world.getName();
			getConfig().set("destination", destinationWorld);
			saveConfig();
			return true;
		}

		return false;
	}

	@SuppressWarnings("SameParameterValue")
	public void readConfig(boolean doReload) {
		if (doReload)
			reloadConfig();

		isActive = getConfig().getBoolean("active");
		worldsToCheck = getConfig().getStringList("worlds");
		destinationWorld = getConfig().getString("destination");

		// Show the current properties from the config.
		if (worldsToCheck == null || worldsToCheck.isEmpty())
			getLogger().info("Could not find the list of worlds to check!");
		else {
			getLogger().info("Worlds to Check");
			getLogger().info("----------------");
			worldsToCheck.forEach(getLogger()::info);
		}

		if (destinationWorld != null)
		{
			getLogger().info("Destination World (" + destinationWorld + ")");
			destination = getServer().getWorld(destinationWorld);

			if (destination != null)
				getLogger().info("Destination World Found!");
			else
				getLogger().warning("Error! Could not find Destination World!");
		}
	}

	/**
	 * Retrieve the destination world
	 *
	 * @return The world where players will be teleported to when logging in at
	 * a location in one of the checked worlds.
	 */
	World getDestinationWorld() {
		return destination;
	}

	/**
	 * Checks the Players current world versus the configured list of worlds to check.
	 * If the player logs in and is in one of those worlds, then they're teleported to the
	 * spawn of the configured destination world.
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void handlePlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (!worldsToCheck.isEmpty() && destination != null)
		{
			String playerWorld = player.getWorld().getName();

			for (String world : worldsToCheck)
			{
				if (playerWorld.equalsIgnoreCase(world))
				{
					player.teleport(
						destination.getSpawnLocation(),
						PlayerTeleportEvent.TeleportCause.PLUGIN
					);
					break;
				}
			}
		}
	}

	/**
	 * Generates the Guice Bindings for the Plugin's Module
	 */
	private void createBindings() {
		getLogger().info("Creating Bindings...");
		PluginBinderModule module = new PluginBinderModule(this, getLogger(), commandManager);
		injector = module.createInjector();
		injector.injectMembers(this);
	}

	/**
	 * Registers all the Commands for the plugin with Aikar's Command Framework
	 */
	private void registerCommands() {
		getLogger().info("Registering Commands...");
		ACFSetup setup = injector.getInstance(ACFSetup.class);
		setup.register();
		setup.registerCommand(injector.getInstance(PluginCommand.class));
	}
}
