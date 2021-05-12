package dev.dotmatthew.inferno.api.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class OnlyOneParentException extends RuntimeException {

    public OnlyOneParentException(final @NotNull String message) {
        super(message);
    }

}
