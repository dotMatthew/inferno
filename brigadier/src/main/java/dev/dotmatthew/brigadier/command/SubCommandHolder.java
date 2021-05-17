package dev.dotmatthew.brigadier.command;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author Mathias Dollenbacher <hello@mdollenbacher.net>
 * @since 17.05.21
 */

@Data
@Builder
public class SubCommandHolder {

    private final String label;
    private final String[] aliases;
    private final String parent;
    private final String description;
    private final String usage;

    private final Class<?> clazz;
    private final Method method;
    private final Object instance;

    public final Method getMethod() {
        return getMethodAccessible();
    }

    private Method getMethodAccessible() {
        this.method.setAccessible(true);
        return this.method;
    }

}
