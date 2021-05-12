package dev.dotmatthew.inferno.api;

import dev.dotmatthew.inferno.api.commands.Command;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class SingleCommandTest {

    @Command(label = "dosomething")
    public void doSomething() {
        System.out.println("This is a parent command");
    }

    @Command(label = "subcommand", parent = "dosomething")
    public void doSomethingSubCommand() {
        System.out.println("This is a subcommand");
    }

}
