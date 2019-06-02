# Player Respawner

A Spigot/Bukkit plugin for v1.14.2 of Minecraft that automatically
teleports players to the spawn point of the configured destination world
when they log in and are located in any of the worlds in a check list.

## Commands
The command sytem utilizes Aikar's Command Framework to provide
autocompletions and suggestions. Each command can be issued from the
console, or while logged in if the user has the permission
```respawner.admin```

The main command is /player_respawn and has an
alias, /p_resp


| SubCommand | Description | 
|---|---|
| help | Shows the list of sub commands along with their description and syntax |
| show | Shows the current destination world. |
| list or ls | Shows the list of worlds that are checked when a player logs in |
| add <world_to_check> | Adds the specified world to the check list |
| remove <world_to_check> | Removes the specified world from the check list |
| toggle | Activates/Deactivates the plugin |
| destination OR dest <destination_world> | Sets the destination world, where players will be teleported |
| reload | Reloads the configuration from the config.yml for the plugin |

## Permissions
| Permission | Description |
| --- | --- |
| ```respawner.admin``` | Allow access to all of the subcommands above |

## Configuration
| Key | Description |
| --- | --- |
| worlds | The check list of worlds, if a player logs in and is in one of these worlds, they will be teleported to the destination world |
| destination | The destination world, if a player is teleported, then they will be sent to the spawn of this world |
| active | If true, then the plugin will teleport players when the login, if they are in one of the worlds in the check list |

### Default Configuration
```yaml
worlds:
  - world_the_end
  - world_nether
destination: world
active: true
```

## Donations
[StreamLabs](http://streamlabs.com/rabbitsn/v2)