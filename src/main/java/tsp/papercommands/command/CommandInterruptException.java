package tsp.papercommands.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * @author TheSilentPro (Silent)
 */
public class CommandInterruptException extends RuntimeException {

    public CommandInterruptException(Component message) {
        super(PlainTextComponentSerializer.plainText().serialize(message));
    }

    public CommandInterruptException() {
        super("Command failed an assertion!");
    }

}