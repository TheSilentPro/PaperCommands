package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tsp.papercommands.argument.Argument;
import tsp.papercommands.argument.ArgumentImpl;
import tsp.papercommands.argument.parser.ArgumentParsers;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Implementation of {@link CommandContext}.
 *
 * @author TheSilentPro (Silent)
 */
public class PaperCommandContext<T extends CommandSender> implements CommandContext<T> {

    private final T sender;
    private final List<String> arguments;
    private final Set<String> options;
    private final Command command;

    public PaperCommandContext(T sender, Command command, String[] args, Set<String> options) {
        this.sender = sender;
        this.arguments = new ArrayList<>(Arrays.asList(args)); // Mutable
        this.options = options;
        this.command = command;
    }

    public PaperCommandContext(T sender, Command command, String[] args) {
        this.sender = sender;
        this.arguments = new ArrayList<>(Arrays.asList(args)); // Mutable
        this.options = new HashSet<>();
        this.command = command;

        // Loop raw arguments to match options and remove them from the arguments list.
        for (Iterator<String> iterator = arguments.iterator(); iterator.hasNext();) {
            String arg = iterator.next();
            if (arg.startsWith(optionPrefix())) {
                this.options.add(arg.substring(optionPrefix().length()));
                iterator.remove();
            }
        }
    }

    @Override
    public T sender() {
        return this.sender;
    }

    @Override
    public Command command() {
        return this.command;
    }

    @Override
    public Set<String> options() {
        return Collections.unmodifiableSet(this.options);
    }

    @Override
    public CommandContext<T> reply(Component message) {
        this.sender.sendMessage(message);
        return this;
    }

    @Override
    public List<String> rawArgs() {
        return Collections.unmodifiableList(this.arguments);
    }

    @Override
    public Optional<String> rawArg(int index) {
        if (index < 0 || index >= this.arguments.size()) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.arguments.get(index));
    }

    @Override
    public Argument arg(int index) {
        return new ArgumentImpl(index, rawArg(index).orElse(null));
    }

    @Override
    public Optional<Argument> argOpt(int index) {
        return rawArg(index).map(arg -> new ArgumentImpl(index, arg));
    }

    @Override
    public Argument[] args() {
        Argument[] args = new Argument[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++) {
            args[i] = arg(i);
        }
        return args;
    }

    @Override
    public String optionPrefix() {
        return "-";
    }

    @Override
    public CommandContext<T> assertion(boolean assertion, @Nullable Component failureMessage) {
        if (assertion) {
            return this;
        } else {
            if (failureMessage != null) reply(failureMessage);
            throw new CommandAssertionException(failureMessage);
        }
    }


    @Override
    public boolean hasPermission(@Nonnull String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean isPlayer() {
        return sender instanceof Player;
    }

    @Override
    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }

    @Override
    public boolean isArgument(int index, @NotNull Class<?> type) {
        return ArgumentParsers.INSTANCE.find(type).orElseThrow(() -> new NoSuchElementException("Unable to find ArgumentParser for " + type)).parse(rawArg(index).orElse("")).isPresent();
    }

    @Override
    public <U> U validateArgument(int index, @NotNull Class<U> type, @Nullable Component failureMessage) {
        Optional<U> result = ArgumentParsers.INSTANCE.find(type).orElseThrow(() -> new NoSuchElementException("Unable to find ArgumentParser for " + type)).parse(rawArg(index).orElse(""));
        if (result.isPresent()) {
            return result.get();
        } else {
            if (failureMessage != null) reply(failureMessage);
            throw new CommandAssertionException(failureMessage);
        }
    }

    @Override
    public <U> U validateArgument(int index, Class<U> type, UnaryOperator<Component> failureMessage) {
        Optional<U> result = ArgumentParsers.INSTANCE.find(type).orElseThrow(() -> new NoSuchElementException("Unable to find ArgumentParser for " + type)).parse(rawArg(index).orElse(""));
        if (result.isPresent()) {
            return result.get();
        } else {
            if (failureMessage != null) {
                Optional<String> raw = rawArg(index);
                if (raw.isPresent()) {
                    Component arg = failureMessage.apply(Component.text(raw.orElse(null)));
                    if (arg != null) {
                        reply(arg);
                    }
                }
            }
            throw new CommandAssertionException();
        }
    }

}