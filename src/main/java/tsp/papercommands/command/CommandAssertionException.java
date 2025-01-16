package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandAssertionException extends RuntimeException {

    public CommandAssertionException(Component message) {
        super(PlainTextComponentSerializer.plainText().serialize(message));
    }

    public CommandAssertionException() {
        super("Command failed an assertion!");
    }

}