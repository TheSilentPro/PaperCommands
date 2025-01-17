package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandAssertionException extends RuntimeException {

    public CommandAssertionException(Component message) {
        super(message != null ? PlainTextComponentSerializer.plainText().serialize(message) : "Assertion failed!");
    }

    public CommandAssertionException() {
        super("Command failed an assertion!");
    }

}