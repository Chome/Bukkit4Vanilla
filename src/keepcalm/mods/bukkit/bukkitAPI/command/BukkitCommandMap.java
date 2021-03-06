package keepcalm.mods.bukkit.bukkitAPI.command;

import net.minecraft.src.ServerCommandManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.MultipleCommandAlias;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.*;
import org.bukkit.permissions.Permission;

import java.util.*;

import keepcalm.mods.bukkit.forgeHandler.commands.CommandExecutor2CommandBase;

import org.bukkit.Server;
import static org.bukkit.util.Java15Compat.Arrays_copyOfRange;
/**
 * Adds net.minecraft.src compatibility to commands, by adding hooks into CommandExecutor2CommandBase.
 * It will need some interesting hackery to make this compatible with some plugins (esp. ones that try and get other plugin's commands manually...)
 * 
 * 
 * {@inheritDoc}
 */
public class BukkitCommandMap extends SimpleCommandMap implements CommandMap  {
    //protected final Map<String, Command> knownCommands = new HashMap<String, Command>();
    //protected final Set<String> aliases = new HashSet<String>();
    protected final Map<String, CommandExecutor2CommandBase> cmdHandlers = new HashMap();
    private final Server server;
    //protected static final Set<VanillaCommand> fallbackCommands = new HashSet<VanillaCommand>();
    // TODO: Register the server commands as fallbackCommands.
    /*static {
        fallbackCommands.add(new ListCommand());
        fallbackCommands.add(new StopCommand());
        fallbackCommands.add(new SaveCommand());
        fallbackCommands.add(new SaveOnCommand());
        fallbackCommands.add(new SaveOffCommand());
        fallbackCommands.add(new OpCommand());
        fallbackCommands.add(new DeopCommand());
        fallbackCommands.add(new BanIpCommand());
        fallbackCommands.add(new PardonIpCommand());
        fallbackCommands.add(new BanCommand());
        fallbackCommands.add(new PardonCommand());
        fallbackCommands.add(new KickCommand());
        fallbackCommands.add(new TeleportCommand());
        fallbackCommands.add(new GiveCommand());
        fallbackCommands.add(new TimeCommand());
        fallbackCommands.add(new SayCommand());
        fallbackCommands.add(new WhitelistCommand());
        fallbackCommands.add(new TellCommand());
        fallbackCommands.add(new MeCommand());
        fallbackCommands.add(new KillCommand());
        fallbackCommands.add(new GameModeCommand());
        fallbackCommands.add(new HelpCommand());
        fallbackCommands.add(new ExpCommand());
        fallbackCommands.add(new ToggleDownfallCommand());
        fallbackCommands.add(new BanListCommand());
        fallbackCommands.add(new DefaultGameModeCommand());
        fallbackCommands.add(new SeedCommand());
    }*/

