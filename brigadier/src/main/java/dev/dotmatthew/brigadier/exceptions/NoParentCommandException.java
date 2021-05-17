package dev.dotmatthew.brigadier.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class NoParentCommandException extends RuntimeException {

    public NoParentCommandException(final @NotNull String message) {
        super(message);
    }

}
