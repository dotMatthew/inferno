package dev.dotmatthew.brigadier.parameter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */
@Data
@AllArgsConstructor
public class ParameterSet {

    private final String[] args;

}
