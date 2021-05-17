package dev.dotmatthew.brigadier.exceptions;

import org.jetbrains.annotations.NotNull;

import java.security.InvalidAlgorithmParameterException;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

public class ParameterException extends RuntimeException {

    public ParameterException(final @NotNull String message) {
        super(message);
    }

}
