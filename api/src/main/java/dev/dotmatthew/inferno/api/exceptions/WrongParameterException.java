package dev.dotmatthew.inferno.api.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class WrongParameterException extends RuntimeException {

    public WrongParameterException(final @NotNull String message) {
        super(message);
    }

}
