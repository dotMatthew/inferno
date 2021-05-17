package dev.dotmatthew.brigadier.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class RegisterCommandException extends RuntimeException {

    public RegisterCommandException(final @NotNull String message) {
        super(message);
    }

}
