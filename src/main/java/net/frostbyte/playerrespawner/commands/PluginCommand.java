package net.frostbyte.playerrespawner.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import net.frostbyte.playerrespawner.Main;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;

@SuppressWarnings("unused")
@Singleton
@CommandAlias("player_respawn|p_resp")
@CommandPermission("respawn.admin")
public class PluginCommand extends BaseCommand
{
	@Inject
	private Main plugin;

	@Inject
	@Named("RespawnerLogger")
	private Logger logger;

	@Subcommand("list|ls")
	@Description("List the worlds that will be checked when players log in.")
	public void listCheckedWorlds(CommandSender sender) {
		plugin.displayCheckedWorlds(sender);
	}

	@Subcommand("show")
	@Description("Show the destination world.")
	public void destinationWorld(CommandSender sender) {
		plugin.showDestination(sender);
	}

	@Subcommand("reload")
	@Description("Reload the plugin's configuration.")
	public void reload(CommandSender sender) {
		sender.sendMessage("Reloading config...");
		plugin.readConfig(true);
	}

	@Subcommand("toggle")
	@Description("Toggles whether the plugin is active or inactive.")
	public void toggle(CommandSender sender) {
		plugin.toggleActive(sender);
	}

	@Subcommand("add")
	@Syntax("<world_to_add>")
	@CommandCompletion("@worlds")
	@Description("Add the specified world to the list of checked worlds.")
	public void addCheckedWorld(CommandSender sender, @Values("@worlds") World world) {
		if (isValidWorld(
			sender,
			world
		)) {
			if (!plugin.addCheckedWorld(world)) {
				sender.sendMessage("Error, could not add that world to the list!");
			}
		}
	}

	@Subcommand("remove")
	@Syntax("<world_to_remove>")
	@CommandCompletion("@worlds")
	@Description("Remove the specified world to the list of checked worlds.")
	public void removeCheckedWorld(CommandSender sender, @Values("@worlds") World world) {
		if (isValidWorld(
			sender,
			world
		)) {
			if (!plugin.removeCheckedWorld(world)) {
				sender.sendMessage("Error, could not remove that world from the list!");
			}
		}
	}

	@Subcommand("dest|destination")
	@Syntax("<destination_world>")
	@CommandCompletion("@worlds")
	@Description("Set the destination world, where players will be sent.")
	public void setDestinationWorld(CommandSender sender, @Values("@worlds") World world) {

		if (isValidWorld(
			sender,
			world
		)) {
			if (!plugin.setDestinationWorld(world)) {
				sender.sendMessage("Error, unable to set that world as the destination!");
			}
		}
	}

	private boolean isValidWorld(CommandSender sender, World world) {
		if (world == null) {
			sender.sendMessage("Invalid World!");
			return false;
		}
		else
			return true;
	}

	/**
	 * Provides Descriptions and Syntax for the command and its subcommands.
	 *
	 * @param sender The player or console who issued the command
	 * @param help   The CommandHelp provided by ACF to generate the help
	 *               descriptions and usages for this command and its subcommands.
	 */
	@SuppressWarnings("unused")
	@HelpCommand
	public void doHelp(CommandSender sender, CommandHelp help)
	{
		help.showHelp();
	}
}
