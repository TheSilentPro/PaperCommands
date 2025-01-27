package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * An abstract implementation of a {@link Command}.
 *
 * @author TheSilentPro (Silent)
 */
public class PaperCommand implements Command, CommandExecutor, TabExecutor {

    private static final Pattern USAGE_PATTERN = Pattern.compile(" ");

    @NotNull
    private final String name;
    @Nullable
    private final String usage;
    @Nullable
    private final String permission;
    @Nullable
    private final Component usageMessage;
    @Nullable
    private final Component permissionMessage;
    @Nullable
    private final Consumer<CommandContext<CommandSender>> handler;

    public PaperCommand(@NotNull String name, @Nullable String usage, @Nullable String permission, @Nullable Component usageMessage, @Nullable Component permissionMessage, @Nullable Consumer<CommandContext<CommandSender>> handler) {
        this.name = name;
        this.usage = usage;
        this.permission = permission;
        this.usageMessage = usageMessage;
        this.permissionMessage = permissionMessage;
        this.handler = handler;
    }

    public PaperCommand(@NotNull String name, @Nullable String usage) {
        this(name, usage, null, null, null, null);
    }

    public PaperCommand(@NotNull String name) {
        this(name, null);
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @NotNull
    @Override
    public Optional<String> getUsage() {
        return Optional.ofNullable(usage);
    }

    @NotNull
    @Override
    public Optional<String> getPermission() {
        return Optional.ofNullable(permission);
    }

    public Optional<Component> getUsageMessage(CommandSender sender, String[] args) {
        return Optional.ofNullable(usageMessage);
    }

    public Optional<Component> getPermissionMessage(CommandSender sender, String[] args) {
        return Optional.ofNullable(permissionMessage);
    }

    @Override
    public void handler(CommandContext<CommandSender> ctx) {
        if (handler != null) {
            handler.accept(ctx);
        }
    }

    public List<String> tabHandler(CommandContext<CommandSender> ctx) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, String @NotNull [] args) {
        // Validate the user has permission
        if (permission != null) {
            if (!sender.hasPermission(permission)) {
                getPermissionMessage(sender, args).ifPresent(sender::sendMessage);
                return true;
            }
        }

        // Validate usage format
        if (usage != null) {
            String[] parts = USAGE_PATTERN.split(usage);

            int required = 0;
            for (String part : parts) {
                if (!part.startsWith("[") && !part.endsWith("]")) { // Even if it doesn't have arrow brackets("<>"), assume the argument is required.
                    required++;
                }
            }

            if (args.length < required) {
                getUsageMessage(sender, args).ifPresent(msg -> sender.sendMessage(msg.replaceText(b -> b.matchLiteral("{usage}").replacement("/" + s + " " + usage))));
                return true;
            }
        }

        // Fire command handler, ignore thrown exceptions
        try {
            handler(new PaperCommandContext<>(sender, this, args));
        } catch (CommandAssertionException ex) {
            onAssertionFailure(ex);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return tabHandler(new PaperCommandContext<>(sender, this, args));
    }

    public void onAssertionFailure(CommandAssertionException ex) {}

    @Override
    public PluginCommand register(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand(name);
        if (command == null) {
            throw new NullPointerException("Missing command: " + name);
        }
        command.setExecutor(this);
        command.setTabCompleter(this);
        return command;
    }

}