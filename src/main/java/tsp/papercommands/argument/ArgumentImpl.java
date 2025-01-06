package tsp.papercommands.argument;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author TheSilentPro (Silent)
 */
public class ArgumentImpl implements Argument {

    private final int index;
    private final String value;

    public ArgumentImpl(int index, @Nullable String value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public int index() {
        return index;
    }

    @NotNull
    @Override
    public Optional<String> value() {
        return Optional.ofNullable(value);
    }

}