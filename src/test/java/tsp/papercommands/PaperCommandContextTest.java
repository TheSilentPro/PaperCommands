package tsp.papercommands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tsp.papercommands.command.Command;
import tsp.papercommands.command.PaperCommandContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PaperCommandContextTest {

    private CommandSender mockSender;
    private Command mockCommand;

    @BeforeEach
    void setUp() {
        mockSender = Mockito.mock(CommandSender.class);
        mockCommand = Mockito.mock(Command.class);
    }

    @Test
    void testOptionsAreExtracted() {
        // Arrange: raw arguments with options prefixed by '-'
        String[] args = {"minecraft:max_health", "testkey:even_more_health", "-override", "-silent"};

        // Act: create the context
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args);

        // Assert: options are extracted correctly
        Set<String> options = context.options();
        assertEquals(2, options.size(), "Expected 2 options");
        assertTrue(options.contains("override"), "Expected 'override' option");
        assertTrue(options.contains("silent"), "Expected 'silent' option");

        // Assert: remaining arguments
        List<String> rawArgs = context.rawArgs();
        assertEquals(2, rawArgs.size(), "Expected 2 remaining arguments");
        assertEquals("minecraft:max_health", rawArgs.get(0));
        assertEquals("testkey:even_more_health", rawArgs.get(1));
    }

    @Test
    void testNoOptions() {
        // Arrange: raw arguments without options
        String[] args = {"minecraft:max_health", "testkey:even_more_health"};

        // Act: create the context
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args);

        // Assert: no options
        Set<String> options = context.options();
        assertTrue(options.isEmpty(), "Expected no options");

        // Assert: all arguments remain
        List<String> rawArgs = context.rawArgs();
        assertEquals(2, rawArgs.size(), "Expected 2 arguments");
        assertEquals("minecraft:max_health", rawArgs.get(0));
        assertEquals("testkey:even_more_health", rawArgs.get(1));
    }

    @Test
    void testMixedArgumentsAndOptions() {
        // Arrange: a mix of valid options and regular arguments
        String[] args = {"-override", "minecraft:max_health", "-silent", "testkey:even_more_health"};

        // Act: create the context
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args);

        // Assert: options are extracted correctly
        Set<String> options = context.options();
        assertEquals(2, options.size(), "Expected 2 options");
        assertTrue(options.contains("override"), "Expected 'override' option");
        assertTrue(options.contains("silent"), "Expected 'silent' option");

        // Assert: remaining arguments
        List<String> rawArgs = context.rawArgs();
        assertEquals(2, rawArgs.size(), "Expected 2 remaining arguments");
        assertEquals("minecraft:max_health", rawArgs.get(0));
        assertEquals("testkey:even_more_health", rawArgs.get(1));
    }

    @Test
    void testOptionWithDifferentPrefix() {
        // Arrange: a raw argument with a custom prefix
        String[] args = {"--override", "minecraft:max_health"};

        // Act: override the prefix method and test
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args) {
            @Override
            public String optionPrefix() {
                return "--";
            }
        };

        // Assert: options are extracted correctly
        Set<String> options = context.options();
        assertEquals(1, options.size(), "Expected 1 option");
        assertTrue(options.contains("override"), "Expected 'override' option");

        // Assert: remaining arguments
        List<String> rawArgs = context.rawArgs();
        assertEquals(1, rawArgs.size(), "Expected 1 remaining argument");
        assertEquals("minecraft:max_health", rawArgs.get(0));
    }

    @Test
    void testEmptyArguments() {
        // Arrange: empty input
        String[] args = {};

        // Act: create the context
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args);

        // Assert: no options
        Set<String> options = context.options();
        assertTrue(options.isEmpty(), "Expected no options");

        // Assert: no arguments
        List<String> rawArgs = context.rawArgs();
        assertTrue(rawArgs.isEmpty(), "Expected no arguments");
    }

    @Test
    void testRawArgAccess() {
        // Arrange: raw arguments
        String[] args = {"minecraft:max_health", "testkey:even_more_health"};

        // Act: create the context
        PaperCommandContext<CommandSender> context = new PaperCommandContext<>(mockSender, mockCommand, args);

        // Assert: accessing valid arguments
        assertTrue(context.rawArg(0).isPresent(), "Expected argument at index 0");
        assertEquals("minecraft:max_health", context.rawArg(0).get());

        assertTrue(context.rawArg(1).isPresent(), "Expected argument at index 1");
        assertEquals("testkey:even_more_health", context.rawArg(1).get());

        // Assert: accessing out-of-bounds arguments
        assertFalse(context.rawArg(2).isPresent(), "Expected no argument at index 2");
    }
}
