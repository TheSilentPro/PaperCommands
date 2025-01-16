package tsp.papercommands.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

/**
 * @author TheSilentPro (Silent)
 */
public interface Command {

    /**
     * The name.
     *
     * @return Name
     */
    String getName();

    /**
     * The usage format.
     *
     * @return Usage
     */
    Optional<String> getUsage();

    /**
     * The permission required to execute this command.
     *
     * @return The permission
     */
    Optional<String> getPermission();

    /**
     * The handler.
     *
     * @param ctx The {@link CommandContext}
     */
    void handler(CommandContext<CommandSender> ctx);

    /**
     * The tab handler
     *
     * @param ctx The {@link CommandContext}
     */
    List<String> tabHandler(CommandContext<CommandSender> ctx);

    /**
     * Register to the provided plugin.
     *
     * @param plugin The owning plugin.
     */
    void register(JavaPlugin plugin);

}