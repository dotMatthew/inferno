package dev.dotmatthew.brigadier.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class MethodIsNoCommandException extends RuntimeException {

    public MethodIsNoCommandException(final @NotNull String message) {
        super(message);
    }

}
