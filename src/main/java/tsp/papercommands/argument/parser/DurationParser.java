package tsp.papercommands.argument.parser;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses durations from a string format
 */
public final class DurationParser {

    private DurationParser() {}

    private static final Map<ChronoUnit, String> UNITS_PATTERNS = Map.of(
            ChronoUnit.YEARS, "y(?:ear)?s?",
            ChronoUnit.MONTHS, "mo(?:nth)?s?",
            ChronoUnit.WEEKS, "w(?:eek)?s?",
            ChronoUnit.DAYS, "d(?:ay)?s?",
            ChronoUnit.HOURS, "h(?:our|r)?s?",
            ChronoUnit.MINUTES, "m(?:inute|in)?s?",
            ChronoUnit.SECONDS, "(?:s(?:econd|ec)?s?)?"
    );

    private static final ChronoUnit[] UNITS = UNITS_PATTERNS.keySet().toArray(new ChronoUnit[0]);

    private static final String PATTERN_STRING = UNITS_PATTERNS.values().stream()
            .map(pattern -> "(?:(\\d+)\\s*" + pattern + "[,\\s]*)?")
            .collect(Collectors.joining());

    private static final Pattern PATTERN = Pattern.compile(PATTERN_STRING, Pattern.CASE_INSENSITIVE);

    public static Duration parse(String input) throws IllegalArgumentException {
        Matcher matcher = PATTERN.matcher(input);
        Duration totalDuration = Duration.ZERO;

        while (matcher.find()) {
            if (matcher.group() == null || matcher.group().isEmpty()) {
                continue;
            }

            for (int i = 0; i < UNITS.length; i++) {
                ChronoUnit unit = UNITS[i];
                int g = i + 1;

                if (matcher.group(g) != null && !matcher.group(g).isEmpty()) {
                    int n = Integer.parseInt(matcher.group(g));
                    if (n > 0) {
                        totalDuration = totalDuration.plus(unit.getDuration().multipliedBy(n));
                    }
                }
            }
        }

        if (totalDuration.isZero()) {
            throw new IllegalArgumentException("Unable to parse duration: " + input);
        }

        return totalDuration;
    }


    /**
     * Attempts to parse a {@link Duration} and returns the
     * result as an {@link Optional}-wrapped object.
     *
     * @param input the input string
     * @return an Optional Duration
     */
    @NotNull
    public static Optional<Duration> parseSafely(String input) {
        try {
            return Optional.of(parse(input));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

}