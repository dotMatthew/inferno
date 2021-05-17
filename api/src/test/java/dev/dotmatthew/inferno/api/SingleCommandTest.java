package dev.dotmatthew.inferno.api;

import dev.dotmatthew.inferno.api.commands.Command;
import dev.dotmatthew.inferno.api.parameters.ParameterSet;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class SingleCommandTest {

    @Command(label = "dosomething")
    public void doSomething(final @NotNull ParameterSet set) {
        System.out.println("This is a parent command");
    }

    @Command(label = "test", parent = "dosomething")
    public void doSomethingSubCommand(final @NotNull ParameterSet set) {
        System.out.println("This is a subcommand and i got executed!");
    }

}
