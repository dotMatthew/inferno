package dev.dotmatthew.inferno.api;

import dev.dotmatthew.inferno.api.commands.CommandHandler;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class CommandTest {

    public static void main(String[] args) {

        final CommandHandler handler = new CommandHandler();

        handler.registerCommand(SingleCommandTest.class);

        handler.executeCommand("dosomething test");

        handler.unregisterCommand(SingleCommandTest.class);

    }

}
