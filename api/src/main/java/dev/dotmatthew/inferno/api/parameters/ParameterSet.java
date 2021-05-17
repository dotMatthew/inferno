package dev.dotmatthew.inferno.api.parameters;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 12.05.21
 */

public class ParameterSet {

    @Getter private final List<String> parameters = new ArrayList<>();

    public void addParameter(final @NotNull String parameter) {
        this.parameters.add(parameter);
    }

    public void removeParameter(final @NotNull String parameter) {
        this.parameters.remove(parameter);
    }

    public boolean contains(final @NotNull String parameter) {
        return this.parameters.contains(parameter);
    }

    public int getLength() {
        return parameters.size();
    }

}
