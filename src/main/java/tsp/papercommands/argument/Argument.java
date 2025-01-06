package tsp.papercommands.argument;

import org.jetbrains.annotations.NotNull;
import tsp.papercommands.argument.parser.ArgumentParser;
import tsp.papercommands.argument.parser.ArgumentParsers;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents an argument in a command.
 *
 * @author TheSilentPro (Silent)
 */
public interface Argument {

    /**
     * The index(position) of this argument in the command.
     *
     * @return The number position
     */
    int index();

    /**
     * The value of this argument if present.
     *
     * @return The value, if present
     */
    Optional<String> value();

    /**
     * Parse this argument using the provided {@link ArgumentParser}.
     *
     * @param parser The parser to use for parsing
     * @return The parsed argument, if present
     * @param <T> The type of argument
     */
    default <T> Optional<T> parse(@NotNull ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    /**
     * Parse this argument using the provided {@link ArgumentParser}, otherwise fail.
     *
     * @param parser The parser to use for parsing
     * @return The parsed argument, if present
     * @param <T> The type of argument
     */
    default <T> T parseOrFail(@NotNull ArgumentParser<T> parser) throws NoSuchElementException {
        return parser.parseOrFail(this);
    }

    /**
     * Parse this argument using the provided {@link ArgumentParser}.
     *
     * @param clazz The type of the parser
     * @return The parsed argument, if present
     * @param <T> The type of argument
     */
    default <T> Optional<T> parse(@NotNull Class<T> clazz) {
        return ArgumentParsers.INSTANCE.find(clazz).flatMap(this::parse);
    }

    /**
     * Parse this argument using the provided {@link ArgumentParser}, otherwise fail.
     *
     * @param clazz The type of the parser
     * @return The parsed argument, if present
     * @param <T> The type of argument
     */
    default <T> T parseOrFail(@NotNull Class<T> clazz) throws NoSuchElementException {
        return parseOrFail(ArgumentParsers.INSTANCE.find(clazz).orElseThrow(() -> new NoSuchElementException("Unable to find ArgumentParser for " + clazz)));
    }

}