    public BukkitCommandMap(final Server server) {
    	//super(server);
    	super(server);
        this.server = server;
        setDefaultCommands(server);
    }
    private void setDefaultCommands(final Server server) {
        register("bukkit", new VersionCommand("version"));
        register("bukkit", new ReloadCommand("reload"));
        register("bukkit", new PluginsCommand("plugins"));
        register("bukkit", new TimingsCommand("timings"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAll(String fallbackPrefix, List<Command> commands) {
        if (commands != null) {
            for (Command c : commands) {
                register(fallbackPrefix, c);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(String fallbackPrefix, Command command) {
        return register(command.getName(), fallbackPrefix, command);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(String label, String fallbackPrefix, Command command) {
        boolean registeredPassedLabel = register(label, fallbackPrefix, command, false);

        Iterator<String> iterator = command.getAliases().iterator();
        while (iterator.hasNext()) {
            if (!register((String) iterator.next(), fallbackPrefix, command, true)) {
                iterator.remove();
            }
        }

        // Register to us so further updates of the commands label and aliases are postponed until its reregistered
        command.register(this);

        return registeredPassedLabel;
    }

    /**
     * Registers a command with the given name is possible, otherwise uses fallbackPrefix to create a unique name if its not an alias
     *
     * @param label the name of the command, without the '/'-prefix.
     * @param fallbackPrefix a prefix which is prepended to the command with a ':' one or more times to make the command unique
     * @param command the command to register
     * @return true if command was registered with the passed in label, false otherwise.
     *         If isAlias was true a return of false indicates no command was registerd
     *         If isAlias was false a return of false indicates the fallbackPrefix was used one or more times to create a unique name for the command
     */
    private synchronized boolean register(String label, String fallbackPrefix, Command command, boolean isAlias) {
        String lowerLabel = label.trim().toLowerCase();

        if (isAlias && knownCommands.containsKey(lowerLabel)) {
            // Request is for an alias and it conflicts with a existing command or previous alias ignore it
            // Note: This will mean it gets removed from the commands list of active aliases
            return false;
        }

        String lowerPrefix = fallbackPrefix.trim().toLowerCase();
        boolean registerdPassedLabel = true;

        // If the command exists but is an alias we overwrite it, otherwise we rename it based on the fallbackPrefix
        while (knownCommands.containsKey(lowerLabel) && !aliases.contains(lowerLabel)) {
            lowerLabel = lowerPrefix + ":" + lowerLabel;
            registerdPassedLabel = false;
        }

        if (isAlias) {
            aliases.add(lowerLabel);
        } else {
            // Ensure lowerLabel isn't listed as a alias anymore and update the commands registered name
            aliases.remove(lowerLabel);
            command.setLabel(lowerLabel);
        }
        knownCommands.put(lowerLabel, command);
        

        return registerdPassedLabel;
    }
@Override
    protected Command getFallback(String label) {
        for (VanillaCommand cmd : fallbackCommands) {
            if (cmd.matches(label)) {
                return cmd;
            }
        }

        return null;
    }
	@Override
    public Set<VanillaCommand> getFallbackCommands() {
        return Collections.unmodifiableSet(fallbackCommands);
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public boolean dispatch(CommandSender sender, String commandLine) throws CommandException {
        String[] args = commandLine.split(" ");

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        CommandExecutor2CommandBase target = getNMSCommand(sentCommandLabel);
        
        if (target == null) {
            return false;
        }

        try {
            // Note: we don't return the result of target.execute as thats success / failure, we return handled (true) or not handled (false)
            target.execute(sender, sentCommandLabel, Arrays_copyOfRange(args, 1, args.length));
        } catch (CommandException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new CommandException("Unhandled exception executing '" + commandLine + "' in " + target, ex);
        }

        // return true as command was handled
        return true;
    }
	@Override
    public synchronized void clearCommands() {
        for (Map.Entry<String, Command> entry : knownCommands.entrySet()) {
            entry.getValue().unregister(this);
        }
        knownCommands.clear();
        aliases.clear();
        setDefaultCommands(server);
    }
    /**
     * Returns the (bukkit) form of a command.
     * 
     * @deprecated
     */
    @Deprecated
    @Override
    public Command getCommand(String name) {
        Command target =  knownCommands.get(name.toLowerCase());
        if (target == null) {
            target = getFallback(name);
        }
        return target;
    }

    public CommandExecutor2CommandBase getNMSCommand(String name) {
    	return cmdHandlers.get(name.toLowerCase());
    }
    @Override
    public Collection<Command> getCommands() {
        return knownCommands.values();
    }
	@Override
    public void registerServerAliases() {
        Map<String, String[]> values = server.getCommandAliases();

        for (String alias : values.keySet()) {
            String[] targetNames = values.get(alias);
            List<Command> targets = new ArrayList<Command>();
            StringBuilder bad = new StringBuilder();

            for (String name : targetNames) {
                Command command = getCommand(name);

                if (command == null) {
                    if (bad.length() > 0) {
                        bad.append(", ");
                    }
                    bad.append(name);
                } else {
                    targets.add(command);
                }
            }

            // We register these as commands so they have absolute priority.

            if (targets.size() > 0) {
                knownCommands.put(alias.toLowerCase(), new MultipleCommandAlias(alias.toLowerCase(), targets.toArray(new Command[0])));
            } else {
                knownCommands.remove(alias.toLowerCase());
            }

            if (bad.length() > 0) {
                server.getLogger().warning("The following command(s) could not be aliased under '" + alias + "' because they do not exist: " + bad);
            }
        }
    }

	public void doneLoadingPlugins(ServerCommandManager scm) {
		for (Command i : this.knownCommands.values()) {
			cmdHandlers.put(i.getName().toLowerCase(), new CommandExecutor2CommandBase(i, i.getName()));
		}
		for (CommandExecutor2CommandBase i : this.cmdHandlers.values()) {
			//server.getLogger().info("Registering command: " + i.getCommandName());
			scm.registerCommand(i);
		}
		
	}

	
}
