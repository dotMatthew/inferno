package dev.dotmatthew.brigadier.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class CommandNotFoundException extends RuntimeException {

    public CommandNotFoundException(final @NotNull String message) {
        super(message);
    }

}
