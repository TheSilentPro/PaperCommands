package tsp.papercommands.argument.parser;

import com.google.common.collect.Range;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

/**
 * Represents the main instance for all {@link ArgumentParser parsers}.
 * <bold>Note that parsers should fail when an empty string is provided!</bold>
 *
 * @author TheSilentPro (Silent)
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class ArgumentParsers {

    /**
     * Singleton instance used for accessing the global {@link ArgumentParsers}.
     */
    public static final ArgumentParsers INSTANCE = new ArgumentParsers();

    private final Map<Class<?>, List<ArgumentParser<?>>> parsers = new HashMap<>();

    private ArgumentParsers() {
        register(String.class, Optional::of);
        register(Number.class, NumbersParser::parse);
        register(Integer.class, NumbersParser::parseInteger);
        register(Long.class, NumbersParser::parseLong);
        register(Float.class, NumbersParser::parseFloat);
        register(Double.class, NumbersParser::parseDouble);
        register(Byte.class, NumbersParser::parseByte);
        register(Boolean.class, s -> {
            if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("on")) {
                return Optional.of(true);
            } else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("No") || s.equalsIgnoreCase("off")) {
                return Optional.of(false);
            } else {
                return Optional.empty();
            }
        });
        register(Duration.class, DurationParser::parseSafely);
        register(UUID.class, s -> {
            try {
                return Optional.of(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
        register(NamespacedKey.class, s -> Optional.ofNullable(NamespacedKey.fromString(s)));
        register(Component.class, s -> Optional.of(MiniMessage.miniMessage().deserialize(s)));
        register(Player.class, s -> {
            try {
                return Optional.ofNullable(Bukkit.getPlayer(UUID.fromString(s)));
            } catch (IllegalArgumentException e) {
                return Optional.ofNullable(Bukkit.getPlayer(s));
            }
        });
        register(OfflinePlayer.class, s -> {
            try {
                return Optional.of(Bukkit.getOfflinePlayer(UUID.fromString(s)));
            } catch (IllegalArgumentException e) {
                return Optional.of(Bukkit.getOfflinePlayer(s));
            }
        });
        register(World.class, s -> Optional.ofNullable(Bukkit.getWorld(s)));
        register(Material.class, s -> Optional.ofNullable(Material.matchMaterial(s)));
        register(Enchantment.class, s -> {
            NamespacedKey key = NamespacedKey.fromString(s);
            if (key == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(key));
        });
        register(ItemFlag.class, s -> {
            try {
                return Optional.of(ItemFlag.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        });

        // Attribute parsers
        register(Attribute.class, s -> {
            NamespacedKey key = NamespacedKey.fromString(s);
            if (key == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(Registry.ATTRIBUTE.get(key));
        });
        register(AttributeModifier.Operation.class, s -> {
            try {
                return Optional.of(AttributeModifier.Operation.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        });
        //noinspection UnstableApiUsage
        register(EquipmentSlotGroup.class, s -> {
            try {
                //noinspection UnstableApiUsage
                return Optional.ofNullable(EquipmentSlotGroup.getByName(s.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                return Optional.empty();
            }
        });
        register(AttributeModifier.class, s -> {
            String delimiter = s.contains(",") ? "," : "\\|";
            String[] parts = s.split(delimiter); // Format: namespaced:key,1,add_number,slot OR namespaced:key|1|add_number|slot
            if (parts.length == 4) {
                NamespacedKey key = NamespacedKey.fromString(parts[0]);
                if (key == null) {
                    return Optional.empty();
                }

                double amount = 0;
                try {
                    amount = Integer.parseInt(parts[1]);
                } catch (NumberFormatException nfe) {
                    return Optional.empty();
                }

                AttributeModifier.Operation operation;
                try {
                    operation = AttributeModifier.Operation.valueOf(parts[2].toUpperCase());
                } catch (IllegalArgumentException ex) {
                    return Optional.empty();
                }

                //noinspection UnstableApiUsage
                EquipmentSlotGroup slot = EquipmentSlotGroup.getByName(parts[3]);
                if (slot == null) {
                    return Optional.empty();
                }

                //noinspection UnstableApiUsage
                return Optional.of(new AttributeModifier(key, amount, operation, slot));
            }
            return Optional.empty();
        });

        // Paper
        register(TriState.class, s -> {
            if (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("on")) {
                return Optional.of(TriState.TRUE);
            } else if (s.equalsIgnoreCase("false") || s.equalsIgnoreCase("No") || s.equalsIgnoreCase("off")) {
                return Optional.of(TriState.FALSE);
            } else if (s.equalsIgnoreCase("none") || s.equalsIgnoreCase("null") || s.equalsIgnoreCase("not_set") || s.equalsIgnoreCase("notset") || s.equalsIgnoreCase("unknown") || s.equalsIgnoreCase("reset") || s.equalsIgnoreCase("clear")) {
                return Optional.of(TriState.NOT_SET);
            } else {
                return Optional.empty();
            }
        });
        // guava
        register(Range.class, s -> {
            String[] parts = s.split("-");
            if (parts.length == 2) {
                try {
                    int start = Integer.parseInt(parts[0]);
                    int end = Integer.parseInt(parts[1]);
                    return Optional.of(Range.closed(start, end));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }
            return Optional.empty();
        });
    }

    @NotNull
    public <T> Optional<ArgumentParser<T>> find(@NotNull Class<T> type) {
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of((ArgumentParser<T>) parsers.get(0));
    }

    @NotNull
    public <T> Collection<ArgumentParser<T>> findAll(@NotNull Class<T> type) {
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return List.of();
        }

        return (Collection) Collections.unmodifiableList(parsers);
    }

    /**
     * Register a new {@link ArgumentParser} with the class type.
     *
     * @param type The class type for the parser
     * @param parser The parser
     * @param <T> The type
     */
    public <T> void register(@NotNull Class<T> type, @NotNull ArgumentParser<T> parser) {
        List<ArgumentParser<?>> list = this.parsers.computeIfAbsent(type, t -> new ArrayList<>());
        if (!list.contains(parser)) {
            list.add(parser);
        }
    }

}