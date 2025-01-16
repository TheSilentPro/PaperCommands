package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tsp.papercommands.argument.Argument;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * @author TheSilentPro (Silent)
 */
@SuppressWarnings("unchecked")
public interface CommandContext<S> {

    /**
     * The sender of the command.
     *
     * @return The sender
     */
    S sender();

    /**
     * The command this context represents.
     *
     * @return The command
     */
    Command command();

    /**
     * Get an array of raw(string) argument values.
     *
     * @return The arguments
     */
    List<String> rawArgs();

    /**
     * Get a single raw(string) argument value.
     *
     * @param index The position of the argument
     * @return The argument
     */
    Optional<String> rawArg(int index);

    /**
     * Get a single argument.
     *
     * @param index The position of the argument
     * @return The argument
     */
    Argument arg(int index);

    /**
     * Get an optional argument.
     *
     * @param index The position of the argument
     * @return The argument, if present
     */
    Optional<Argument> argOpt(int index);

    /**
     * Get an array of arguments.
     *
     * @return The array of arguments
     */
    Argument[] args();

    /**
     * The prefix used for options.
     * Options are filtered out of the command and can be retrieved via {@link #options()}.
     *
     * @return The prefix
     * @see #options()
     */
    String optionPrefix();

    /**
     * Gets the set of options for this context.
     *
     * @return Options set
     */
    Set<String> options();

    /**
     * Reply to the comman sender with a message.
     *
     * @param message The message
     * @return Context
     */
    CommandContext<S> reply(Component message);

    // Checkers/Validators

    boolean hasPermission(@NotNull String permission);

    boolean isPlayer();

    boolean isConsole();

    boolean isArgument(int index, Class<?> type);

    <U> U validateArgument(int index, Class<U> type, Component failureMessage);

    <U> U validateArgument(int index, Class<U> type, UnaryOperator<Component> failureMessage);

    default <U> U validateArgument(int index, Class<U> type) {
        return validateArgument(index, type, (Component) null);
    }

    // Assertions

    /**
     * Assert an expression.
     *
     * @param assertion Expression
     * @param failureMessage Message if assertion fails
     * @return Context
     */
    CommandContext<S> assertion(boolean assertion, Component failureMessage);

    /**
     * Assert an expression.
     *
     * @param assertion Expression
     * @return Context
     */
    default CommandContext<S> assertion(boolean assertion) {
        return assertion(assertion, null);
    }

    /**
     * Assert that the sender is of a type.
     *
     * @param other The type object
     * @param message The message
     * @return Context
     */
    default CommandContext<S> assertSender(CommandSender other, Component message) {
        return assertion(sender().getClass().isInstance(other), message);
    }

    /**
     * Assert that the sender is of a type.
     *
     * @param other The type object
     * @return Context
     */
    default CommandContext<S> assertSender(CommandSender other) {
        return assertSender(other, null);
    }


    /**
     * Assert that the sender has the provided permission.
     *
     * @param permission Permission
     * @param message Message
     * @return Context
     */
    default CommandContext<S> assertPermission(String permission, Component message) {
        return assertion(hasPermission(permission), message);
    }

    /**
     * Assert that the sender has the provided permission.
     *
     * @param permission Permission
     * @return Context
     */
    default CommandContext<S> assertPermission(String permission) {
        return assertPermission(permission, null);
    }


    /**
     * Assert that the sender is a {@link ConsoleCommandSender}.
     *
     * @param message Message
     * @return Context
     */
    default CommandContext<ConsoleCommandSender> assertConsole(Component message) {
        return (CommandContext<ConsoleCommandSender>) assertion(isConsole(), message);
    }

    /**
     * Assert that the sender is a {@link ConsoleCommandSender}.
     *
     * @return Context
     */
    default CommandContext<ConsoleCommandSender> assertConsole() {
        return assertConsole(null);
    }

    /**
     * Assert that the sender is a {@link Player}.
     *
     * @param message Message
     * @return Context
     */
    default CommandContext<Player> assertPlayer(Component message) {
        return (CommandContext<Player>) assertion(isPlayer(), message);
    }

    /**
     * Assert that the sender is a {@link Player}.
     *
     * @return Context
     */
    default CommandContext<Player> assertPlayer() {
        return assertPlayer(null);
    }

    /**
     * Assert that an argument is of a specific type, otherwise send a message.
     *
     * @param index The argument index
     * @param type The argument type
     * @param message The message to send if type does not match
     * @return Context
     */
    default CommandContext<S> assertArgument(int index, Class<?> type, Component message) {
        return assertion(validateArgument(index, type) != null, message);
    }

    /**
     * Assert that an argument is of a specific type, otherwise send a message.
     *
     * @param index The argument index
     * @param type The argument type
     * @return Context
     */
    default CommandContext<S> assertArgument(int index, Class<?> type) {
        return assertArgument(index, type, null);
    }

}