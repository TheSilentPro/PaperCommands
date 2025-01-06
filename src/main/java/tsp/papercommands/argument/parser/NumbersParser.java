package tsp.papercommands.argument.parser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

/**
 * @author TheSilentPro (Silent)
 */
public final class NumbersParser {

    private NumbersParser() {
        throw new UnsupportedOperationException("Utility class.");
    }

    @NotNull
    public static Optional<Number> parse(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(NumberFormat.getInstance().parse(s));
        } catch (ParseException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Integer> parseInteger(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Long> parseLong(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Float> parseFloat(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Float.parseFloat(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Double> parseDouble(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Double.parseDouble(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public static Optional<Byte> parseByte(@Nullable String s) {
        if (s == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Byte.parseByte(s));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}