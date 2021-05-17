package dev.dotmatthew.brigadier.command;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

@Data
@Builder
public class CommandHolder {

    private final String label;
    private final String[] aliases;
    private final String parent;
    private final String description;
    private final String usage;

    private final Class<?> clazz;
    private final Method method;
    private final Object instance;

    private final List<SubCommandHolder> subCommands = new ArrayList<>();

    public final Method getMethod() {
        return getMethodAccessible();
    }

    private Method getMethodAccessible() {
        this.method.setAccessible(true);
        return this.method;
    }

}
