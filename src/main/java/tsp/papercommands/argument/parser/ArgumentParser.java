package tsp.papercommands.argument.parser;

import org.jetbrains.annotations.NotNull;
import tsp.papercommands.argument.Argument;

import java.util.Optional;

/**
 * Represents a parser for an {@link Argument}.
 * <bold>Note: that parsers should fail when an empty string is provided!</bold>
 *
 * @author TheSilentPro (Silent)
 */
public interface ArgumentParser<T> {

    /**
     * Parses the given string using this parser.
     *
     * @param s The argument string
     * @return The parsed argument, if present
     */
    Optional<T> parse(String s);

    /**
     * Parses the given argument using this parser, otherwise fail.
     *
     * @param s The string argument
     * @return The parsed argument
     */
    @NotNull
    default T parseOrFail(@NotNull String s) throws IllegalArgumentException {
        return parse(s).orElseThrow(() -> new IllegalArgumentException("&cArgument " + s + " is missing."));
    }

    /**
     * Parses the given argument using this parser.
     *
     * @param argument The argument
     * @return The parsed argument, if present
     */
    @NotNull
    default Optional<T> parse(@NotNull Argument argument) {
        return argument.value().flatMap(this::parse);
    }

    /**
     * Parses the given argument using this parser, otherwise fail.
     *
     * @param argument The argument
     * @return The parsed argument
     */
    @NotNull
    default T parseOrFail(@NotNull Argument argument) throws IllegalArgumentException {
        return parseOrFail(argument.value().orElseThrow(() -> new IllegalArgumentException("&cArgument " + argument.index() + " is missing.")));
    }

}