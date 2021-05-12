package dev.dotmatthew.inferno.api.commands;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public interface Command {

    String getName();
    String[] getAliases();

    boolean execute();

}
